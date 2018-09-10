package net.victium.xelg.notatry.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract.*;

public class DatabaseUtil {
    private static Context mContext;
    private static SQLiteDatabase mDb;

    public static void setupCharacterTable(Context context, SQLiteDatabase sqLiteDatabase) {
        mContext = context;
        mDb = sqLiteDatabase;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        ContentValues contentValues = new ContentValues();

        String keyForCharName = context.getString(R.string.pref_full_name_key);
        String defaultName = context.getString(R.string.pref_full_name_default);
        String characterName = sharedPreferences.getString(keyForCharName, defaultName);
        contentValues.put(CharacterStatusEntry.COLUMN_CHARACTER_NAME, characterName);

        String keyForCharPower = context.getString(R.string.pref_power_key);
        String defaultPower = context.getString(R.string.pref_power_default);
        int currentPower = Integer.parseInt(sharedPreferences.getString(keyForCharPower, defaultPower));
        contentValues.put(CharacterStatusEntry.COLUMN_CURRENT_POWER, currentPower);

        int maxDuskDepth = 3;
        contentValues.put(CharacterStatusEntry.COLUMN_MAX_DEPTH, maxDuskDepth);

        String keyForCharLevel = context.getString(R.string.pref_level_key);
        String defaultLevel = context.getString(R.string.pref_level_value_seven);
        int characterLevel = Integer.parseInt(sharedPreferences.getString(keyForCharLevel, defaultLevel));
        int maxPersonalShields = 1;
        int maxAutoAmulets = 1;

        if (characterLevel >= 5) {
            maxPersonalShields = 1;
            maxAutoAmulets = 1;
        } else if (characterLevel >= 3) {
            maxPersonalShields = 2;
            maxAutoAmulets = 1;
        } else if (characterLevel >= 1) {
            maxPersonalShields = 3;
            maxAutoAmulets = 2;
        } else if (characterLevel == 0) {
            maxPersonalShields = 4;
            maxAutoAmulets = 3;
        }

        contentValues.put(CharacterStatusEntry.COLUMN_MAX_PERSONAL_SHIELDS, maxPersonalShields);
        contentValues.put(CharacterStatusEntry.COLUMN_MAX_AMULETS, maxAutoAmulets);

        String keyForCharType = context.getString(R.string.pref_type_key);
        String defaultType = context.getString(R.string.pref_type_value_mag);
        String characterType = sharedPreferences.getString(keyForCharType, defaultType);
        String[] vops = new String[]{
                context.getString(R.string.pref_type_value_flipflop),
                context.getString(R.string.pref_type_value_werewolf),
                context.getString(R.string.pref_type_value_werewolf_mag),
                context.getString(R.string.pref_type_value_vampire)
        };
        int naturalDefence = 0;
        int reactionsCount = 1;

        for (String vop : vops) {
            if (vop.equals(characterType)) {
                naturalDefence = currentPower;
                if (characterLevel >= 5) {
                    reactionsCount = 2;
                } else if (characterLevel >= 3) {
                    reactionsCount = 3;
                } else if (characterLevel >= 1) {
                    reactionsCount = 4;
                } else if (characterLevel == 0) {
                    reactionsCount = 5;
                }
                break;
            }
        }

        contentValues.put(CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
        contentValues.put(CharacterStatusEntry.COLUMN_COUNT_REACTIONS, reactionsCount);

        mDb.insert(CharacterStatusEntry.TABLE_NAME, null, contentValues);
    }
}
