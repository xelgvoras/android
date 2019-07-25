package net.victium.xelg.notatry.database;

import android.content.Context;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShieldEntryBuilder {

    public static final String TYPE_UNIVERSAL = "унив";
    public static final String TYPE_MAGIC = "маг";
    public static final String TYPE_PHYSIC = "физ";
    public static final String TYPE_MENTAL = "мент";
    public static final String TARGET_PERSONAL = "перс.";
    public static final String TARGET_GROUP = "груп.";

    private String mName;
    private String mType;
    private int mMinCost;
    private int mMaxCost;
    private int mPower;
    private int mMagicDefenceMultiplier;
    private int mPhysicDefenceMultiplier;
    private int mMentalDefence;
    private String mTarget;
    private int mRange;

    public ShieldEntryBuilder(String name) {
        mName = name;
    }

    public void createShieldEntry(Context context) {

        mType = TYPE_UNIVERSAL;
        mMinCost = 1;
        mMaxCost = PreferenceUtilities.getMagicPowerLimit(context) * 2;
        mPower = 0;
        mMagicDefenceMultiplier = 0;
        mPhysicDefenceMultiplier = 0;
        mMentalDefence = 0;
        mTarget = TARGET_PERSONAL;
        mRange = 1;

        switch (mName) {
            case "Щит мага":
                mMagicDefenceMultiplier = 1;
                mPhysicDefenceMultiplier = 1;
                mMentalDefence = 1;
                break;
            case "Чистый разум":
                mType = TYPE_MENTAL;
                mMinCost = 2;
                mMaxCost = 2;
                mMentalDefence = 1;
                mRange = 0;
                break;
            case "Барьер воли":
                mType = TYPE_MENTAL;
                mMinCost = 2;
                mMaxCost = 2;
                mMentalDefence = 1;
                mRange = 0;
                break;
            case "Сфера спокойствия":
                mType = TYPE_MENTAL;
                mMinCost = 2;
                mMaxCost = 2;
                mMentalDefence = 1;
                mRange = 0;
                break;
            case "Ледяная кора":
                mType = TYPE_MENTAL;
                mMinCost = 2;
                mMaxCost = 2;
                mMentalDefence = 1;
                mRange = 0;
                break;
            case "Силовой барьер":
                mMinCost = 4;
                mMagicDefenceMultiplier = 1;
                mPhysicDefenceMultiplier = 1;
                mRange = 2;
                break;
            case "Вогнутый щит":
                mType = TYPE_PHYSIC;
                mMinCost = 4;
                mPhysicDefenceMultiplier = 2;
                mTarget = TARGET_GROUP;
                mRange = 3;
                break;
            case "Сфера отрицания":
                mType = TYPE_MAGIC;
                mMinCost = 4;
                mMagicDefenceMultiplier = 2;
                mTarget = TARGET_GROUP;
                mRange = 3;
                break;
            case "Спаренный щит":
                mMinCost = 8;
                mMagicDefenceMultiplier = 1;
                mPhysicDefenceMultiplier = 1;
                mMentalDefence = 1;
                mTarget = TARGET_GROUP;
                mRange = 5;
                break;
            case "Плащ тьмы":
                mMinCost = 16;
                mTarget = TARGET_PERSONAL;
                mRange = 5;
                break;
            case "Радужная сфера":
                mMinCost = 64;
                mMagicDefenceMultiplier = 2;
                mPhysicDefenceMultiplier = 2;
                mMentalDefence = 1;
                mRange = 4;
                break;
            case "Высший щит мага":
                mMinCost = 16;
                mMagicDefenceMultiplier = 2;
                mPhysicDefenceMultiplier = 2;
                mMentalDefence = 1;
                break;
            case "Большая радужная сфера":
                mMinCost = 64;
                mMagicDefenceMultiplier = 2;
                mPhysicDefenceMultiplier = 2;
                mMentalDefence = 1;
                mTarget = TARGET_GROUP;
                mRange = 5;
                break;
            case "Защитный купол":
                mMinCost = 256;
                mMagicDefenceMultiplier = 2;
                mPhysicDefenceMultiplier = 2;
                mMentalDefence = 1;
                mTarget = TARGET_GROUP;
                mRange = 6;
                break;
            case "Хрустальный щит":
                mType = TYPE_PHYSIC;
                mMinCost = 512;
                mPhysicDefenceMultiplier = 100;
                mTarget = TARGET_GROUP;
                mRange = 6;
                break;
            default:
                throw new IllegalArgumentException("Unknown shield: " + mName);
        }
    }

    public ShieldEntry getShieldEntry() {

        return new ShieldEntry(
                mName,
                mType,
                mMinCost,
                mMaxCost,
                mPower,
                mMagicDefenceMultiplier,
                mPhysicDefenceMultiplier,
                mMentalDefence,
                mTarget,
                mRange
        );
    }

    public static List<ShieldEntry> getShieldList(Context context) {

        String[] shieldArray = context.getResources().getStringArray(R.array.shields_array);
        List<String> shieldList = Arrays.asList(shieldArray);
        ArrayList<ShieldEntry> shieldEntries = new ArrayList<>();
        ShieldEntryBuilder builder;

        for (String s : shieldList) {
            builder = new ShieldEntryBuilder(s);
            builder.createShieldEntry(context);
            shieldEntries.add(builder.getShieldEntry());
        }

        return shieldEntries;
    }

    public static List<ShieldEntry> getPersonalShieldList(Context context) {

        String[] shieldArray = new String[]{
                context.getString(R.string.shields_mag_shield),
                context.getString(R.string.shields_clean_mind),
                context.getString(R.string.shields_will_barrier),
                context.getString(R.string.shields_sphere_of_tranquility),
                context.getString(R.string.shields_icecrown),
                context.getString(R.string.shields_force_barrier),
                context.getString(R.string.shields_rainbow_sphere),
                context.getString(R.string.shields_highest_mag_shield)
        };
        List<String> immutableShieldList = Arrays.asList(shieldArray);
        ArrayList<ShieldEntry> shieldEntries = new ArrayList<>();
        ShieldEntryBuilder builder;

        for (String s : immutableShieldList) {
            builder = new ShieldEntryBuilder(s);
            builder.createShieldEntry(context);
            shieldEntries.add(builder.getShieldEntry());
        }

        return shieldEntries;
    }
}
