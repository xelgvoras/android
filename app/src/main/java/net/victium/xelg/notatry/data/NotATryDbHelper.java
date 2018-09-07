package net.victium.xelg.notatry.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.victium.xelg.notatry.data.NotATryContract.*;

public class NotATryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notatry.db";

    private static final int DATABASE_VERSION = 1;

    public NotATryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CHARACTER_STATUS_TABLE = "CREATE TABLE " + CharacterStatusEntry.TABLE_NAME + " (" +
                CharacterStatusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CharacterStatusEntry.COLUMN_POWER + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_CURRENT_DEPTH + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_DEPTH_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_CURRENT_SHIELDS + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_SHIELDS_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_AMULETS_LIMIT + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_NATURAL_DEFENCE + " INTEGER NOT NULL," +
                CharacterStatusEntry.COLUMN_REACTIONS_NUMBER + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_CHARACTER_STATUS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CharacterStatusEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
