package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import net.victium.xelg.notatry.enums.Operation;

import java.util.ArrayList;

public class UpdateCurrentPowerDialogFragment extends DialogFragment {

    private Activity mActivity;
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
                    + " must implement UpdateCurrentPowerDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        mInputPowerEditText = new EditText(mActivity);
        mInputPowerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mInputPowerEditText.setHint("количество у.е.");

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
            Toast.makeText(mActivity, "Вы не указали количество у.е.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(mActivity, "Вы не можете превысить свой лимит силы", Toast.LENGTH_LONG).show();
            newValue = mPowerLimit;
        } else if (newValue < 0) {
            // COMPLETED(13) Выводить только сообщение с ошибкой, но не тратить сам резерв (?)
            Toast.makeText(mActivity, "Нельзя потратить больше у.е., чем есть в резерве", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, newValue);

        Character character = new Character(mActivity);
        if (character.isCharacterVop()) {
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, newValue);
        }

        updateCharacterStatus(contentValues);
    }

    private Cursor getCharacterStatus() {

        return mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
    }

    private int getMagicPower(@NonNull Cursor cursor, String columnName) {
        int value = 0;

        if (cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(columnName);
            value = cursor.getInt(columnId);
            cursor.close();
        }

        return value;
    }

    private void updateCharacterStatus(ContentValues contentValues) {

        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                contentValues, null, null);
    }
}
