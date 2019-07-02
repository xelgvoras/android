package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.enums.Operation;

public class UpdateNaturalDefenceDialogFragment extends DialogFragment {

    private Activity mActivity;
    private EditText mInputPowerEditText;
    private int mCurrentPower;
    private int mNaturalDefence;

    public interface UpdateNaturalDefenceDialogListener {
        void onDialogClick(DialogFragment dialogFragment);
    }

    UpdateNaturalDefenceDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (UpdateNaturalDefenceDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateNaturalDefenceDialogListener");
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

        mCurrentPower = getCharacterStatus(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER);
        mNaturalDefence = getCharacterStatus(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE);

        builder.setTitle("Ручное редактирование естественной защиты")
                .setMessage("Меню для ручного изменения естественной защиты, использовать по необходимости\n\n" +
                        "Естественная защита: " + mNaturalDefence)
                .setView(mInputPowerEditText)
                .setPositiveButton("увеличить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateNaturalDefence(Operation.ADD_POWER);
                        mListener.onDialogClick(UpdateNaturalDefenceDialogFragment.this);
                    }
                })
                .setNegativeButton("уменьшить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateNaturalDefence(Operation.DEL_POWER);
                        mListener.onDialogClick(UpdateNaturalDefenceDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private void updateNaturalDefence(Operation operation) {

        String valueString = mInputPowerEditText.getText().toString();
        if (valueString.length() == 0) {
            Toast.makeText(mActivity, "Вы не указали количество у.е.", Toast.LENGTH_LONG).show();
            return;
        }

        int valueInt = Integer.parseInt(valueString);
        int newValue = 0;

        if (operation == Operation.ADD_POWER) {
            newValue = mNaturalDefence + valueInt;
        } else if (operation == Operation.DEL_POWER) {
            newValue = mNaturalDefence - valueInt;
        }

        if (newValue > mCurrentPower) {
            Toast.makeText(mActivity, "Естественная защита не может быть больше текущего резерва", Toast.LENGTH_LONG).show();
            newValue = mCurrentPower;
        } else if (newValue < 0) {
            Toast.makeText(mActivity, "Естественная защита не может быть меньше нуля, операция отменена", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, newValue);

        updateCharacterStatus(contentValues);
    }

    private int getCharacterStatus(String columnName) {
        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        int returnCurrentPower = cursor.getInt(cursor.getColumnIndex(columnName));
        cursor.close();

        return returnCurrentPower;
    }

    private void updateCharacterStatus(ContentValues contentValues) {

        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                contentValues, null, null);
    }
}
