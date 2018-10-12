package net.victium.xelg.notatry.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;

public class TransformUtil {
    private TransformUtil() {}

    public static String makeTransform(Context context) {
        String battleForm = getCurrentForm(context);

        if (battleForm.equals("человек")) {
            battleForm = "боевая форма";
        } else {
            battleForm = "человек";
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_BATTLE_FORM, battleForm);
        context.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues,
                null, null);

        int count = 0;
        Character character = new Character(context);
        if (!character.getCharacterType().equals("вампир")) {
            String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=?";
            String[] selectionArgs = new String[]{"персональный"};
            count = context.getContentResolver().delete(NotATryContract.ActiveShieldsEntry.CONTENT_URI, selection, selectionArgs);
        }

        String transformMessage = "Выполнена трансформация";
        if (count > 0) {
            transformMessage = transformMessage + ", все персональные щиты уничтожены";
        }

        return transformMessage;
    }

    public static String getCurrentForm(@NonNull Context context) {

        Cursor cursor = context.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        String returnForm = cursor.getString(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_BATTLE_FORM));
        cursor.close();

        return returnForm;
    }
}
