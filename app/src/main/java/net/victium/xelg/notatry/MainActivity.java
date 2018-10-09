package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.adapter.DuskLayersAdapter;
import net.victium.xelg.notatry.data.CharacterPreferences;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.Character;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    TextView mFullNameTextView;
    TextView mPersonalInfoTextView;
    TextView mMagicPowerTextView;
    TextView mDefenceTextView;
    TextView mCharacterDetailsTextView;
    RecyclerView mDuskLayersRecyclerView;
    Button mShieldsButton;
    Button mBattleButton;

    private Character mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFullNameTextView = findViewById(R.id.tv_character_full_name);
        mPersonalInfoTextView = findViewById(R.id.tv_character_personal_info);
        mMagicPowerTextView = findViewById(R.id.tv_character_magic_power);
        mDefenceTextView = findViewById(R.id.tv_character_defence);
        mCharacterDetailsTextView = findViewById(R.id.tv_character_details);
        mDuskLayersRecyclerView = findViewById(R.id.rv_dusk_layers);
        mShieldsButton = findViewById(R.id.b_shields);
        mBattleButton = findViewById(R.id.b_battle);

        mDuskLayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mShieldsButton.setOnClickListener(this);
        mBattleButton.setOnClickListener(this);

        mCharacter = new Character(this);

        // COMPLETED(12) Добавить механизм импорта/экспорта персонажа из файла описания
        // TODO(14) Добавить логирование действий и вывод журнала логов
        collectCharacterStatusIntoDb(mCharacter);
        setupDuskLayers(mCharacter);

        setupSharedPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mDuskLayersRecyclerView.setAdapter(new DuskLayersAdapter(this));
    }

    private void setupSharedPreferences() {

        mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(mCharacter));
        mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfo(mCharacter));
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, getCharacterStatusCursor()));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(getCharacterStatusCursor(), getCharacterDefence()));
        mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(getCharacterStatusCursor(), getDuskLayersCursor()));
    }

    private void collectCharacterStatusIntoDb(Character character) {

        Cursor cursor = getCharacterStatusCursor();

        if (cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, character.getCharacterPowerLimit());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT, character.getCharacterPowerLimit());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_DEPTH, 0);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT, character.getCharacterDuskLayerLimit());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_SHIELDS, 0);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT, character.getCharacterPersonalShieldsLimit());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT, character.getCharacterAmuletsLimit());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, character.getCharacterNaturalDefence());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, character.getCharacterReactionsNumber());
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_BATTLE_FORM, "человек");

        Uri uri = getContentResolver().insert(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            String message = "Персонаж настроен:\n";
            Toast.makeText(this, message + uri.toString(), Toast.LENGTH_LONG).show();
        }

        cursor.close();
    }

    private void updateCharacterStatus(ContentValues contentValues){

        int updatedRows = getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                contentValues, null, null);

        if (updatedRows == 0) {
            Toast.makeText(this, "Не удалось обновить информацию о персонаже в БД", Toast.LENGTH_LONG).show();
        }
    }

    private void setupDuskLayers(Character character) {

        Cursor cursor = getDuskLayersCursor();

        if (cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        ContentValues contentValues = new ContentValues();

        float[] stsForLayers = new float[]{32f,128f,512f,512f,128f,32f};
        float powerLimit = (float) character.getCharacterPowerLimit();
        int duskLayer = 1;

        for (float sts : stsForLayers) {
            double rounds;

            try {
                rounds = Math.ceil((10f * powerLimit / (sts - powerLimit)));
            } catch (ArithmeticException ax) {
                rounds = 999;
            }

            if (rounds < 0 || Double.isInfinite(rounds)) {
                rounds = 999;
            }

            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER, duskLayer++);
            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS, rounds);

            getContentResolver().insert(NotATryContract.DuskLayersSummaryEntry.CONTENT_URI, contentValues);
        }

        cursor.close();
    }

    private void updateDuskLayers(Character character) {

        getContentResolver().delete(NotATryContract.DuskLayersSummaryEntry.CONTENT_URI,
                null, null);

        setupDuskLayers(character);
    }

    private Cursor getDuskLayersCursor() {

        return getContentResolver().query(NotATryContract.DuskLayersSummaryEntry.CONTENT_URI,
                null,
                null,
                null,
                NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER);
    }

    private Cursor getCharacterStatusCursor() {

        return getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private Cursor getCharacterDefence() {

        String[] defenceSummary = new String[]{
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };

        return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                defenceSummary, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_import_export) {
            Intent intent = new Intent(this, ImportExportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getCharacterStatusCursor();

        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, cursor));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(cursor, getCharacterDefence()));

        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button clickedButton = (Button) v;
            int buttonId = clickedButton.getId();

            if (buttonId == R.id.b_shields) {
                Intent intent = new Intent(this, ShieldsActivity.class);
                startActivity(intent);
            } else if (buttonId == R.id.b_battle) {
                Intent intent = new Intent(this, BattleActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // COMPLETED(bug) При изменении типа персонажа или резерва, обновлять размер естественной защиты

        ContentValues contentValues;

        if (key.equals(getString(R.string.pref_full_name_key))) {
            mCharacter.setCharacterName(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_age_key))) {
            mCharacter.setCharacterAge(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_side_key))) {
            mCharacter.setCharacterSide(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_type_key))) {
            mCharacter.setCharacterType(sharedPreferences);

            contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE,
                    mCharacter.getCharacterNaturalDefence());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER,
                    mCharacter.getCharacterReactionsNumber());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT,
                    mCharacter.getCharacterAmuletsLimit());

            updateCharacterStatus(contentValues);

        } else if (key.equals(getString(R.string.pref_level_key))) {
            mCharacter.setCharacterLevel(sharedPreferences);

            contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT,
                    mCharacter.getCharacterPersonalShieldsLimit());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT,
                    mCharacter.getCharacterAmuletsLimit());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER,
                    mCharacter.getCharacterReactionsNumber());

            updateCharacterStatus(contentValues);

        } else if (key.equals(getString(R.string.pref_power_key))) {
            ArrayList<String> listOfVops = new ArrayList<>();
            listOfVops.add(getString(R.string.pref_type_value_flipflop));
            listOfVops.add(getString(R.string.pref_type_value_vampire));
            listOfVops.add(getString(R.string.pref_type_value_werewolf));
            listOfVops.add(getString(R.string.pref_type_value_werewolf_mag));

            mCharacter.setCharacterPowerLimit(sharedPreferences);

            contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT,
                    mCharacter.getCharacterPowerLimit());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT,
                    mCharacter.getCharacterDuskLayerLimit());

            Cursor cursor = getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                    null, null, null, null);
            cursor.moveToFirst();
            int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
            int powerLimit = mCharacter.getCharacterPowerLimit();

            if (currentPower > powerLimit) {
                contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, powerLimit);
                if (listOfVops.contains(mCharacter.getCharacterType())) {
                    contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, powerLimit);
                }
            }

            updateCharacterStatus(contentValues);
            updateDuskLayers(mCharacter);

            cursor.close();
        }

        setupSharedPreferences();
    }
}
