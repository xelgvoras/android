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

public class BattleActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        DamageDialogFragment.DamageDialogListener {

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
        mBattleJournalRecyclerView = findViewById(R.id.rv_battle_journal);
        mCheckActionButton = findViewById(R.id.bt_check_damage);
        mShieldScanButton = findViewById(R.id.bt_shield_scan);
        mAddShieldActionButton = findViewById(R.id.fab_add_shield);

        /* Для тестов */
        mTestJournal = findViewById(R.id.tv_test_journal);

        mActiveShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mActiveShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShieldListAdapter = new ShieldListAdapter(getAllShields(), this);
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
                mShieldListAdapter.swapCursor(getAllShields());
            }
        }).attachToRecyclerView(mActiveShieldsRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShieldListAdapter.swapCursor(getAllShields());
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        if (dialogFragment instanceof AddShieldDialogFragment) {
            mShieldListAdapter.swapCursor(getAllShields());
        } else if (dialogFragment instanceof DamageDialogFragment) {
            mTestJournal.setText(((DamageDialogFragment) dialogFragment).mResultSummary);
            mShieldListAdapter.swapCursor(getAllShields());
        }
    }
}
