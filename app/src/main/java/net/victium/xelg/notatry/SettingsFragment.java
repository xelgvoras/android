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

        //TODO(5) Добавить проверку ввода значений, выходящих за предел типа
        //TODO(6) Добавить проверку ввода минимальных и максимальных значений запаса силы от уровня игрока

        String ageKey = getString(R.string.pref_age_key);
        String powerKey = getString(R.string.pref_power_key);
        String prefKey = preference.getKey();

        if (prefKey.equals(ageKey) || prefKey.equals(powerKey)) {
            String value = String.valueOf(newValue);
            try {
                int intValue = Integer.parseInt(value);

                if (intValue < 0) {
                    errorMessage.show();
                    return false;
                }
            } catch (Exception e) {
                errorMessage.show();
                return false;
            }
        }

        return true;
    }
}
