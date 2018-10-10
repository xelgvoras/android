package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.CharacterPreferences;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;
import net.victium.xelg.notatry.dialog.DamageDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateCurrentPowerDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateNaturalDefenceDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateShieldDialogFragment;
import net.victium.xelg.notatry.utilities.ShieldUtil;
import net.victium.xelg.notatry.utilities.TransformUtil;

public class BattleActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        DamageDialogFragment.DamageDialogListener,
        UpdateCurrentPowerDialogFragment.UpdateCurrentPowerDialogListener,
        View.OnClickListener,
        ShieldListAdapter.ShieldListAdapterOnClickHandler,
        UpdateShieldDialogFragment.UpdateShieldDialogListener,
        UpdateNaturalDefenceDialogFragment.UpdateNaturalDefenceDialogListener {

    private TextView mFullNameTextView;
    private TextView mBattleFormTextView;
    private TextView mPersonalInfoTextView;
    private TextView mMagicPowerTextView;
    private TextView mNaturalDefenceTextView;
    // TODO(16) Реализовать журнал боя, новое сообщение должно быть сверху
    // Между сообщениями должен быть разделитель
    // В сообщении должна быть информация о входящем воздействии
    private RecyclerView mBattleJournalRecyclerView;

    /* Для тестов */
    private TextView mTestJournal;

    private Character mCharacter;
    private ShieldListAdapter mShieldListAdapter;

    // COMPLETED(bug) В начале боя Вампиры автоматически трансформирутся
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        mFullNameTextView = findViewById(R.id.tv_character_full_name);
        mBattleFormTextView = findViewById(R.id.tv_character_battle_form);
        mPersonalInfoTextView = findViewById(R.id.tv_character_personal_info);
        mMagicPowerTextView = findViewById(R.id.tv_current_power);
        mNaturalDefenceTextView = findViewById(R.id.tv_natural_defence);
        mBattleJournalRecyclerView = findViewById(R.id.rv_battle_journal);

        mMagicPowerTextView.setOnClickListener(this);
        mBattleFormTextView.setOnClickListener(this);
        mNaturalDefenceTextView.setOnClickListener(this);

        /* Для тестов */
        mTestJournal = findViewById(R.id.tv_test_journal);

        RecyclerView activeShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        activeShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShieldListAdapter = new ShieldListAdapter(getAllShields(null, null, null), this, this);
        activeShieldsRecyclerView.setAdapter(mShieldListAdapter);

        mCharacter = new Character(this);
        String type = mCharacter.getCharacterType();
        if (type.equals(getString(R.string.pref_type_value_vampire)) && TransformUtil.getCurrentForm(this).equals("человек")) {
            mTestJournal.setText(TransformUtil.makeTransform(this));
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
        }

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
        }).attachToRecyclerView(activeShieldsRecyclerView);
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
        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, this));

        setupNaturalDefence(getCharacterStatus());

        if (mCharacter.isCharacterVop()) {
            setVopsInfoVisible();
        } else {
            setVopsInfoInvisible();
        }
    }

    private void setVopsInfoVisible() {
        String stringBattleForm = TransformUtil.getCurrentForm(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBattleFormTextView.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mBattleFormTextView.setVisibility(View.VISIBLE);
        mBattleFormTextView.setLayoutParams(params);
        mBattleFormTextView.setText(stringBattleForm);

        Cursor cursor = getCharacterStatus();
        cursor.moveToFirst();
        int naturalDefence = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));
        cursor.close();

        params = (LinearLayout.LayoutParams) mNaturalDefenceTextView.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mNaturalDefenceTextView.setVisibility(View.VISIBLE);
        mNaturalDefenceTextView.setLayoutParams(params);
        mNaturalDefenceTextView.setText(String.format("Естественная защита: %s", String.valueOf(naturalDefence)));
    }

    private void setVopsInfoInvisible() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBattleFormTextView.getLayoutParams();
        params.height = 0;
        mBattleFormTextView.setVisibility(View.INVISIBLE);
        mBattleFormTextView.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) mNaturalDefenceTextView.getLayoutParams();
        params.height = 0;
        mNaturalDefenceTextView.setVisibility(View.INVISIBLE);
        mNaturalDefenceTextView.setLayoutParams(params);
    }

    private void setupNaturalDefence(@NonNull Cursor cursor) {
        cursor.moveToFirst();
        int naturalDefence = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));
        mNaturalDefenceTextView.setText(String.format("Естественная защита: %s", String.valueOf(naturalDefence)));
        cursor.close();
    }

    private Cursor getCharacterStatus() {

        return getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
    }

    private Cursor getAllShields(String[] columns, String selection, String[] selectionArgs) {

        return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                columns, selection, selectionArgs, NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC");
    }

    private void removeShield(long id) {

        String stringId = String.valueOf(id);
        Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

    public void onClickAddShield(View view) {

        DialogFragment dialogFragment = new AddShieldDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "AddShieldDialogFragment");
    }

    public void onClickCheckDamage(View view) {

        Bundle args = new Bundle();
        String battleForm = TransformUtil.getCurrentForm(this);
        args.putString("battleForm", battleForm);

        DialogFragment dialogFragment = new DamageDialogFragment();
        dialogFragment.setArguments(args);
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

    @NonNull
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

    private void reloadNaturalDefence() {
        Cursor cursor = getCharacterStatus();
        cursor.moveToFirst();
        int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, currentPower);
        getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues,
                null, null);

        setupNaturalDefence(getCharacterStatus());
    }

    private void resetShields() {

        int powerLimit = mCharacter.getCharacterPowerLimit();

        Cursor cursor = getAllShields(null, null, null);
        int columnId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int columnName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnCost = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST);
        int columnTarget = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_TARGET);

        if (cursor.moveToFirst()) {
            while (true) {
                String shieldId = cursor.getString(columnId);
                Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();

                String shieldTarget = cursor.getString(columnTarget);
                if (!shieldTarget.equals("персональный")) {
                    getContentResolver().delete(uri, null, null);
                } else {
                    String shieldName = cursor.getString(columnName);
                    int shieldCost = cursor.getInt(columnCost);
                    ShieldUtil.Shield shield = ShieldUtil.getShield(this, shieldName);

                    if (powerLimit < shield.getMinCost() || shieldCost < shield.getMinCost()) {
                        getContentResolver().delete(uri, null, null);
                    } else if (shieldCost > powerLimit) {
                        int newMagicDefence = shield.getMagicDefenceMultiplier() * powerLimit;
                        int newPhysicDefence = shield.getPhysicDefenceMultiplier() * powerLimit;

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, powerLimit);
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, newMagicDefence);
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, newPhysicDefence);

                        getContentResolver().update(uri, contentValues, null, null);
                    }
                }

                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }

        mShieldListAdapter.swapCursor(getAllShields(null, null, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.battle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_finish_battle) {
            reloadNaturalDefence();
            resetShields();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        if (dialogFragment instanceof AddShieldDialogFragment || dialogFragment instanceof UpdateShieldDialogFragment) {
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
        } else if (dialogFragment instanceof DamageDialogFragment) {
            DamageDialogFragment damageDialogFragment = (DamageDialogFragment) dialogFragment;
            mTestJournal.setText(damageDialogFragment.mResultSummary);
            if (damageDialogFragment.mShouldBeTransformed) {
                mTestJournal.setText(TransformUtil.makeTransform(this));
                mBattleFormTextView.setText(TransformUtil.getCurrentForm(this));
                mShieldListAdapter.swapCursor(getAllShields(null, null, null));
            }
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
            } else if (textViewId == R.id.tv_character_battle_form) {
                mTestJournal.setText(TransformUtil.makeTransform(this));
                mBattleFormTextView.setText(TransformUtil.getCurrentForm(this));
                mShieldListAdapter.swapCursor(getAllShields(null, null, null));
            } else if (textViewId == R.id.tv_natural_defence) {
                DialogFragment dialogFragment = new UpdateNaturalDefenceDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateNaturalDefenceDialogFragment");
            }
        }
    }

    @Override
    public void onDialogClick(DialogFragment dialogFragment) {

        mMagicPowerTextView.setText(CharacterPreferences.getCharacterMagicPower(mCharacter, this));
        setupNaturalDefence(getCharacterStatus());
    }

    @Override
    public void onClick(long itemId) {
        Bundle args = new Bundle();
        String stringItemId = String.valueOf(itemId);
        args.putString("itemId", stringItemId);

        DialogFragment dialogFragment = new UpdateShieldDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "UpdateShieldPower");
    }
}
