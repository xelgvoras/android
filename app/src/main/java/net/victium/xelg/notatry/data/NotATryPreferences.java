package net.victium.xelg.notatry.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import net.victium.xelg.notatry.R;

public class NotATryPreferences {

    public static String getPreferredPlayerName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForPlayerName = context.getString(R.string.pref_full_name_key);
        String defaultName = context.getString(R.string.pref_full_name_default);
        return sharedPreferences.getString(keyForPlayerName, defaultName);
    }

    public static String getPreferredPlayerAge(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForPlayerAge = context.getString(R.string.pref_age_key);
        String defaultAge = context.getString(R.string.pref_age_default);
        String preferredAge = sharedPreferences.getString(keyForPlayerAge, defaultAge);

        return preferredAge;
    }

    public static boolean isLightSide(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSide = context.getString(R.string.pref_side_key);
        String defaultSide = context.getString(R.string.pref_side_light_value);
        String preferredSide = sharedPreferences.getString(keyForSide, defaultSide);
        String light = context.getString(R.string.pref_side_light_value);
        boolean userPrefersSide;

        if (light.equals(preferredSide)) {
            userPrefersSide = true;
        } else {
            userPrefersSide = false;
        }

        return userPrefersSide;
    }
}
