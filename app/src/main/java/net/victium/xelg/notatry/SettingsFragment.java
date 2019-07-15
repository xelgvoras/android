package net.victium.xelg.notatry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.collection.ArrayMap;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import net.victium.xelg.notatry.utilities.PreferenceUtilities;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private String mVampire;
    private String mWerewolf;
    private String mWitch;
    private String mWitcher;
    private String mCharmer;
    private String mSorcerer;

    private Context mContext;

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = String.valueOf(value);

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

        mContext = getContext();
        mVampire = mContext.getString(R.string.pref_type_value_vampire);
        mWerewolf = mContext.getString(R.string.pref_type_value_werewolf);
        mWitch = mContext.getString(R.string.pref_type_value_witch);
        mWitcher = mContext.getString(R.string.pref_type_value_witcher);
        mCharmer = mContext.getString(R.string.pref_type_value_charmer);
        mSorcerer = mContext.getString(R.string.pref_type_value_sorcerer);

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

        if (key.equals(getString(R.string.pref_level_key))) {
            setupPersonalShieldLimit(mContext);
            setupAmuletLimit(mContext);
            setupAmuletInSeries(mContext);
            setupReactionNumber(mContext);
            setupNaturalMentalDefence(mContext);
        } else if (key.equals(getString(R.string.pref_type_key))) {
            setupReactionNumber(mContext);
            setupNaturalDefence(mContext);
            setupAmuletLimit(mContext);
            setupAmuletInSeries(mContext);
        } else if (key.equals(getString(R.string.pref_power_key))) {
            int power = Integer.parseInt(sharedPreferences.getString(key, ""));
            PreferenceUtilities.setCurrentMagicPower(mContext, power);
            setupNaturalDefence(mContext);
            setupDuskLimit(mContext);
            setupDuskSummary(mContext);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast errorMessage = Toast.makeText(getContext(), "Вводить можно только положительные целые числа", Toast.LENGTH_LONG);

        // COMPLETED(5) Добавить проверку ввода значений, выходящих за предел типа
        // COMPLETED(6) Добавить проверку ввода минимальных и максимальных значений запаса силы от уровня игрока
        // TODO(9) Рефакторинг проверки вводимых значений
        // - поправить текст ошибки
        // - при изменении возраста, проверяет указанный размер силы (исправить)

        String ageKey = getString(R.string.pref_age_key);
        String powerKey = getString(R.string.pref_power_key);
        String levelKey = getString(R.string.pref_level_key);
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

    private void setupPersonalShieldLimit(Context context) {

        int level = PreferenceUtilities.getCharacterLevel(context);
        int limit;

        if (level >= 6) {
            limit = 1;
        } else if (level >= 4) {
            limit = 2;
        } else if (level >= 2) {
            limit = 3;
        } else {
            limit = 4;
        }

        PreferenceUtilities.setPersonalShieldLimit(context, limit);
    }

    private void setupAmuletLimit(Context context) {

        int level = PreferenceUtilities.getCharacterLevel(context);
        String type = PreferenceUtilities.getCharacterType(context);
        int limit;

        if (level >= 5) {
            limit = 1;
        } else if (level >= 3) {
            limit = 2;
        } else if (level >= 1) {
            limit = 3;
        } else {
            limit = 4;
        }

        if (type.equals(mVampire) || type.equals(mWerewolf)) {
            limit = 0;
        } else if (type.equals(mWitch) || type.equals(mWitcher) || type.equals(mCharmer) || type.equals(mSorcerer)) {
            limit++;
        }

        PreferenceUtilities.setAmuletLimit(context, limit);
    }

    private void setupAmuletInSeries(Context context) {
        int level = PreferenceUtilities.getCharacterLevel(context);
        String type = PreferenceUtilities.getCharacterType(context);
        int limit;

        if (level >= 3) {
            limit = 1;
        } else if (level >= 1) {
            limit = 2;
        } else {
            limit = 3;
        }

        if (type.equals(mVampire) || type.equals(mWerewolf)) {
            limit = 0;
        } else if (type.equals(mWitch) || type.equals(mWitcher) || type.equals(mCharmer) || type.equals(mSorcerer)) {
            limit++;
        }

        PreferenceUtilities.setAmuletInSeries(context, limit);
    }

    private void setupReactionNumber(Context context) {

        int level = PreferenceUtilities.getCharacterLevel(context);
        int limit = 1;

        if (PreferenceUtilities.isCharacterVop(context)) {

            if (level >= 5) {
                limit = 2;
            } else if (level >= 3) {
                limit = 3;
            } else if (level >= 1) {
                limit = 4;
            } else {
                limit = 5;
            }
        }

        PreferenceUtilities.setReactionsNumber(context, limit);
    }

    private void setupNaturalMentalDefence(Context context) {

        int level = PreferenceUtilities.getCharacterLevel(context);
        int defence;

        if (level >= 6) {
            defence = 0;
        } else if (level >= 3) {
            defence = 1;
        } else if (level >= 1) {
            defence = 2;
        } else {
            defence = 3;
        }

        PreferenceUtilities.setNaturalMentalDefence(context, defence);
        PreferenceUtilities.setCurrentNaturalMentalDefence(context, defence);
    }

    private void setupNaturalDefence(Context context) {

        int defence = 0;

        if (PreferenceUtilities.isCharacterVop(context)) {
            defence = PreferenceUtilities.getCurrentMagicPower(context);
        }

        PreferenceUtilities.setNaturalDefence(context, defence);
    }

    private void setupDuskLimit(Context context) {

        int limit = 1;
        int power = PreferenceUtilities.getMagicPowerLimit(context);

        if (power > 512) {
            limit = 4;
        } else if (power > 128) {
            limit = 3;
        } else if (power > 32) {
            limit = 2;
        }

        PreferenceUtilities.setDuskLimit(context, limit);
    }

    private void setupDuskSummary(Context context) {

        int power = PreferenceUtilities.getMagicPowerLimit(context);
        int roundsLow;
        try {
            roundsLow = (int) Math.ceil((10 * power) / (32 - power));
            if (roundsLow < 0) roundsLow = 100;
        } catch (ArithmeticException e) {
            roundsLow = 100;
        }
        int roundsMedium;
        try {
            roundsMedium = (int) Math.ceil((10 * power) / (128 - power));
            if (roundsMedium < 0) roundsMedium = 100;
        } catch (ArithmeticException e) {
            roundsMedium = 100;
        }
        int roundsHeight;
        try {
            roundsHeight = (int) Math.ceil((10 * power) / (512 - power));
            if (roundsHeight < 0) roundsHeight = 100;
        } catch (ArithmeticException e) {
            roundsHeight = 100;
        }

        ArrayMap<String, Integer> duskSummary = new ArrayMap<>();
        duskSummary.put("layout-1", roundsLow);
        duskSummary.put("layout-2", roundsMedium);
        duskSummary.put("layout-3", roundsHeight);
        duskSummary.put("layout-4", roundsHeight);
        duskSummary.put("layout-5", roundsMedium);
        duskSummary.put("layout-6", roundsLow);

        PreferenceUtilities.setDuskSummary(context, duskSummary);
    }
}
