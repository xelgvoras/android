package net.victium.xelg.notatry.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NotATryContract {
    public static final String AUTHORITY = "net.victium.xelg.notatry";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_CHARACTER_STATUS = "characterProperties";
    public static final String PATH_DUSK_SUMMARY = "duskSummary";
    public static final String PATH_ACTIVE_SHIELDS = "activeShields";

    private NotATryContract() {};

    public static class CharacterStatusEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHARACTER_STATUS).build();

        public static final String TABLE_NAME = "characterProperties";
        public static final String COLUMN_CHARACTER_NAME = "charName";
        public static final String COLUMN_CURRENT_POWER = "currentPower";
        public static final String COLUMN_MAX_DEPTH = "maxDepth";
        public static final String COLUMN_MAX_PERSONAL_SHIELDS = "maxShields";
        public static final String COLUMN_MAX_AMULETS = "maxAmulets";
        public static final String COLUMN_NATURAL_DEFENCE = "naturalDefence";
        public static final String COLUMN_COUNT_REACTIONS = "countReactions";
    }

    public static class DuskLayersEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DUSK_SUMMARY).build();

        public static final String TABLE_NAME = "duskSummary";
        public static final String COLUMN_DUSK_LAYER = "duskLayer";
        public static final String COLUMN_ROUNDS = "duskRounds";
        public static final String COLUMN_TIME = "duskTime";
    }

    public static class ActiveShieldsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVE_SHIELDS).build();

        public static final String TABLE_NAME = "activeShields";
        public static final String COLUMN_SHIELD_NAME = "shieldName";
        public static final String COLUMN_COST = "shieldCost";
        public static final String COLUMN_MAGIC_DEFENCE = "magicDefence";
        public static final String COLUMN_PHYSIC_DEFENCE = "physicDefence";
        public static final String COLUMN_MENTAL_DEFENCE = "mentalDefence";
        public static final String COLUMN_RANGE = "range";
    }
}
