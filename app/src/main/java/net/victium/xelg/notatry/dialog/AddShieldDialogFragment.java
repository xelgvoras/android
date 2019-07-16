package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.victium.xelg.notatry.viewModel.AddShieldViewModel;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;
import net.victium.xelg.notatry.database.ShieldEntryBuilder;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;
import net.victium.xelg.notatry.viewModel.AddShieldViewModelFactory;

public class AddShieldDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_SHIELD_ID = "extraShieldId";
    public static final String INSTANCE_SHIELD_ID = "instanceShieldId";

    private static final int DEFAULT_SHIELD_ID = -1;

    private EditText mShieldCost;
    private Spinner mShieldList;

    private int mShieldId = DEFAULT_SHIELD_ID;
    private String positiveButtonText = "Повесить щит";

    /*private int mShieldPowerLimit;*/
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

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SHIELD_ID)) {
            mShieldId = savedInstanceState.getInt(INSTANCE_SHIELD_ID, DEFAULT_SHIELD_ID);
        }

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_SHIELD_ID)) {
            positiveButtonText = "Усилить щит";
            if (mShieldId == DEFAULT_SHIELD_ID) {

                mShieldId = bundle.getInt(EXTRA_SHIELD_ID);

                AddShieldViewModelFactory factory = new AddShieldViewModelFactory(mDb, mShieldId);

                final AddShieldViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddShieldViewModel.class);

                viewModel.getShield().observe(this, new Observer<ShieldEntry>() {
                    @Override
                    public void onChanged(ShieldEntry shieldEntry) {
                        viewModel.getShield().removeObserver(this);
                        populateUI(shieldEntry);
                    }
                });
            }
        }

        /*if (mActivity instanceof ShieldsActivity) {
            setupShieldPowerLimit(1);
        } else if (mActivity instanceof BattleActivity) {
            setupShieldPowerLimit(0);
        }*/

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
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShield();
                        mListener.onDialogPositiveClick(AddShieldDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private void populateUI(ShieldEntry shieldEntry) {
        if (shieldEntry == null) {
            return;
        }

        mShieldCost.setText(shieldEntry.getPower());
    }

    /*private void setupShieldPowerLimit(int module) {

        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();

        int powerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT));

        mShieldPowerLimit = powerLimit * module;

        cursor.close();
    }*/

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
