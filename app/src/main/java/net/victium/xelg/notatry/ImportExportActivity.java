package net.victium.xelg.notatry;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.dialog.OpenFileDialogFragment;
import net.victium.xelg.notatry.utilities.ImportCharacterJsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class ImportExportActivity extends AppCompatActivity implements
        OpenFileDialogFragment.OpenFileDialogListener {

    private Character mCharacter;
    private File mDefaultPath;
    private File mSelectedFile;

    private static final int REQUEST_EXTERNAL_STORAGE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCharacter = new Character(this);

        mDefaultPath = new File(Environment.getExternalStorageDirectory() + "/notatry");
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

        String exportCharacter = jsonExport.toString();

        checkWriteExternalStoragePermission();

        if (isExternalStorageWritable()) {
            if (!mDefaultPath.exists()) {
                if (!mDefaultPath.mkdirs()) {
                    Toast.makeText(this, "Не удалось создать каталог: " + mDefaultPath, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            File fileName = new File(mDefaultPath + "/" + characterName + ".json");
            if (!fileName.exists()) {
                try {
                    if (!fileName.createNewFile()) {
                        Toast.makeText(this, "Не удалось создать файл: " + fileName, Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                fileOutputStream.write(exportCharacter.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Персонаж успешно экспортирован в файл: " + fileName.toString(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Нет доступа к внутренней памяти", Toast.LENGTH_LONG).show();
        }

    }

    public void onClickImportCharacter(View view) {

        DialogFragment dialogFragment = new OpenFileDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "OpenFileDialogFragment");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {

            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Разрешение не предоставлено, невозможно экспортировать персонажа",
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Для экспорта персонажа нужны права на запись в внутреннюю память",
                        Toast.LENGTH_LONG).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Nullable
    private JSONArray getCurrentShieldsToJSON() {
        boolean hasShields = true;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        Cursor cursor = getCurrentShield();
        int columnName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnCost = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST);

        if (!cursor.moveToFirst()) {
            return null;
        }

        while (hasShields) {
            jsonObject = new JSONObject();

            try {
                jsonObject.put("name", cursor.getString(columnName));
                jsonObject.put("cost", cursor.getString(columnCost));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

            if (!cursor.moveToNext()) {
                hasShields = false;
            }
        }

        return jsonArray;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private Cursor getCurrentShield () {

        return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onDialogFileSelected(DialogFragment dialogFragment) {

        if (dialogFragment instanceof OpenFileDialogFragment) {
            mSelectedFile = ((OpenFileDialogFragment) dialogFragment).mSelectedFileName;
        }

        String importCharacter;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mSelectedFile));
            importCharacter = bufferedReader.readLine();
            bufferedReader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Не удалось прочитать файл", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        try {
            ImportCharacterJsonUtils.importCharacterFromJson(this, importCharacter);
            Toast.makeText(this, "Персонаж успешно импортирован из файла: " + mSelectedFile, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(this, "Импорт не удался, почему-то", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
