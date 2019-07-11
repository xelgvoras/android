package net.victium.xelg.notatry.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NotATryContract {

    public static final String CONTENT_AUTHORITY = "net.victium.xelg.notatry";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CHARACTER_STATUS_PATH = "characterStatus";
    public static final String DUSK_LAYERS_PATH = "duskLayers";
    public static final String ACTIVE_SHIELDS_PATH = "activeShields";
    public static final String BATTLE_JOURNAL_PATH = "battleJournal";
    public static final String ACTIVE_AMULETS_PATH = "activeAmulets";
    public static final String SPELLS_IN_AMULET_PATH = "spellsInAmulet";

    public static final class CharacterStatusEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(CHARACTER_STATUS_PATH).build();

        public static final String TABLE_NAME = "characterStatus";
        public static final String COLUMN_CURRENT_POWER = "currentPower";
        public static final String COLUMN_POWER_LIMIT = "powerLimit";
        public static final String COLUMN_CURRENT_DEPTH = "currentDepth";
        public static final String COLUMN_DEPTH_LIMIT = "depthLimit";
        public static final String COLUMN_CURRENT_SHIELDS = "currentShields";
        public static final String COLUMN_SHIELDS_LIMIT = "shieldsLimit";
        public static final String COLUMN_CURRENT_AMULETS = "currentAmulets";
        public static final String COLUMN_NATURAL_DEFENCE = "naturalDefence";
        public static final String COLUMN_NATURAL_MENTAL_DEFENCE = "naturalMentalDefence";
        public static final String COLUMN_REACTIONS_NUMBER = "reactionsNumber";
        public static final String COLUMN_BATTLE_FORM = "battleForm";
    }

    public static final class DuskLayersSummaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(DUSK_LAYERS_PATH).build();

        public static final String TABLE_NAME = "duskLayers";
        public static final String COLUMN_LAYER = "layer";
        public static final String COLUMN_ROUNDS = "rounds";
    }

    public static final class ActiveShieldsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ACTIVE_SHIELDS_PATH).build();

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

    public static final class BattleJournalEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(BATTLE_JOURNAL_PATH).build();

        public static final String TABLE_NAME = "battleJournal";
        public static final String COLUMN_ATTACK_MESSAGE = "attackMessage";
        public static final String COLUMN_RESULT_MESSAGE = "resultMessage";
        public static final String COLUMN_SYSTEM_MESSAGE = "systemMessage";
    }

    /*public static final class ActiveAmuletsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ACTIVE_AMULETS_PATH).build();

        public static final String TABLE_NAME = "activeAmulets";
        public static final String COLUMN_AMULET_NAME = "amuletName";
        public static final String COLUMN_AMULET_TYPE = "amuletType";
        public static final String COLUMN_AMULET_TRIGGER = "amuletTrigger";
        public static final String COLUMN_SPELL_NAME = "spellName";
        public static final String COLUMN_SPELL_COST = "spellCost";
        public static final String COLUMN_SERIES_NUMBER = "seriesNumber";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(SPELLS_IN_AMULET_PATH).build();

        public static final String TABLE_NAME = "spellsInAmulet";
        public static final String COLUMN_AMULET_ID = "amuletId";
        public static final String COLUMN_SPELL_NAME = "spellName";
        public static final String COLUMN_COST = "cost";
    }*/
}
