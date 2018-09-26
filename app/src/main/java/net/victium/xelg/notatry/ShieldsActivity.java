package net.victium.xelg.notatry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateCurrentPowerDialogFragment;

public class ShieldsActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        UpdateCurrentPowerDialogFragment.UpdateCurrentPowerDialogListener,
        View.OnClickListener {

    private ShieldListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private TextView mCurrentPower;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        RecyclerView activeShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mCurrentPower = findViewById(R.id.tv_current_power);
        mCurrentPower.setOnClickListener(this);

        activeShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);

        mDb = notATryDbHelper.getWritableDatabase();

        mCursor = getAllShields();

        mAdapter = new ShieldListAdapter(mCursor, this);
        activeShieldsRecyclerView.setAdapter(mAdapter);

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
        }).attachToRecyclerView(activeShieldsRecyclerView);

        // COMPLETED(11) Сделать изменение резерва через диалоговое окно
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        mAdapter.swapCursor(getAllShields());
    }

    @Override
    public void onDialogClick(DialogFragment dialogFragment) {
        setupCurrentPower(getCharacterStatus());
    }

    @Override
    public void onClick(View v) {

        if (v instanceof TextView) {
            TextView tappedTextView = (TextView) v;
            int textViewId = tappedTextView.getId();

            if (textViewId == R.id.tv_current_power) {
                DialogFragment dialogFragment = new UpdateCurrentPowerDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateCurrentPowerDialogFragment");
            }
        }
    }
}
