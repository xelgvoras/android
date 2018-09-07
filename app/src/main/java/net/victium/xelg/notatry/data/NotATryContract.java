package net.victium.xelg.notatry.data;

import android.provider.BaseColumns;

public class NotATryContract {

    public static final class CharacterStatusEntry implements BaseColumns {

        public static final String TABLE_NAME = "characterStatus";
        public static final String COLUMN_POWER = "power";
        public static final String COLUMN_CURRENT_DEPTH = "currentDepth";
        public static final String COLUMN_DEPTH_LIMIT = "depthLimit";
        public static final String COLUMN_CURRENT_SHIELDS = "currentShields";
        public static final String COLUMN_SHIELDS_LIMIT = "shieldsLimit";
        public static final String COLUMN_AMULETS_LIMIT = "amuletsLimit";
        public static final String COLUMN_NATURAL_DEFENCE = "naturalDefence";
        public static final String COLUMN_REACTIONS_NUMBER = "reactionsNumber";
    }
}
