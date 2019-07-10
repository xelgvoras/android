package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.victium.xelg.notatry.BattleActivity;
import net.victium.xelg.notatry.liveData.AddShieldViewModel;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.ShieldsActivity;
import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;
import net.victium.xelg.notatry.database.ShieldEntryBuilder;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;

public class AddShieldDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText mShieldCost;
    private Spinner mShieldList;

    private int mShieldPowerLimit;
    private Activity mActivity;

    private AppDatabase mDb;

    private AddShieldViewModel mViewModel;

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
        mDb = AppDatabase.getInstance(mActivity);

        mViewModel = new AddShieldViewModel(mDb);

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

        final ShieldArrayAdapter shieldArrayAdapter = new ShieldArrayAdapter(mActivity,
                ShieldEntryBuilder.getShieldList(mActivity));
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

        int personalShieldsCount ;
        try {
            personalShieldsCount = mViewModel.getShieldCount(true).getValue();
        } catch (NullPointerException e) {
            personalShieldsCount = 0;
        }
        int groupShieldsCount;
        try {
            groupShieldsCount = mViewModel.getShieldCount(false).getValue();
        } catch (NullPointerException e) {
            groupShieldsCount = 0;
        }

        int personalShieldsLimit = PreferenceUtilities.getPersonalShieldLimit(mActivity);

        ShieldEntry currentShield = (ShieldEntry) mShieldList.getSelectedItem();
        int minCost = currentShield.getMinCost();
        int maxCost = currentShield.getMaxCost();

        if (currentShield.isPersonalShield() && personalShieldsCount >= personalShieldsLimit) {
            errorMessage = "Достигнут лимит персональных щитов";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        } else if (!currentShield.isPersonalShield() && groupShieldsCount >= 1) {
            errorMessage = "Достигнут лимит групповых щитов";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

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
                } else if (inputInt < minCost) {
                    errorMessage = "Недостаточно у.е. для создания щита";
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                } else if (inputInt > maxCost) {
                    errorMessage = "Превышен лимит для создания щита";
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }

        currentShield.setPower(inputInt);

        try {
            mDb.shieldDao().insertShield(currentShield);
        } catch (SQLException e) {
            errorMessage = "Нельзя повесить два одинаковых щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
