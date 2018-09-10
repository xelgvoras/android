package net.victium.xelg.notatry.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import net.victium.xelg.notatry.data.NotATryContract.*;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase sqLiteDatabase) {
        if (null == sqLiteDatabase) {
            return;
        }

        List<ContentValues> list = new ArrayList<>();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DuskLayersEntry.COLUMN_DUSK_LAYER, 1);
        contentValues.put(DuskLayersEntry.COLUMN_ROUNDS, 10);
        contentValues.put(DuskLayersEntry.COLUMN_TIME, 5);
        list.add(contentValues);

        contentValues = new ContentValues();
        contentValues.put(DuskLayersEntry.COLUMN_DUSK_LAYER, 2);
        contentValues.put(DuskLayersEntry.COLUMN_ROUNDS, 20);
        contentValues.put(DuskLayersEntry.COLUMN_TIME, 15);
        list.add(contentValues);

        contentValues = new ContentValues();
        contentValues.put(DuskLayersEntry.COLUMN_DUSK_LAYER, 3);
        contentValues.put(DuskLayersEntry.COLUMN_ROUNDS, 30);
        contentValues.put(DuskLayersEntry.COLUMN_TIME, 25);
        list.add(contentValues);

        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(DuskLayersEntry.TABLE_NAME, null, null);

            for (ContentValues c : list) {
                sqLiteDatabase.insert(DuskLayersEntry.TABLE_NAME, null, c);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException sqle) {

        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
