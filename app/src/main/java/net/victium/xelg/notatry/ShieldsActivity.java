package net.victium.xelg.notatry;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import net.victium.xelg.notatry.data.NotATryContract;

public class ShieldsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ShieldsActivity.class.getSimpleName();
    private static final int ACTIVE_SHIELD_LOADER_ID = 0;

    private ActiveShieldsAdapter mAdapter;
    RecyclerView mActiveShieldsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        mActiveShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mActiveShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ActiveShieldsAdapter(this);
        mActiveShieldsRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();

                String stringId = String.valueOf(id);
                Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(ACTIVE_SHIELD_LOADER_ID, null, ShieldsActivity.this);
            }
        }).attachToRecyclerView(mActiveShieldsRecyclerView);

        FloatingActionButton fabAddShield = findViewById(R.id.fab_add_shield);

        fabAddShield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addShieldIntent = new Intent(ShieldsActivity.this, AddShieldActivity.class);
                startActivity(addShieldIntent);
            }
        });

        getSupportLoaderManager().initLoader(ACTIVE_SHIELD_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(ACTIVE_SHIELD_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mActiveShieldData = null;

            @Override
            protected void onStartLoading() {
                if (null != mActiveShieldData) {
                    deliverResult(mActiveShieldData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mActiveShieldData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
