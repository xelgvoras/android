package net.victium.xelg.notatry.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class PreferenceUtilities {

    public static final String KEY_PERSONAL_SHIELD_LIMIT = "personal-shield-limit";
    public static final String KEY_PREFERENCE_POWER = "power";

    private static final int DEFAULT_SHIELD_LIMIT = 1;
    private static final int DEFAULT_PREFERENCE_POWER = 8;

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
}
