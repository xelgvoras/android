package net.victium.xelg.notatry;

import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.CharacterPreferences;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;
import net.victium.xelg.notatry.dialog.DamageDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateCurrentPowerDialogFragment;

public class BattleActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        DamageDialogFragment.DamageDialogListener,
        UpdateCurrentPowerDialogFragment.UpdateCurrentPowerDialogListener,
        View.OnClickListener {

    private TextView mFullNameTextView;
    private TextView mPersonalInfoTextView;
    private TextView mMagicPowerTextView;
    private RecyclerView mActiveShieldsRecyclerView;
    private RecyclerView mBattleJournalRecyclerView;
    private Button mCheckActionButton;
    private Button mShieldScanButton;
    private FloatingActionButton mAddShieldActionButton;

    /* Для тестов */
    private TextView mTestJournal;

    private SQLiteDatabase mDb;
    private Character mCharacter;
    private ShieldListAdapter mShieldListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(this);
        mDb = notATryDbHelper.getWritableDatabase();

        mFullNameTextView = findViewById(R.id.tv_character_full_name);
        mPersonalInfoTextView = findViewById(R.id.tv_character_personal_info);
        mMagicPowerTextView = findViewById(R.id.tv_current_power);
        mMagicPowerTextView.setOnClickListener(this);
        mBattleJournalRecyclerView = findViewById(R.id.rv_battle_journal);
        mCheckActionButton = findViewById(R.id.bt_check_damage);
        mShieldScanButton = findViewById(R.id.bt_shield_scan);
        mAddShieldActionButton = findViewById(R.id.fab_add_shield);

        /* Для тестов */
        mTestJournal = findViewById(R.id.tv_test_journal);

        mActiveShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mActiveShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShieldListAdapter = new ShieldListAdapter(getAllShields(null, null, null), this);
        mActiveShieldsRecyclerView.setAdapter(mShieldListAdapter);

        mCharacter = new Character(this);

        setupScreen();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeShield(id);
                mShieldListAdapter.swapCursor(getAllShields(null, null, null));
            }
        }).attachToRecyclerView(mActiveShieldsRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShieldListAdapter.swapCursor(getAllShields(null, null, null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupScreen() {

        mFullNameTextView.setText(CharacterPreferences.getCharacterNameAndAge(mCharacter));
        mPersonalInfoTextView.setText(CharacterPreferences.getPersonalInfo(mCharacter));
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, getCharacterStatus()));
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

    private Cursor getAllShields(String[] columns, String selection, String[] selectionArgs) {

        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC"
        );
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

    public void onClickCheckDamage(View view) {

        DialogFragment dialogFragment = new DamageDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "CheckDamageDialogFragment");
    }

    public void onClickScanShields(View view) {
        StringBuilder builder = new StringBuilder();

        String[] columns = new String[]{"MAX(" + NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + ") as maxRange"};
        Cursor cursor = getAllShields(columns, null, null);
        cursor.moveToFirst();
        int columnMaxRange = cursor.getColumnIndex("maxRange");
        int maxRange = cursor.getInt(columnMaxRange);

        for (int i = maxRange; i >= 0; i--) {
            boolean isStop = false;
            String[] selectionArgs = new String[]{String.valueOf(i)};
            cursor = getAllShields(null, NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + "=?", selectionArgs);
            int columnName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
            int columnType = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE);
            int columnCost = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST);
            String shieldName;
            String shieldType;
            int shieldCost;
            String shieldLevel;

            for (int j = 0; j < cursor.getCount(); j++) {
                cursor.moveToPosition(j);
                shieldName = cursor.getString(columnName);
                shieldType = cursor.getString(columnType);
                shieldCost = cursor.getInt(columnCost);

                if (shieldName.equals(getString(R.string.shields_cloack_of_darkness))) {
                    mTestJournal.setText("Зондирование ничего не показывает");
                    return;
                }

                shieldLevel = getShieldLevel(shieldCost);

                builder.append(shieldName).append(": ").append(shieldLevel).append("\n");

                if (shieldType.equals("маг") || shieldType.equals("унив")) {
                    isStop = true;
                }
            }

            if (isStop) {
                mTestJournal.setText(builder.toString());
                return;
            }
        }

        mTestJournal.setText(builder.toString());
    }

    private String getShieldLevel(int shieldCost) {

        if (shieldCost < 4) {
            return "7го уровня";
        } else if (shieldCost < 8) {
            return "7го или 6го уровня";
        } else if (shieldCost < 16) {
            return "6го или 5го уровня";
        } else if (shieldCost < 32) {
            return "5го или 4го уровня";
        } else if (shieldCost < 64) {
            return "4го или 3го уровня";
        } else if (shieldCost < 128) {
            return "3го или 2го уровня";
        } else if (shieldCost < 256) {
            return "2го или 1го уровня";
        } else if (shieldCost < 512) {
            return "1го уровня";
        } else {
            return "0го уровня";
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        if (dialogFragment instanceof AddShieldDialogFragment) {
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
        } else if (dialogFragment instanceof DamageDialogFragment) {
            mTestJournal.setText(((DamageDialogFragment) dialogFragment).mResultSummary);
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof TextView) {
            TextView clickedTextView = (TextView) v;
            int textViewId = clickedTextView.getId();

            if (textViewId == R.id.tv_current_power) {
                DialogFragment dialogFragment = new UpdateCurrentPowerDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateCurrentPowerDialogFragment");
            }
        }
    }

    @Override
    public void onDialogClick(DialogFragment dialogFragment) {
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, getCharacterStatus()));
    }
}
