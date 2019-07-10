package net.victium.xelg.notatry.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;

public final class PreferenceUtilities {

    public static final String KEY_PERSONAL_SHIELD_LIMIT = "personal-shield-limit";
    public static final String KEY_CURRENT_MAGIC_POWER = "current-magic-power";
    public static final String KEY_NATURAL_DEFENCE = "natural-defence";
    public static final String KEY_NATURAL_MENTAL_DEFENCE = "natural-mental-defence";
    public static final String KEY_CURRENT_NATURAL_MENTAL_DEFENCE = "current-natural-mental-defence";

    private static final int DEFAULT_SHIELD_LIMIT = 1;
    private static final int DEFAULT_CURRENT_MAGIC_POWER = 8;
    private static final int DEFAULT_NATURAL_DEFENCE = 0;
    private static final int DEFAULT_NATURAL_MENTAL_DEFENCE = 1;
    private static final int DEFAULT_CURRENT_NATURAL_MENTAL_DEFENCE = 1;

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

    synchronized public static void setCurrentMagicPower(Context context, int magicPower) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_MAGIC_POWER, magicPower);
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
}
