package net.victium.xelg.notatry;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;

public class ShieldsActivity extends AppCompatActivity implements View.OnClickListener {

    private ShieldListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private RecyclerView mActiveShieldsRecyclerView;
    private FloatingActionButton mAddShieldButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        mActiveShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mAddShieldButton = findViewById(R.id.fab_add_shield);

        mAddShieldButton.setOnClickListener(this);

        mActiveShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);

        mDb = notATryDbHelper.getWritableDatabase();

        Cursor cursor = getAllShields();

        mAdapter = new ShieldListAdapter(cursor, this);

        mActiveShieldsRecyclerView.setAdapter(mAdapter);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.swapCursor(getAllShields());
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

    private boolean removeShield(long id) {
        return mDb.delete(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                NotATryContract.ActiveShieldsEntry._ID + "=" + id,
                null
        ) > 0;
    }

    @Override
    public void onClick(View v) {

        if (v instanceof FloatingActionButton) {
            FloatingActionButton clickedButton = (FloatingActionButton) v;
            int buttonId = v.getId();

            if (R.id.fab_add_shield == buttonId) {
                Intent intent = new Intent(this, AddShieldActivity.class);
                startActivity(intent);
            }
        }
    }
}
