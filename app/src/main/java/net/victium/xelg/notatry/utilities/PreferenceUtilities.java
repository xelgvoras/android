package net.victium.xelg.notatry.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.collection.ArrayMap;
import androidx.preference.PreferenceManager;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;

public final class PreferenceUtilities {

    public static final String KEY_PERSONAL_SHIELD_LIMIT = "personal-shield-limit";
    public static final String KEY_CURRENT_MAGIC_POWER = "current-magic-power";
    public static final String KEY_NATURAL_DEFENCE = "natural-defence";
    public static final String KEY_NATURAL_MENTAL_DEFENCE = "natural-mental-defence";
    public static final String KEY_CURRENT_NATURAL_MENTAL_DEFENCE = "current-natural-mental-defence";
    public static final String KEY_DUSK_LIMIT = "dusk-limit";
    public static final String KEY_AMULET_LIMIT = "amulet-limit";
    public static final String KEY_AMULET_IN_SERIES = "amulet-in-series";
    public static final String KEY_REACTIONS_NUMBER = "reactions-number";
    public static final String KEY_BATTLE_FORM = "battle-form";

    private static final int DEFAULT_SHIELD_LIMIT = 1;
    private static final int DEFAULT_CURRENT_MAGIC_POWER = 8;
    private static final int DEFAULT_NATURAL_DEFENCE = 0;
    private static final int DEFAULT_NATURAL_MENTAL_DEFENCE = 1;
    private static final int DEFAULT_CURRENT_NATURAL_MENTAL_DEFENCE = 1;
    private static final int DEFAULT_DUSK_LIMIT = 1;
    private static final int DEFAULT_AMULET_LIMIT = 1;
    private static final int DEFAULT_AMULET_IN_SERIES = 1;
    private static final int DEFAULT_REACTIONS_NUMBER = 1;
    private static final String DEFAULT_BATTLE_FORM = "человек";

    public static int getPersonalShieldLimit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_PERSONAL_SHIELD_LIMIT, DEFAULT_SHIELD_LIMIT);
    }

    synchronized public static void setPersonalShieldLimit(Context context, int personalShieldLimit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_PERSONAL_SHIELD_LIMIT, personalShieldLimit);
        editor.apply();
    }

    public static String getCharacterName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                context.getString(R.string.pref_full_name_key),
                context.getString(R.string.pref_full_name_default)
        );
    }

    synchronized public static void setCharacterName(Context context, String s) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(
                context.getString(R.string.pref_full_name_key),
                s
        );
        editor.apply();
    }

    public static int getCharacterAge(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(prefs.getString(
                context.getString(R.string.pref_age_key),
                context.getString(R.string.pref_age_default)
        ));
    }

    public static String getCharacterSide(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                context.getString(R.string.pref_side_key),
                context.getString(R.string.pref_side_light_value)
        );
    }

    public static String getCharacterType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                context.getString(R.string.pref_type_key),
                context.getString(R.string.pref_type_value_mag)
        );
    }

    public static int getCharacterLevel(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(prefs.getString(
                context.getString(R.string.pref_level_key),
                context.getString(R.string.pref_level_value_seven)
        ));
    }

    public static int getCurrentMagicPower(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_CURRENT_MAGIC_POWER, DEFAULT_CURRENT_MAGIC_POWER);
    }

    synchronized public static void setCurrentMagicPower(Context context, int power) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_MAGIC_POWER, power);
        editor.apply();
    }

    public static int getMagicPowerLimit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(prefs.getString(
                context.getString(R.string.pref_power_key),
                context.getString(R.string.pref_power_default)
        ));
    }

    public static int getNaturalDefence(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_NATURAL_DEFENCE, DEFAULT_NATURAL_DEFENCE);
    }

    synchronized public static void setNaturalDefence(Context context, int defence) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NATURAL_DEFENCE, defence);
        editor.apply();
    }

    public static int getNaturalMentalDefence(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_NATURAL_MENTAL_DEFENCE, DEFAULT_NATURAL_MENTAL_DEFENCE);
    }

    synchronized public static void setNaturalMentalDefence(Context context, int defence) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NATURAL_MENTAL_DEFENCE, defence);
        editor.apply();
    }

    public static boolean isCharacterVop(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String type = prefs.getString(
                context.getString(R.string.pref_type_key),
                context.getString(R.string.pref_type_value_mag)
        );

        ArrayList<String> listOfVop = new ArrayList<>();
        listOfVop.add(context.getString(R.string.pref_type_value_flipflop));
        listOfVop.add(context.getString(R.string.pref_type_value_vampire));
        listOfVop.add(context.getString(R.string.pref_type_value_werewolf));
        listOfVop.add(context.getString(R.string.pref_type_value_werewolf_mag));

        return listOfVop.contains(type);
    }

    public static int getCurrentNaturalMentalDefence(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_CURRENT_NATURAL_MENTAL_DEFENCE, DEFAULT_CURRENT_NATURAL_MENTAL_DEFENCE);
    }

    synchronized public static void setCurrentNaturalMentalDefence(Context context, int defence) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_NATURAL_MENTAL_DEFENCE, defence);
        editor.apply();
    }

    public static int getDuskLimit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_DUSK_LIMIT, DEFAULT_DUSK_LIMIT);
    }

    synchronized public static void setDuskLimit(Context context, int limit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_DUSK_LIMIT, limit);
        editor.apply();
    }

    public static int getAmuletLimit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_AMULET_LIMIT, DEFAULT_AMULET_LIMIT);
    }

    synchronized public static void setAmuletLimit(Context context, int limit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_AMULET_LIMIT, limit);
        editor.apply();
    }

    public static int getAmuletInSeries(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_AMULET_IN_SERIES, DEFAULT_AMULET_IN_SERIES);
    }

    synchronized public static void setAmuletInSeries(Context context, int limit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_AMULET_IN_SERIES, limit);
        editor.apply();
    }

    public static int getReactionsNumber(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(KEY_REACTIONS_NUMBER, DEFAULT_REACTIONS_NUMBER);
    }

    synchronized public static void setReactionsNumber(Context context, int limit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_REACTIONS_NUMBER, limit);
        editor.apply();
    }

    public static String getBattleForm(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(KEY_BATTLE_FORM, DEFAULT_BATTLE_FORM);
    }

    synchronized public static void setBattleForm(Context context, String s) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BATTLE_FORM, s);
        editor.apply();
    }

    public static ArrayMap<String, Integer> getDuskSummary(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayMap<String, Integer> duskSummary = new ArrayMap<>();

        for (int i=1; i<7; i++) {
            String key = "layout-" + i;
            int rounds = prefs.getInt(key, 0);
            duskSummary.put(key, rounds);
        }

        return duskSummary;
    }

    synchronized public static void setDuskSummary(Context context, ArrayMap<String, Integer> summary) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        for (int i=0; i<summary.size(); i++) {
            String key = summary.keyAt(i);
            int value = summary.valueAt(i);
            editor.putInt(key,value);
        }
        editor.apply();
    }
}
