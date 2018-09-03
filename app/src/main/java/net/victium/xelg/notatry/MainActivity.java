package net.victium.xelg.notatry;

import android.content.Intent;
import android.content.SharedPreferences;
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

        setupSharedPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(this));
        mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfoFromPreferences(this));
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this));
        mDefenceTextView.setText(CharacterPreferences.getCharacterDefence(this));
        mCharacterDetailsTextView.setText(CharacterPreferences.getCharacterDetails(this));
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
            mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(this));
        }
    }
}
