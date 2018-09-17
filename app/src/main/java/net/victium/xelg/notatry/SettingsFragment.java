package net.victium.xelg.notatry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = String.valueOf(value);
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }

        Preference[] preferences = {
                findPreference(getString(R.string.pref_age_key)),
                findPreference(getString(R.string.pref_power_key))
        };

        for (Preference p : preferences) {
            p.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (null != preference) {
            setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast errorMessage = Toast.makeText(getContext(), "Вводить можно только положительные целые числа", Toast.LENGTH_LONG);

        //COMPLETED(5) Добавить проверку ввода значений, выходящих за предел типа
        //COMPLETED(6) Добавить проверку ввода минимальных и максимальных значений запаса силы от уровня игрока

        String ageKey = getString(R.string.pref_age_key);
        String powerKey = getString(R.string.pref_power_key);
        String prefKey = preference.getKey();

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        int currentLevel = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_level_key),
                getString(R.string.pref_level_value_seven)));
        int minPower = 1;
        int maxPower = 7;

        switch (currentLevel) {
            case 7:
                minPower = 1;
                maxPower = 7;
                break;
            case 6:
                minPower = 4;
                maxPower = 15;
                break;
            case 5:
                minPower = 8;
                maxPower = 31;
                break;
            case 4:
                minPower = 16;
                maxPower = 63;
                break;
            case 3:
                minPower = 32;
                maxPower = 127;
                break;
            case 2:
                minPower = 64;
                maxPower = 255;
                break;
            case 1:
                minPower = 128;
                maxPower = 511;
                break;
            case 0:
                minPower = 512;
                maxPower = 2048;
                break;
        }

        if (prefKey.equals(ageKey) || prefKey.equals(powerKey)) {
            String value = String.valueOf(newValue);
            try {
                int intValue = Integer.parseInt(value);

                if (intValue < 0) {
                    errorMessage.show();
                    return false;
                }

                if (intValue < minPower || intValue > maxPower) {
                    Toast.makeText(getContext(), "Указано значение силы вне границы уровня: " + minPower + "-" + maxPower, Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                Toast.makeText(getContext(), "Слишком большое число", Toast.LENGTH_LONG).show();
                return false;
            } catch (Exception e) {
                errorMessage.show();
                return false;
            }
        }

        return true;
    }
}
