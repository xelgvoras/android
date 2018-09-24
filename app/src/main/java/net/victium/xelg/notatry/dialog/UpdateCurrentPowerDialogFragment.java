package net.victium.xelg.notatry.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;

import java.util.ArrayList;

public class UpdateCurrentPowerDialogFragment extends DialogFragment {

    private SQLiteDatabase mDb;
    private EditText mInputPowerEditText;
    private int mCurrentPower;
    private int mPowerLimit;

    public interface UpdateCurrentPowerDialogListener {
        void onDialogClick(DialogFragment dialogFragment);
    }

    UpdateCurrentPowerDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (UpdateCurrentPowerDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mInputPowerEditText = new EditText(getActivity());
        mInputPowerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mInputPowerEditText.setHint("количество у.е.");

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(getActivity());
        mDb = notATryDbHelper.getWritableDatabase();
        mCurrentPower = getMagicPower(getCharacterStatus(), NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER);
        mPowerLimit = getMagicPower(getCharacterStatus(), NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT);


        builder.setTitle("Изменить резерв силы")
                .setMessage("Укажите количество у.е., которые вы хотите добавить в резерв или потратить\n\n" +
                        "Текущий резерв: " + mCurrentPower + "/" + mPowerLimit)
                .setView(mInputPowerEditText)
                .setPositiveButton("пополнить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCurrentPower(Operation.ADD_POWER);
                        mListener.onDialogClick(UpdateCurrentPowerDialogFragment.this);
                    }
                })
                .setNegativeButton("потратить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCurrentPower(Operation.DEL_POWER);
                        mListener.onDialogClick(UpdateCurrentPowerDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private void updateCurrentPower(Operation operation) {

        String valueString = mInputPowerEditText.getText().toString();
        if (valueString.length() == 0) {
            Toast.makeText(getActivity(), "Вы не указали количество у.е.", Toast.LENGTH_LONG).show();
            return;
        }

        int valueInt = Integer.parseInt(valueString);
        int newValue = 0;

        if (operation == Operation.ADD_POWER) {
            newValue = mCurrentPower + valueInt;
        } else if (operation == Operation.DEL_POWER) {
            newValue = mCurrentPower - valueInt;
        }

        if (newValue > mPowerLimit) {
            Toast.makeText(getActivity(), "Вы не можете превысить свой лимит силы", Toast.LENGTH_LONG).show();
            newValue = mPowerLimit;
        } else if (newValue < 0) {
            // TODO(13) Выводить только сообщение с ошибкой, но не тратить сам резерв (?)
            Toast.makeText(getActivity(), "Резерв не может быть отрицательным", Toast.LENGTH_LONG).show();
            newValue = 0;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, newValue);

        ArrayList<String> vops = new ArrayList<>();
        vops.add(getString(R.string.pref_type_value_flipflop));
        vops.add(getString(R.string.pref_type_value_vampire));
        vops.add(getString(R.string.pref_type_value_werewolf));
        vops.add(getString(R.string.pref_type_value_werewolf_mag));

        Character character = new Character(getActivity());

        for (String type : vops) {
            if (character.getCharacterType().equals(type)) {
                contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, newValue);
            }
        }

        updateCharacterStatus(contentValues);
    }

    enum Operation{
        ADD_POWER,
        DEL_POWER
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

    private int getMagicPower(Cursor cursor, String columnName) {
        int value = 0;

        if (cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(columnName);
            value = cursor.getInt(columnId);
            cursor.close();
        }

        return value;
    }

    private void updateCharacterStatus(ContentValues contentValues) {

        mDb.update(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                contentValues,
                null,
                null
        );
    }
}
