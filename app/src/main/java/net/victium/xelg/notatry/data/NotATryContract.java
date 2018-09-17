package net.victium.xelg.notatry.data;

import android.provider.BaseColumns;

public class NotATryContract {

    public static final class CharacterStatusEntry implements BaseColumns {

        public static final String TABLE_NAME = "characterStatus";
        public static final String COLUMN_CURRENT_POWER = "currentPower";
        public static final String COLUMN_POWER_LIMIT = "powerLimit";
        public static final String COLUMN_CURRENT_DEPTH = "currentDepth";
        public static final String COLUMN_DEPTH_LIMIT = "depthLimit";
        public static final String COLUMN_CURRENT_SHIELDS = "currentShields";
        public static final String COLUMN_SHIELDS_LIMIT = "shieldsLimit";
        public static final String COLUMN_AMULETS_LIMIT = "amuletsLimit";
        public static final String COLUMN_NATURAL_DEFENCE = "naturalDefence";
        public static final String COLUMN_REACTIONS_NUMBER = "reactionsNumber";
    }

    public static final class DuskLayersSummaryEntry implements BaseColumns {

        public static final String TABLE_NAME = "duskLayers";
        public static final String COLUMN_LAYER = "layer";
        public static final String COLUMN_ROUNDS = "rounds";
    }

    public static final class ActiveShieldsEntry implements BaseColumns {

        public static final String TABLE_NAME = "activeShields";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_MAGIC_DEFENCE = "magicDefence";
        public static final String COLUMN_PHYSIC_DEFENCE = "physicDefence";
        public static final String COLUMN_MENTAL_DEFENCE = "mentalDefence";
        public static final String COLUMN_TARGET = "target";
        public static final String COLUMN_RANGE = "range";

        // Названия столбцов для суммы защиты
        public static final String COLUMN_MAGIC_DEFENCE_SUM = "magicDefenceSum";
        public static final String COLUMN_PHYSIC_DEFENCE_SUM = "physicDefenceSum";
        public static final String COLUMN_MENTAL_DEFENCE_SUM = "mentalDefenceSum";
    }
}
