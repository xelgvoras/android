package net.victium.xelg.notatry.data;

import android.content.Context;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;

public class ShieldUtil {

    private ShieldUtil(){}
    
    public static class Shield {

        private String mName;
        private String mType;
        private int mMinCost;
        private int mMagicDefenceMultiplier;
        private int mPhysicDefenceMultiplier;
        private boolean mHasMentalDefence;
        private boolean mPersonalShield;
        private boolean mCanBeDestroyed;
        private int mRange;

        public Shield(String name, String type, int minCost, int magicDefenceMultiplier, 
                      int physicDefenceMultiplier, boolean hasMentalDefence, 
                      boolean personalShield, boolean canBeDestroyed, int range) {
            this.mName = name;
            this.mType = type;
            this.mMinCost = minCost;
            this.mMagicDefenceMultiplier = magicDefenceMultiplier;
            this.mPhysicDefenceMultiplier = physicDefenceMultiplier;
            this.mHasMentalDefence = hasMentalDefence;
            this.mPersonalShield = personalShield;
            this.mCanBeDestroyed = canBeDestroyed;
            this.mRange = range;
        }

        public String getName() {
            return mName;
        }

        public String getType() {
            return mType;
        }
        
        public int getMinCost() {
            return mMinCost;
        }

        public int getMagicDefenceMultiplier() {
            return mMagicDefenceMultiplier;
        }

        public int getPhysicDefenceMultiplier() {
            return mPhysicDefenceMultiplier;
        }

        public boolean hasMentalDefence() {
            return mHasMentalDefence;
        }

        public boolean isPersonalShield() {
            return mPersonalShield;
        }

        public boolean canPersonalShield() {
            return mCanBeDestroyed;
        }

        public int getRange() {
            return mRange;
        }

        @Override
        public String toString() {
            
            String target;
            
            if (mPersonalShield) {
                target = "перс.";
            } else {
                target = "груп.";
            }
            
            return String.format("%s (%s - %s, мин. у.е.: %s)", mName, target, mType, mMinCost);
        }
    }
    
    public static ArrayList<Shield> getShieldList(Context context) {

        ArrayList<Shield> shieldArrayList = new ArrayList<>();
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_mag_shield),
                "унив",
                1,
                1,
                1,
                true,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_clean_mind),
                "мент",
                2,
                0,
                0,
                true,
                true,
                false,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_will_barrier),
                "мент",
                2,
                0,
                0,
                true,
                true,
                false,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_sphere_of_tranquility),
                "мент",
                2,
                0,
                0,
                true,
                true,
                false,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_icecrown),
                "мент",
                2,
                0,
                0,
                true,
                true,
                false,
                0
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_force_barrier),
                "унив",
                4,
                1,
                1,
                false,
                true,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_concave_shield),
                "физ",
                4,
                0,
                2,
                false,
                true,
                false,
                3
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_sphere_of_negation),
                "маг",
                4,
                2,
                0,
                false,
                true,
                false,
                3
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_pair_shield),
                "унив",
                8,
                1,
                1,
                true,
                false,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_cloack_of_darkness),
                "маг",
                16,
                0,
                0,
                false,
                false,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_rainbow_sphere),
                "унив",
                64,
                2,
                2,
                true,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_highest_mag_shield),
                "унив",
                16,
                2,
                2,
                true,
                true,
                false,
                1
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_big_rainbow_sphere),
                "унив",
                64,
                2,
                2,
                true,
                false,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_protective_dome),
                "унив",
                256,
                2,
                2,
                true,
                false,
                false,
                6
        ));
        shieldArrayList.add(new Shield(
                context.getString(R.string.shields_crystal_shield),
                "физ",
                128,
                0,
                100,
                false,
                false,
                false,
                6
        ));

        return shieldArrayList;
    }

    public static Shield getShield(Context context, String name) {

        Shield returnShield = null;

        ArrayList<Shield> shieldList = getShieldList(context);

        for (Shield shield : shieldList) {
            if (name.equals(shield.getName())) {
                returnShield = shield;
                break;
            }
        }

        return returnShield;
    }
}
