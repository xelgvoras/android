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

        mCharacterStatusCursor = getCharacterStatus();
        collectCharacterStatusIntoDb(mCharacterStatusCursor, mCharacter);

        mDuskLayersCursor = getDuskLayersCursor();
        setupDuskLayers(mDuskLayersCursor, mCharacter);

        setupSharedPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mDuskLayersRecyclerView.setAdapter(new DuskLayersAdapter(this));
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(this));
        mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this, mCharacterStatusCursor));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(this));
        mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this, mCharacterStatusCursor, mDuskLayersCursor));
    }

    private void collectCharacterStatusIntoDb(Cursor cursor, Character character) {

        if (cursor.moveToFirst()) {
            /* На этапе разработки, все время очищает таблицу при запуске, в дальнейшем,
            * после проверки, просто прерывать выполнение кода. */
            mDb.delete(NotATryContract.CharacterStatusEntry.TABLE_NAME, null, null);
//            return;
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

        swapCursor(mCharacterStatusCursor, getCharacterStatus());
    }

    private void setupDuskLayers(Cursor cursor, Character character) {

        if (cursor.moveToFirst()) {
            /* На этапе разработки, все время очищает таблицу при запуске, в дальнейшем,
             * после проверки, просто прерывать выполнение кода. */
            mDb.delete(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME, null, null);
//            return;
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

            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER, duskLayer++);
            contentValues.put(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS, rounds);

            mDb.insert(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                    null,
                    contentValues);
        }

        swapCursor(mDuskLayersCursor, getDuskLayersCursor());
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

    private void swapCursor(Cursor oldCursor, Cursor newCursor) {

        if (null != oldCursor) {
            oldCursor.close();
            oldCursor = newCursor;
        }
    }

    private void updateCharacterLimits(Cursor cursor) {
        int idCol = cursor.getColumnIndex(NotATryContract.CharacterStatusEntry._ID);
        String id = cursor.getString(idCol);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ContentValues contentValues = new ContentValues();

        String levelKey = getString(R.string.pref_level_key);
        String levelDefault = getString(R.string.pref_level_value_seven);
        String levelString = sharedPreferences.getString(levelKey, levelDefault);
        int level = Integer.parseInt(levelString);
        int shieldsLimit = 1;
        int amuletsLimit = 1;

        if (level >= 5) {
            shieldsLimit = 1;
            amuletsLimit = 1;
        } else if (level >= 3) {
            shieldsLimit = 2;
            amuletsLimit = 1;
        } else if (level >= 1) {
            shieldsLimit = 3;
            amuletsLimit = 2;
        } else {
            shieldsLimit = 4;
            amuletsLimit = 3;
        }

        String powerKey = getString(R.string.pref_power_key);
        String powerDefault = getString(R.string.pref_power_default);
        String powerString = sharedPreferences.getString(powerKey, powerDefault);
        int power = Integer.parseInt(powerString);

        int depthLimit = 1;
        if (power > 512) {
            depthLimit = 6;
        } else if (power > 128) {
            depthLimit = 3;
        } else if (power > 32) {
            depthLimit = 2;
        }

        ArrayList<String> typeArray = new ArrayList<>();
        typeArray.add(getString(R.string.pref_type_value_flipflop));
        typeArray.add(getString(R.string.pref_type_value_vampire));
        typeArray.add(getString(R.string.pref_type_value_werewolf));
        typeArray.add(getString(R.string.pref_type_value_werewolf_mag));

        String typeKey = getString(R.string.pref_type_key);
        String typeDefault = getString(R.string.pref_type_value_mag);
        String typeString = sharedPreferences.getString(typeKey, typeDefault);
        int reactionsNumber = 1;
        if (typeArray.contains(typeString)) {

            if (level >= 5) {
                reactionsNumber = 2;
            } else if (level >= 3) {
                reactionsNumber = 3;
            } else if (level >= 1) {
                reactionsNumber = 4;
            } else {
                reactionsNumber = 5;
            }

            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, reactionsNumber);
        }

        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT, shieldsLimit);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT, amuletsLimit);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT, depthLimit);

        mDb.update(NotATryContract.CharacterStatusEntry.TABLE_NAME,
                contentValues,
                "_id=?",
                new String[]{id});

        mCharacterStatusCursor = getCharacterStatus();
    }

    private void updateDuskLayersSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String powerLimitKey = getString(R.string.pref_power_key);
        String powerLimitDefault = getString(R.string.pref_power_default);
        String powerLimitString = sharedPreferences.getString(powerLimitKey, powerLimitDefault);
        int powerLimit = Integer.parseInt(powerLimitString);

        int[] stsForLayers = new int[]{32,128,512,512,128,32};

        ContentValues contentValues = new ContentValues();
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

            mDb.update(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                    contentValues,
                    NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER + "=?",
                    new String[]{String.valueOf(duskLayer)});

            duskLayer++;
        }

        mDuskLayersCursor = getDuskLayersCursor();
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
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this, getCharacterStatus()));
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
        if (key.equals(getString(R.string.pref_full_name_key)) ||
                key.equals(getString(R.string.pref_age_key))) {
            mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(this));
        } else if (key.equals(getString(R.string.pref_side_key)) ||
                key.equals(getString(R.string.pref_type_key))) {
            mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
        } else if (key.equals(getString(R.string.pref_power_key))) {
            updateDuskLayersSummary();
            updateCharacterLimits(mCharacterStatusCursor);
            mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this, mCharacterStatusCursor));
            mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this, mCharacterStatusCursor, mDuskLayersCursor));
        } else if (key.equals(getString(R.string.pref_level_key))) {
            updateCharacterLimits(mCharacterStatusCursor);
            mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
            mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this, mCharacterStatusCursor, mDuskLayersCursor));
        } else if (key.equals(getString(R.string.pref_type_key))) {
            mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this, mCharacterStatusCursor, mDuskLayersCursor));
        }
    }
}
