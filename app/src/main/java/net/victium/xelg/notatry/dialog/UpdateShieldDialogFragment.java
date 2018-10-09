package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import net.victium.xelg.notatry.BattleActivity;
import net.victium.xelg.notatry.ShieldsActivity;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.utilities.ShieldUtil;

public class UpdateShieldDialogFragment extends DialogFragment {

    private EditText mInputPowerEditText;
    private Activity mActivity;
    private int mShieldPowerLimit;
    private int mCurrentShieldPower;
    private String mShieldName;
    private Uri mSelectedShield;

    public interface UpdateShieldDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    UpdateShieldDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (UpdateShieldDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = getActivity();

        String shieldId = getArguments().getString("itemId");
        mSelectedShield = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();
        Cursor cursor = mActivity.getContentResolver().query(mSelectedShield, null, null, null, null);
        cursor.moveToFirst();
        mShieldName = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME));
        mCurrentShieldPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST));
        cursor.close();

        if (mActivity instanceof ShieldsActivity) {
            setupShieldPowerLimit(1);
        } else if (mActivity instanceof BattleActivity) {
            setupShieldPowerLimit(2);
        }

        mInputPowerEditText = new EditText(mActivity);
        mInputPowerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mInputPowerEditText.setHint("на сколько усилить щит?");

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle("Усилить щит")
                .setMessage("Укажите количество у.е. силы, которые вы хотите вложить в щит для усиления.\n" +
                        "Текущая сила щита: " + mCurrentShieldPower + "/" + mShieldPowerLimit)
                .setView(mInputPowerEditText)
                .setPositiveButton("Усилить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateShield();
                        mListener.onDialogPositiveClick(UpdateShieldDialogFragment.this);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }

    private void setupShieldPowerLimit(int module) {
        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();

        int powerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT));

        mShieldPowerLimit = powerLimit * module;

        cursor.close();
    }

    private void updateShield() {

        // COMPLETED(bug) Если щит изначально больше двух резервов, усиление сбрасывает силу щита до 2х резервов
        // С точки зрения логики, если щит возможно усилить, его необходимо усилить до допустимого предела
        // Если щит невозможно усилить, оставить текущую силу щита

        String updateMessage = "Щит успешно усилен";
        String errorMessage = "Можно указывать только положительные, целые числа больше нуля";
        String input = mInputPowerEditText.getText().toString();
        int inputInt;
        int newValue;

        if (input.length() == 0) {
            Toast.makeText(mActivity, "Вы не указали у.е. для щита", Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                inputInt = Integer.parseInt(input);

                if (inputInt < 0) {
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }

                newValue = mCurrentShieldPower + inputInt;

                if (newValue > mShieldPowerLimit) {
                    if (mCurrentShieldPower < mShieldPowerLimit) {
                        updateMessage = updateMessage + ", потрачено больше у.е., чем позволяет лимит";
                        newValue = mShieldPowerLimit;
                    } else {
                        Toast.makeText(mActivity, "Невозможно усилить щит, превышен лимит", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }

        ShieldUtil.Shield currentShield = ShieldUtil.getShield(mActivity, mShieldName);

        if (currentShield == null) {
            Toast.makeText(mActivity, "Не найден такой щит в системе", Toast.LENGTH_LONG).show();
            return;
        }

        int magicDefence = currentShield.getMagicDefenceMultiplier() * newValue;
        int physicDefence = currentShield.getPhysicDefenceMultiplier() * newValue;

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, newValue);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, magicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, physicDefence);

        mActivity.getContentResolver().update(mSelectedShield, contentValues, null, null);

        Toast.makeText(mActivity, updateMessage, Toast.LENGTH_LONG).show();
    }
}
