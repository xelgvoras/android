package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;

import java.util.ArrayList;

public class ShieldsActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener {

    private ShieldListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private RecyclerView mActiveShieldsRecyclerView;
    private TextView mCurrentPower;
    private EditText mNewValuePower;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        mActiveShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mCurrentPower = findViewById(R.id.tv_current_power);
        mNewValuePower = findViewById(R.id.et_new_value_power);

        mActiveShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);

        mDb = notATryDbHelper.getWritableDatabase();

        mCursor = getAllShields();

        mAdapter = new ShieldListAdapter(mCursor, this);
        mActiveShieldsRecyclerView.setAdapter(mAdapter);

        mCursor.close();
        mCursor = getCharacterStatus();
        setupCurrentPower(mCursor);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();
                removeShield(id);
                mAdapter.swapCursor(getAllShields());
            }
        }).attachToRecyclerView(mActiveShieldsRecyclerView);

        // TODO(11) Сделать изменение резерва через диалоговое окно
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.swapCursor(getAllShields());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }

    private Cursor getAllShields() {
        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC"
        );
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

    private void setupCurrentPower(Cursor cursor) {
        cursor.moveToFirst();
        int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        int powerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT));
        mCurrentPower.setText(String.format("Резерв силы: %s/%s", String.valueOf(currentPower), String.valueOf(powerLimit)));
    }

    private void removeShield(long id) {

        mDb.delete(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                NotATryContract.ActiveShieldsEntry._ID + "=" + id,
                null
        );
    }

    public void onClickAddShield(View view) {

        DialogFragment dialogFragment = new AddShieldDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "AddShieldDialogFragment");
    }

    public void onClickEditCurrentPower(View v) {

        String errorMessage;
        String newValueString = mNewValuePower.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String powerKey = getString(R.string.pref_power_key);
        String powerDefault = getString(R.string.pref_power_default);
        String powerLimitString = sharedPreferences.getString(powerKey, powerDefault);
        int powerLimit = Integer.parseInt(powerLimitString);

        String typeKey = getString(R.string.pref_type_key);
        String typeDefault = getString(R.string.pref_type_value_mag);
        String type = sharedPreferences.getString(typeKey, typeDefault);

        ArrayList<String> vops = new ArrayList<>();
        vops.add(getString(R.string.pref_type_value_flipflop));
        vops.add(getString(R.string.pref_type_value_vampire));
        vops.add(getString(R.string.pref_type_value_werewolf));
        vops.add(getString(R.string.pref_type_value_werewolf_mag));

        Cursor cursor = getCharacterStatus();
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry._ID));
        int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));

        try {
            int newValueInt = Integer.parseInt(newValueString);
            int newPower = currentPower + newValueInt;

            if (newPower < 0) {
                newPower = 0;
            } else if (newPower > powerLimit) {
                newPower = powerLimit;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER, newPower);

            if (vops.contains(type)) {
                contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, newPower);
            }

            mDb.update(
                    NotATryContract.CharacterStatusEntry.TABLE_NAME,
                    contentValues,
                    NotATryContract.CharacterStatusEntry._ID + "=" + id,
                    null
            );

            mNewValuePower.clearFocus();
            mNewValuePower.getText().clear();

            setupCurrentPower(getCharacterStatus());
        } catch (Exception ex) {
            errorMessage = "Введите число со знаком плюс или минус";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        mAdapter.swapCursor(getAllShields());
    }
}
