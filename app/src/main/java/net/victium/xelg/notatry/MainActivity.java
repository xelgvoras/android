package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.victium.xelg.notatry.data.CharacterPreferences;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    TextView mFullNameTextView;
    TextView mPersonalInfoTextView;
    TextView mMagicPowerTextView;
    TextView mDefenceTextView;
    TextView mCharacterDetailsTextView;
    Button mShieldsButton;
    Button mBattleButton;

    private SQLiteDatabase mDb;
    private Cursor mCursor;

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
        mShieldsButton = findViewById(R.id.b_shields);
        mBattleButton = findViewById(R.id.b_battle);

        mShieldsButton.setOnClickListener(this);
        mBattleButton.setOnClickListener(this);

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);
        mDb = notATryDbHelper.getWritableDatabase();

        mCursor = getCharacterStatus();
        collectCharacterStatusIntoDb(mCursor);

        setupSharedPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(this));
        mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this, mCursor));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(this));
        mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this, mCursor));
    }

    private void collectCharacterStatusIntoDb(Cursor cursor) {
        if (cursor.moveToFirst()) {
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String powerKey = getString(R.string.pref_power_key);
        String powerDefault = getString(R.string.pref_power_default);
        String powerString = sharedPreferences.getString(powerKey, powerDefault);
        int power = Integer.parseInt(powerString);

        int currentDepth = 0;
        int depthLimit = 3;
        int currentShields = 0;

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

        ArrayList<String> typeArray = new ArrayList<>();
        typeArray.add(getString(R.string.pref_type_value_flipflop));
        typeArray.add(getString(R.string.pref_type_value_vampire));
        typeArray.add(getString(R.string.pref_type_value_werewolf));
        typeArray.add(getString(R.string.pref_type_value_werewolf_mag));

        String typeKey = getString(R.string.pref_type_key);
        String typeDefault = getString(R.string.pref_type_value_mag);
        String typeString = sharedPreferences.getString(typeKey, typeDefault);
        int naturalDefence = 0;
        int reactionsNumber = 1;
        if (typeArray.contains(typeString)) {
            naturalDefence = power;

            if (level >= 5) {
                reactionsNumber = 2;
            } else if (level >= 3) {
                reactionsNumber = 3;
            } else if (level >= 1) {
                reactionsNumber = 4;
            } else {
                reactionsNumber = 5;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_POWER, power);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_DEPTH, currentDepth);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT, depthLimit);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_SHIELDS, currentShields);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT, shieldsLimit);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT, amuletsLimit);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, reactionsNumber);

        mDb.insert(NotATryContract.CharacterStatusEntry.TABLE_NAME, null, contentValues);
        mCursor = getCharacterStatus();
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
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
                key.equals(getString(R.string.pref_type_key)) ||
                key.equals(getString(R.string.pref_level_key))) {
            mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
        } else if (key.equals(getString(R.string.pref_power_key))) {
            mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this, mCursor));
        }
    }
}
