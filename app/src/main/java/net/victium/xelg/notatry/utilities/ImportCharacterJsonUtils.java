package net.victium.xelg.notatry.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.preference.PreferenceManager;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.data.Shield;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        JSONArray shieldsJsonArray = importJson.getJSONObject(SHIELD_BLOCK).getJSONArray(SHIELD_LIST);

        ArrayList<Shield> shieldArray = createShieldArrayList(context);

        for (int i = 0; i < shieldsJsonArray.length(); i++) {

            JSONObject jsonShield = shieldsJsonArray.getJSONObject(i);
            String shieldName = jsonShield.getString(SHIELD_NAME);
            int shieldCost = jsonShield.getInt(SHIELD_COST);

            for (Shield shield : shieldArray) {

                if (shieldName.equals(shield.name)) {

                    addShield(sqLiteDatabase, shield, shieldCost);
                    break;
                }
            }
        }
    }

    private static ArrayList<Shield> createShieldArrayList(Context context) {
        ArrayList<Shield> shieldArrayList = new ArrayList<>();
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_mag_shield),
                "унив",
                1,
                1,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_clean_mind),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_will_barrier),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_sphere_of_tranquility),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_icecrown),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_concave_shield),
                "физ",
                0,
                2,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_sphere_of_negation),
                "маг",
                2,
                0,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_pair_shield),
                "унив",
                1,
                1,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_cloack_of_darkness),
                "маг",
                0,
                0,
                false,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                true,
                3
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_highest_mag_shield),
                "унив",
                2,
                2,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_big_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_protective_dome),
                "унив",
                2,
                2,
                true,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_crystal_shield),
                "физ",
                0,
                100,
                false,
                false,
                5
        ));

        return shieldArrayList;
    }

    private static void addShield(SQLiteDatabase db, Shield shield, int cost) {

        String name = shield.name;
        String type = shield.type;
        int magicDefence = shield.magicDefenceMultiplier * cost;
        int physicDefence = shield.physicDefenceMultiplier * cost;
        int mentalDefence = 0;
        if (shield.hasMentalDefence) mentalDefence = 1;
        String target = "персональный";
        if (!shield.isPersonalShield) target = "групповой";
        int range = shield.range;

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_NAME, name);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE, type);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, cost);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, magicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, physicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE, mentalDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TARGET, target);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_RANGE, range);

        db.insert(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                contentValues
        );
    }
}
