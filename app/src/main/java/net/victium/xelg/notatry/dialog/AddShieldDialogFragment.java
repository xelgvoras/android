package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.victium.xelg.notatry.database.AppExecutors;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;
import net.victium.xelg.notatry.database.ShieldEntryBuilder;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;

import java.util.List;

public class AddShieldDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_SHIELD_ID = "extraShieldId";
    public static final String INSTANCE_SHIELD_ID = "instanceShieldId";
    public static final String ACTIVITY_KEY = "activityKey";
    public static final String SHIELDS_ACTIVITY = "shieldsActivity";
    public static final String BATTLE_ACTIVITY = "battleActivity";

    private String positiveButtonText = "Повесить щит";

    private static final int DEFAULT_SHIELD_ID = -1;

    private EditText mShieldCostEditText;
    private Spinner mShieldListSpinner;
    private AlertDialog.Builder mDialogBuilder;

    private int mShieldId = DEFAULT_SHIELD_ID;

    private Activity mActivity;
    private AppDatabase mDb;
    private ShieldEntry mSelectedShieldEntry;
    private int mPersonalShieldCount;
    private int mGroupShieldCount;

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

        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_shield, null);
        mShieldCostEditText = view.findViewById(R.id.et_shield_cost);
        mShieldListSpinner = view.findViewById(R.id.sp_shields_list);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SHIELD_ID)) {
            mShieldId = savedInstanceState.getInt(INSTANCE_SHIELD_ID, DEFAULT_SHIELD_ID);
        }

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_SHIELD_ID)) {

            positiveButtonText = "Усилить щит";
            mShieldListSpinner.setVisibility(View.INVISIBLE);

            if (mShieldId == DEFAULT_SHIELD_ID) {

                mShieldId = bundle.getInt(EXTRA_SHIELD_ID);
            }
        }

        List<ShieldEntry> shieldEntries = ShieldEntryBuilder.getShieldList(mActivity);
        if (bundle != null && bundle.containsKey(ACTIVITY_KEY)) {

            String key = bundle.getString(ACTIVITY_KEY);

            if (key.equals(SHIELDS_ACTIVITY)) {
                shieldEntries = ShieldEntryBuilder.getPersonalShieldList(mActivity);
            }
        }

        mDialogBuilder = new AlertDialog.Builder(mActivity);

        final ShieldArrayAdapter shieldArrayAdapter = new ShieldArrayAdapter(mActivity, shieldEntries);
        shieldArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShieldListSpinner.setAdapter(shieldArrayAdapter);
        mShieldListSpinner.setOnItemSelectedListener(this);

        mDialogBuilder.setTitle("Щит")
                .setMessage("Укажите щит и его силу")
                .setView(view)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShield();
                        mListener.onDialogPositiveClick(AddShieldDialogFragment.this);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        setupVariables();

        return mDialogBuilder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(INSTANCE_SHIELD_ID, mShieldId);
        super.onSaveInstanceState(outState);
    }

    private void populateUI(ShieldEntry shieldEntry) {
        if (shieldEntry == null) {
            return;
        }
        mDialogBuilder.setTitle("Щит: " + shieldEntry.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedShieldEntry = (ShieldEntry) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addShield() {

        int personalShieldLimit = PreferenceUtilities.getPersonalShieldLimit(mActivity);

        if (mSelectedShieldEntry.getTarget().equals(ShieldEntryBuilder.TARGET_PERSONAL)
                && mPersonalShieldCount >= personalShieldLimit) {
            Toast.makeText(mActivity, "Достигнут лимит персональных щитов", Toast.LENGTH_LONG).show();
            return;
        } else if (mSelectedShieldEntry.getTarget().equals(ShieldEntryBuilder.TARGET_GROUP)
                && mGroupShieldCount >= 1) {
            Toast.makeText(mActivity, "Можно поставить только один групповой щит", Toast.LENGTH_LONG).show();
            return;
        }

        String inputString = mShieldCostEditText.getText().toString();
        int power = 0;

        if (inputString.length() == 0) {
            Toast.makeText(mActivity, "Вы не указали силу щита", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            power = Integer.parseInt(inputString);
        } catch (Exception e) {
            Toast.makeText(mActivity, "Illegal arguments", Toast.LENGTH_LONG).show();
        }

        if (power < mSelectedShieldEntry.getMinCost()) {
            Toast.makeText(mActivity, "Недостаточно у.е. для создания щита", Toast.LENGTH_LONG).show();
            return;
        } else if (power > mSelectedShieldEntry.getMaxCost()) {
            Toast.makeText(mActivity, "Превышен лимит силы щита", Toast.LENGTH_LONG).show();
            return;
        }

        mSelectedShieldEntry.setPower(power);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDb.shieldDao().insertShield(mSelectedShieldEntry);
                } catch (Exception e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Нельзя повесить два одинаковых щита", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setupVariables() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mPersonalShieldCount = mDb.shieldDao().getShieldsCount(ShieldEntryBuilder.TARGET_PERSONAL);
                mGroupShieldCount = mDb.shieldDao().getShieldsCount(ShieldEntryBuilder.TARGET_GROUP);
            }
        });
    }

    /*public void addShield() {

        String errorMessage;

        final ShieldViewModel personalShieldViewModel = ViewModelProviders.of(this).get(ShieldViewModel.class);
        personalShieldViewModel.getPersonalShieldCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                personalShieldViewModel.getPersonalShieldCount().removeObserver(this);
                if (integer != null) mPersonalShieldCount = integer;
            }
        });

        final ShieldViewModel groupShieldViewModel = ViewModelProviders.of(this).get(ShieldViewModel.class);
        groupShieldViewModel.getGroupShieldCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                groupShieldViewModel.getGroupShieldCount().removeObserver(this);
                if (integer != null) mGroupShieldCount = integer;
            }
        });

        int personalShieldsLimit = PreferenceUtilities.getPersonalShieldLimit(mActivity);

        *//*ShieldEntry currentShield = (ShieldEntry) mShieldListSpinner.getSelectedItem();
        int minCost = currentShield.getMinCost();
        int maxCost = currentShield.getMaxCost();*//*

        if (mCurrentShield.isPersonalShield() && mPersonalShieldCount >= personalShieldsLimit) {
            errorMessage = "Достигнут лимит персональных щитов";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        } else if (!mCurrentShield.isPersonalShield() && mGroupShieldCount >= 1) {
            errorMessage = "Достигнут лимит групповых щитов";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        String input = mShieldCostEditText.getText().toString();
        final int inputInt;
        int minCost = mCurrentShield.getMinCost();
        int maxCost = mCurrentShield.getMaxCost();

        if (input.length() == 0) {
            errorMessage = "Вы не указали количество у.е. для щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
            return;
        } else {
            errorMessage = "Можно указывать только положительные, целые числа, больше нуля.";

            try {
                inputInt = Integer.parseInt(input) + mCurrentShield.getPower();

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

        mCurrentShield.setPower(inputInt);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mShieldId == DEFAULT_SHIELD_ID) {
                    try {
                        mDb.shieldDao().insertShield(mCurrentShield);
                    } catch (SQLException e) {
                        return;
                    }
                } else {
                    mCurrentShield.setId(mShieldId);
                    mDb.shieldDao().updateShield(mCurrentShield);
                }
            }
        });

        *//*try {
            mDb.shieldDao().insertShield(currentShield);
        } catch (SQLException e) {
            errorMessage = "Нельзя повесить два одинаковых щита";
            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
        }*//*
    }*/
}
