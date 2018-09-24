package net.victium.xelg.notatry;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.dialog.OpenFileDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ImportExportActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Character mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);
        mDb = notATryDbHelper.getWritableDatabase();

        mCharacter = new Character(this);
    }

    public void onClickExportCharacter(View view) {

        String characterName = mCharacter.getCharacterName();
        String characterType = mCharacter.getCharacterType();
        String characterAge = String.valueOf(mCharacter.getCharacterAge());
        String characterLevel = String.valueOf(mCharacter.getCharacterLevel());
        String characterPowerLimit = String.valueOf(mCharacter.getCharacterPowerLimit());
        String characterSide = mCharacter.getCharacterSideToString();

        JSONObject character = new JSONObject();

        try {
            character.put("name", characterName);
            character.put("type", characterType);
            character.put("age", characterAge);
            character.put("level", characterLevel);
            character.put("powerLimit", characterPowerLimit);
            character.put("side", characterSide);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject activeShields = new JSONObject();

        try {
            activeShields.put("shields", getCurrentShieldsToJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonExport = new JSONObject();

        try {
            jsonExport.put("character", character);
            jsonExport.put("activeShields", activeShields);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String testJson = jsonExport.toString();

        OpenFileDialogFragment openFileDialogFragment = new OpenFileDialogFragment();
        openFileDialogFragment.show(getSupportFragmentManager(), "ChooseDirectoryToSaveFile");

    }

    private JSONArray getCurrentShieldsToJSON() {
        boolean hasShields = true;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        Cursor cursor = getCurrentShield();
        int columnCount = cursor.getColumnCount();

        if (!cursor.moveToFirst()) {
            return null;
        }

        while (hasShields) {
            jsonObject = new JSONObject();

            for (int i = 0; i < columnCount; i++) {
                try {
                    jsonObject.put(cursor.getColumnName(i), cursor.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            jsonArray.put(jsonObject);

            if (!cursor.moveToNext()) {
                hasShields = false;
            }
        }

        return jsonArray;
    }

    private Cursor getCharacterStatus() {

        return mDb.query(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getCurrentShield () {

        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
