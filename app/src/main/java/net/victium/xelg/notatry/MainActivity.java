package net.victium.xelg.notatry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import net.victium.xelg.notatry.data.NotATryPreferences;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    TextView mPersonalInfoTextView;

    String playerName;
    String playerAge;
    boolean  playerSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPersonalInfoTextView = findViewById(R.id.tv_personal_info);

        playerName = NotATryPreferences.getPreferredPlayerName(this);
        playerAge = NotATryPreferences.getPreferredPlayerAge(this);
        playerSide = NotATryPreferences.isLightSide(this);
        setupPersonalPreferences();
    }

    private void setupPersonalPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String personalInfoSummary;

        String playerSideString;
        if (playerSide) {
            playerSideString = "светлый";
        } else {
            playerSideString = "темный";
        }

        personalInfoSummary = String.format("%s, возраст: %s, %s иной", playerName, playerAge, playerSideString);
        mPersonalInfoTextView.setText(personalInfoSummary);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (R.id.action_settings == id) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_full_name_key))) {
            playerName = sharedPreferences.getString(key, getString(R.string.pref_full_name_default));
        } else if (key.equals(getString(R.string.pref_age_key))) {
            playerAge = sharedPreferences.getString(key, getString(R.string.pref_age_default));
        } else if (key.equals(getString(R.string.pref_side_key))) {
            String playerSideString = sharedPreferences.getString(key, getString(R.string.pref_side_label_light));
            if (playerSideString.equals(getString(R.string.pref_side_light_value))) {
                playerSide = true;
            } else {
                playerSide = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
