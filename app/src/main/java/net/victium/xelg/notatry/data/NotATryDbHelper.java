package net.victium.xelg.notatry.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.victium.xelg.notatry.data.NotATryContract.*;

import java.util.ArrayList;

public class NotATryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notATry.db";
    private static final int DATABASE_VERSION = 1;

    public NotATryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void execSqlArray(SQLiteDatabase sqLiteDatabase, ArrayList<String> arrayList) {
        for (String sqlString : arrayList) {
            sqLiteDatabase.execSQL(sqlString);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ArrayList<String> listOfTables = new ArrayList<>();

        final String SQL_CREATE_CHARACTER_STATUS_TABLE = "CREATE TABLE " + CharacterStatusEntry.TABLE_NAME + " (" +
                CharacterStatusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CharacterStatusEntry.COLUMN_CHARACTER_NAME + " TEXT NOT NULL, " +
                CharacterStatusEntry.COLUMN_CURRENT_POWER + " INTEGER, " +
                CharacterStatusEntry.COLUMN_MAX_DEPTH + " INTEGER NOT NULL, " +
                CharacterStatusEntry.COLUMN_MAX_PERSONAL_SHIELDS + " INTEGER NOT NULL, " +
                CharacterStatusEntry.COLUMN_MAX_AMULETS + " INTEGER, " +
                CharacterStatusEntry.COLUMN_NATURAL_DEFENCE + " INTEGER, " +
                CharacterStatusEntry.COLUMN_COUNT_REACTIONS + " INTEGER " +
                "); ";
        listOfTables.add(SQL_CREATE_CHARACTER_STATUS_TABLE);

        final String SQL_CREATE_DUSK_LAYERS_TABLE = "CREATE TABLE " + DuskLayersEntry.TABLE_NAME + " (" +
                DuskLayersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DuskLayersEntry.COLUMN_DUSK_LAYER + " INTEGER NOT NULL, " +
                DuskLayersEntry.COLUMN_ROUNDS + " INTEGER, " +
                DuskLayersEntry.COLUMN_TIME + " INTEGER " +
                "); ";
        listOfTables.add(SQL_CREATE_DUSK_LAYERS_TABLE);

        final String SQL_CREATE_ACTIVE_SHIELDS_TABLE = "CREATE TABLE " + ActiveShieldsEntry.TABLE_NAME + " (" +
                ActiveShieldsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ActiveShieldsEntry.COLUMN_SHIELD_NAME + " INTEGER NOT NULL, " +
                ActiveShieldsEntry.COLUMN_COST + " INTEGER NOT NULL, " +
                ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + " INTEGER NOT NULL, " +
                ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + " INTEGER NOT NULL, " +
                ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + " INTEGER, " +
                ActiveShieldsEntry.COLUMN_RANGE + "INTEGER NOT NULL " +
                "); ";
        listOfTables.add(SQL_CREATE_ACTIVE_SHIELDS_TABLE);

        execSqlArray(sqLiteDatabase, listOfTables);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        ArrayList<String> listOfTables = new ArrayList<>();
        listOfTables.add("DROP TABLE IF EXISTS " + CharacterStatusEntry.TABLE_NAME);
        listOfTables.add("DROP TABLE IF EXISTS " + DuskLayersEntry.TABLE_NAME);
        listOfTables.add("DROP TABLE IF EXISTS " + ActiveShieldsEntry.TABLE_NAME);

        execSqlArray(sqLiteDatabase, listOfTables);
    }
}
