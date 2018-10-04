package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.victium.xelg.notatry.BattleActivity;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.ShieldsActivity;
import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.ShieldUtil;

import java.util.ArrayList;

public class AddShieldDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText mShieldCost;
    private Spinner mShieldList;

    private int mShieldPowerLimit;
    private Activity mActivity;

    public interface AddShieldDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    AddShieldDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (AddShieldDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mActivity = getActivity();

        if (mActivity instanceof ShieldsActivity) {
            setupShieldPowerLimit(1);
        } else if (mActivity instanceof BattleActivity) {
            setupShieldPowerLimit(0);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_shield, null);
        mShieldCost = view.findViewById(R.id.et_shield_cost);
        mShieldList = view.findViewById(R.id.sp_shields_list);

        ArrayList<ShieldUtil.Shield> shieldArrayList = ShieldUtil.getShieldList(mActivity);
        final ShieldArrayAdapter shieldArrayAdapter = new ShieldArrayAdapter(mActivity, shieldArrayList);
        shieldArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShieldList.setAdapter(shieldArrayAdapter);
        mShieldList.setOnItemSelectedListener(this);

        builder.setTitle("Повесить щит")
                .setMessage("Укажите щит и его силу")
                .setView(view)
                .setPositiveButton("Повесить щит", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShield();
                        mListener.onDialogPositiveClick(AddShieldDialogFragment.this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addShield() {

        String errorMessage;

        Cursor cursor;
        String[] selectionPersonal = new String[]{"персональный"};
        String[] selectionGroup = new String[]{"групповой"};

        int personalShieldsCount = getShieldCount(selectionPersonal);
        int groupShieldsCount = getShieldCount(selectionGroup);

        cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();

        int personalShieldsLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT));
        cursor.close();

        String input = mShieldCost.getText().toString();
        int inputInt;

        if (input.length() == 0) {
            errorMessage = "Вы не указали количество у.е. для щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        } else {
            errorMessage = "Можно указывать только положительные, целые числа, больше нуля.";

            try {
                inputInt = Integer.parseInt(input);

                if (inputInt <= 0) {
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                } else if (inputInt > mShieldPowerLimit && mShieldPowerLimit != 0) {
                    errorMessage = "Вы не можете создать щит больше вашего резерва";
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }

        ShieldUtil.Shield currentShield = (ShieldUtil.Shield) mShieldList.getSelectedItem();

        String name = currentShield.getName();
        String type = currentShield.getType();
        int minCost = currentShield.getMinCost();
        int magicDefence = currentShield.getMagicDefenceMultiplier() * inputInt;
        int physicDefence = currentShield.getPhysicDefenceMultiplier() * inputInt;
        int mentalDefence = 0;
        if (currentShield.hasMentalDefence()) mentalDefence = 1;
        String target = "персональный";
        if (!currentShield.isPersonalShield()) target = "групповой";
        int range = currentShield.getRange();

        if (inputInt < minCost) {
            errorMessage = "Недостаточно магической силы для поддержания щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        if (target.equals("персональный")) {
            if (personalShieldsCount >= personalShieldsLimit) {
                errorMessage = "Достигнут лимит персональных щитов";
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            if (groupShieldsCount >= 1) {
                errorMessage = "Достигнут лимит групповых щитов";
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_NAME, name);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE, type);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, inputInt);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, magicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, physicDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE, mentalDefence);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_TARGET, target);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_RANGE, range);

        try {
            mActivity.getContentResolver().insert(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                    contentValues);
        } catch (SQLException exception) {
            errorMessage = "Нельзя повесить два одинаковых щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private int getShieldCount(String[] selectionArgs){
        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=?";

        return mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, selection, selectionArgs, null).getCount();
    }
}
