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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.data.Shield;

import java.util.ArrayList;

public class AddShieldDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText mShieldCost;
    private Spinner mShieldList;
    private SQLiteDatabase mDb;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_shield_dialog, null);
        mShieldCost = view.findViewById(R.id.et_shield_cost);
        mShieldList = view.findViewById(R.id.sp_shields_list);

        ArrayList<Shield> shieldArrayList = createShieldArrayList();
        final ShieldArrayAdapter shieldArrayAdapter = new ShieldArrayAdapter(getActivity(), shieldArrayList);
        shieldArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShieldList.setAdapter(shieldArrayAdapter);
        mShieldList.setOnItemSelectedListener(this);

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(getActivity());
        mDb = notATryDbHelper.getWritableDatabase();

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

    private ArrayList<Shield> createShieldArrayList() {
        ArrayList<Shield> shieldArrayList = new ArrayList<>();
        shieldArrayList.add(new Shield(
                getString(R.string.shields_mag_shield),
                "унив",
                1,
                1,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_clean_mind),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_will_barrier),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_sphere_of_tranquility),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_icecrown),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_concave_shield),
                "физ",
                0,
                2,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_sphere_of_negation),
                "маг",
                2,
                0,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_pair_shield),
                "унив",
                1,
                1,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_cloack_of_darkness),
                "маг",
                0,
                0,
                false,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                true,
                3
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_highest_mag_shield),
                "унив",
                2,
                2,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_big_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_protective_dome),
                "унив",
                2,
                2,
                true,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_crystal_shield),
                "физ",
                0,
                100,
                false,
                false,
                5
        ));

        return shieldArrayList;
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
        cursor = mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + " is ?",
                new String[]{"персональный"},
                null,
                null,
                null
        );

        int personalShieldsCount = cursor.getCount();
        cursor.close();

        cursor = mDb.query(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        int personalShieldsLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT));
        cursor.close();

        cursor = mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + " is ?",
                new String[]{"групповой"},
                null,
                null,
                null
        );

        int groupShieldsCount = cursor.getCount();
        cursor.close();

        String input = mShieldCost.getText().toString();
        int inputInt;

        if (input.length() == 0) {
            errorMessage = "Вы не указали количество у.е. для щита";
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            return;
        } else {
            errorMessage = "Можно указывать только положительные, целые числа, больше нуля.";

            try {
                inputInt = Integer.parseInt(input);

                if (inputInt <= 0) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        }

        Shield currentShield = (Shield) mShieldList.getSelectedItem();

        String name = currentShield.name;
        String type = currentShield.type;
        int magicDefence = currentShield.magicDefenceMultiplier * inputInt;
        int physicDefence = currentShield.physicDefenceMultiplier * inputInt;
        int mentalDefence = 0;
        if (currentShield.hasMentalDefence) mentalDefence = 1;
        String target = "персональный";
        if (!currentShield.isPersonalShield) target = "групповой";
        int range = currentShield.range;

        if (target.equals("персональный")) {

            if (personalShieldsCount >= personalShieldsLimit) {
                errorMessage = "Достигнут лимит персональных щитов";
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
        } else {

            if (groupShieldsCount >= 1) {
                errorMessage = "Достигнут лимит групповых щитов";
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
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

        long insertedRowCount = mDb.insert(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                contentValues
        );

        if (insertedRowCount <= 0) {
            errorMessage = "Нельзя повесить два одинаковых щита";
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
