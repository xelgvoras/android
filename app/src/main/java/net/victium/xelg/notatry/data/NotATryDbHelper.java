package net.victium.xelg.notatry.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.victium.xelg.notatry.data.NotATryContract.*;

public class NotATryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notatry.db";

    private static final int DATABASE_VERSION = 3;

    public NotATryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CHARACTER_STATUS_TABLE = "CREATE TABLE " + CharacterStatusEntry.TABLE_NAME + " (" +
                CharacterStatusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CharacterStatusEntry.COLUMN_CURRENT_POWER + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_CURRENT_DEPTH + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_DEPTH_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_CURRENT_SHIELDS + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_SHIELDS_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_AMULETS_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_NATURAL_DEFENCE + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_REACTIONS_NUMBER + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_CHARACTER_STATUS_TABLE);

        final String SQL_CREATE_DUSK_LAYERS_SUMMARY_TABLE = "CREATE TABLE " + DuskLayersSummaryEntry.TABLE_NAME + " (" +
                DuskLayersSummaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DuskLayersSummaryEntry.COLUMN_LAYER + " INTEGER NOT NULL," +
                DuskLayersSummaryEntry.COLUMN_ROUNDS + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_DUSK_LAYERS_SUMMARY_TABLE);

        final String SQL_CREATE_ACTIVE_SHIELDS_TABLE = "CREATE TABLE " + ActiveShieldsEntry.TABLE_NAME + " (" +
                ActiveShieldsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ActiveShieldsEntry.COLUMN_NAME + " TEXT NOT NULL UNIQUE," +
                ActiveShieldsEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                ActiveShieldsEntry.COLUMN_COST + " INTEGER NOT NULL," +
                ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + " INTEGER NOT NULL," +
                ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + " INTEGER NOT NULL," +
                ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + " INTEGER NOT NULL," +
                ActiveShieldsEntry.COLUMN_TARGET + " TEXT NOT NULL," +
                ActiveShieldsEntry.COLUMN_RANGE + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVE_SHIELDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CharacterStatusEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DuskLayersSummaryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ActiveShieldsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
