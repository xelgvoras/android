package net.victium.xelg.notatry.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.preference.PreferenceManager;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class ImportCharacterJsonUtils  {

    private static final String CHARACTER_BLOCK = "character";
    private static final String CHARACTER_NAME = "name";
    private static final String CHARACTER_TYPE = "type";
    private static final String CHARACTER_AGE = "age";
    private static final String CHARACTER_LEVEL = "level";
    private static final String CHARACTER_POWER_LIMIT = "powerLimit";
    private static final String CHARACTER_SIDE = "side";

    private static final String SHIELD_BLOCK = "activeShields";
    private static final String SHIELD_LIST = "shields";

    private static final String SHIELD_NAME = "name";
    private static final String SHIELD_COST = "cost";

    public static void importCharacterFromJson(Context context, String importCharacterJson)
            throws JSONException {

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notATryDbHelper.getWritableDatabase();
        sqLiteDatabase.delete(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                null
        );

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String characterNameKey = context.getString(R.string.pref_full_name_key);
        String characterTypeKey = context.getString(R.string.pref_type_key);
        String characterAgeKey = context.getString(R.string.pref_age_key);
        String characterLevelKey = context.getString(R.string.pref_level_key);
        String characterPowerLimitKey = context.getString(R.string.pref_power_key);
        String characterSideKey = context.getString(R.string.pref_side_key);

        JSONObject importJson = new JSONObject(importCharacterJson);
        JSONObject characterJson = importJson.getJSONObject(CHARACTER_BLOCK);

        sharedPreferences.edit()
                .putString(characterNameKey, characterJson.getString(CHARACTER_NAME))
                .putString(characterTypeKey, characterJson.getString(CHARACTER_TYPE))
                .putString(characterAgeKey, characterJson.getString(CHARACTER_AGE))
                .putString(characterLevelKey, characterJson.getString(CHARACTER_LEVEL))
                .putString(characterPowerLimitKey, characterJson.getString(CHARACTER_POWER_LIMIT))
                .putString(characterSideKey, characterJson.getString(CHARACTER_SIDE))
                .apply();

        if (!importJson.getJSONObject(SHIELD_BLOCK).has(SHIELD_LIST)) {
            return;
        }

        JSONArray shieldsJsonArray = importJson.getJSONObject(SHIELD_BLOCK).getJSONArray(SHIELD_LIST);

        if (shieldsJsonArray.length() == 0) {
            return;
        }

        for (int i = 0; i < shieldsJsonArray.length(); i++) {

            JSONObject jsonShield = shieldsJsonArray.getJSONObject(i);
            String shieldName = jsonShield.getString(SHIELD_NAME);
            int shieldCost = jsonShield.getInt(SHIELD_COST);

            ShieldUtil.Shield shield = ShieldUtil.getShield(context, shieldName);
            addShield(context, shield, shieldCost);
        }
    }

    private static void addShield(Context context, ShieldUtil.Shield shield, int cost) {

        String name = shield.getName();
        String type = shield.getType();
        int magicDefence = shield.getMagicDefenceMultiplier() * cost;
        int physicDefence = shield.getPhysicDefenceMultiplier() * cost;
        int mentalDefence = 0;
        if (shield.hasMentalDefence()) mentalDefence = 1;
        String target = "персональный";
        if (!shield.isPersonalShield()) target = "групповой";
        int range = shield.getRange();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_NAME, name);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE, type);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, cost);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, magicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, physicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE, mentalDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TARGET, target);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_RANGE, range);

        context.getContentResolver().insert(NotATryContract.ActiveShieldsEntry.CONTENT_URI, contentValues);
    }
}
