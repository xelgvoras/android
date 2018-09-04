package net.victium.xelg.notatry.data;

import android.provider.BaseColumns;

public class NotATryContract {
    private NotATryContract() {};

    public class CharacterStatusEntry implements BaseColumns {
        public static final String TABLE_NAME = "characterProperties";
        public static final String COLUMN_CHARACTER_NAME = "charName";
        public static final String COLUMN_CURRENT_POWER = "currentPower";
        public static final String COLUMN_MAX_DEPTH = "maxDepth";
        public static final String COLUMN_MAX_PERSONAL_SHIELDS = "maxShields";
        public static final String COLUMN_MAX_AMULETS = "maxAmulets";
        public static final String COLUMN_NATURAL_DEFENCE = "naturalDefence";
        public static final String COLUMN_COUNT_REACTIONS = "countReactions";
    }

    public class DuskLayersEntry implements BaseColumns {
        public static final String TABLE_NAME = "duskSummary";
        public static final String COLUMN_DUSK_LAYER = "duskLayer";
        public static final String COLUMN_ROUNDS = "duskRounds";
        public static final String COLUMN_TIME = "duskTime";
    }

    public class ActiveShieldsEntry implements BaseColumns {
        public static final String TABLE_NAME = "activeShields";
        public static final String COLUMN_SHIELD_NAME = "shieldName";
        public static final String COLUMN_COST = "shieldCost";
        public static final String COLUMN_MAGIC_DEFENCE = "magicDefence";
        public static final String COLUMN_PHYSIC_DEFENCE = "physicDefence";
        public static final String COLUMN_MENTAL_DEFENCE = "mentalDefence";
        public static final String COLUMN_RANGE = "range";
    }
}
