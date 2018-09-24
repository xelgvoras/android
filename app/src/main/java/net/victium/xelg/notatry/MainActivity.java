package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import net.victium.xelg.notatry.adapter.DuskLayersAdapter;
import net.victium.xelg.notatry.data.CharacterPreferences;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.data.Character;

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

    private SQLiteDatabase mDb;
    private Cursor mCharacterStatusCursor;
    private Cursor mDuskLayersCursor;
    private Character mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFullNameTextView = findViewById(R.id.tv_character_full_name);
        mPersonalInfoTextView = findViewById(R.id.tv_character_personal_info);
        mMagicPowerTextView = findViewById(R.id.tv_character_magic_power);
        mDefenceTextView = findViewById(R.id.tv_character_defence);
        // TODO(1) Сделать раскрывающееся текстовое поле
        mCharacterDetailsTextView = findViewById(R.id.tv_character_details);
        mDuskLayersRecyclerView = findViewById(R.id.rv_dusk_layers);
        mShieldsButton = findViewById(R.id.b_shields);
        mBattleButton = findViewById(R.id.b_battle);

        mDuskLayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mShieldsButton.setOnClickListener(this);
        mBattleButton.setOnClickListener(this);

        mCharacter = new Character(this);

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);
        mDb = notATryDbHelper.getWritableDatabase();

        // TODO(12) Добавить механизм импорта/экспорта персонажа из файла описания
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
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, getCharacterStatus()));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(getCharacterStatus(), getCharacterDefence()));
        mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(getCharacterStatus(), getDuskLayersCursor()));
    }

    private void collectCharacterStatusIntoDb(Character character) {

        mCharacterStatusCursor = swapCursor(mCharacterStatusCursor, getCharacterStatus());

        if (mCharacterStatusCursor.moveToFirst()) {
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

        mDb.insert(NotATryContract.CharacterStatusEntry.TABLE_NAME, null, contentValues);
    }

    private void updateCharacterStatus(ContentValues contentValues){

        mCharacterStatusCursor = swapCursor(mCharacterStatusCursor, getCharacterStatus());

        mCharacterStatusCursor.moveToFirst();
        long id = mCharacterStatusCursor.getLong(mCharacterStatusCursor.getColumnIndex(NotATryContract.CharacterStatusEntry._ID));

        mDb.update(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                contentValues,
                NotATryContract.CharacterStatusEntry._ID + "=" + id,
                null
        );
    }

    private void setupDuskLayers(Character character) {

        boolean isTableEmpty = true;

        mDuskLayersCursor = swapCursor(mDuskLayersCursor, getDuskLayersCursor());

        if (mDuskLayersCursor.moveToFirst()) {
            isTableEmpty = false;
        }

        ContentValues contentValues = new ContentValues();

        int[] stsForLayers = new int[]{32,128,512,512,128,32};
        int powerLimit = character.getCharacterPowerLimit();
        int duskLayer = 1;

        for (int sts : stsForLayers) {
            Integer rounds;

            try {
                rounds = Math.round((float)(10 * powerLimit / (sts - powerLimit)));
            } catch (ArithmeticException ax) {
                rounds = 999;
            }

            if (rounds < 0) {
                rounds = 999;
            } else if (rounds == 0) {
                rounds = null;
            }

            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER, duskLayer);
            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS, rounds);

            if (isTableEmpty) {
                mDb.insert(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                        null,
                        contentValues);
            } else {
                mDb.update(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                        contentValues,
                        NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER + "=?",
                        new String[]{String.valueOf(duskLayer)});
            }

            duskLayer++;
        }
    }

    private Cursor getDuskLayersCursor() {

        return mDb.query(
                NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER
        );
    }

    private Cursor getCharacterStatus() {

        return mDb.query(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getCharacterDefence() {

        String[] defenceSummary = new String[]{
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };

        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                defenceSummary,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor swapCursor(Cursor oldCursor, Cursor newCursor) {

        if (null != oldCursor) {
            oldCursor.close();
        }

        return newCursor;
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
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, getCharacterStatus()));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(getCharacterStatus(), getCharacterDefence()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        mCharacterStatusCursor.close();
        mDuskLayersCursor.close();
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
            mCharacter.setCharacterPowerLimit(sharedPreferences);

            contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT,
                    mCharacter.getCharacterPowerLimit());
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT,
                    mCharacter.getCharacterDuskLayerLimit());

            Cursor cursor = getCharacterStatus();
            cursor.moveToFirst();
            int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
            int powerLimit = mCharacter.getCharacterPowerLimit();

            if (currentPower > powerLimit) {
                contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, powerLimit);
            }

            updateCharacterStatus(contentValues);

            setupDuskLayers(mCharacter);
        }

        setupSharedPreferences();
    }
}
