package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.adapter.BattleJournalAdapter;
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

    /* Для тестов */
//    private TextView mTestJournal;

    private Character mCharacter;
    private ShieldListAdapter mShieldListAdapter;
    private BattleJournalAdapter mBattleJournalAdapter;
    private Menu mMenu;

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

        mMagicPowerTextView.setOnClickListener(this);
        mBattleFormTextView.setOnClickListener(this);
        mNaturalDefenceTextView.setOnClickListener(this);

        /* Для тестов */
//        mTestJournal = findViewById(R.id.tv_test_journal);

        RecyclerView activeShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        activeShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShieldListAdapter = new ShieldListAdapter(getAllShields(null, null, null), this, this);
        activeShieldsRecyclerView.setAdapter(mShieldListAdapter);

        RecyclerView battleJournalRecyclerView = findViewById(R.id.rv_battle_journal);
        battleJournalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBattleJournalAdapter = new BattleJournalAdapter(getAllBattleJournalMessage(), this);
        battleJournalRecyclerView.setAdapter(mBattleJournalAdapter);

        mCharacter = new Character(this);
        String type = mCharacter.getCharacterType();
        if (type.equals(getString(R.string.pref_type_value_vampire)) && TransformUtil.getCurrentForm(this).equals("человек")) {
            insertMessageIntoBattleJournal(TransformUtil.makeTransform(this));
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

    private Cursor getAllBattleJournalMessage() {

        return getContentResolver().query(NotATryContract.BattleJournalEntry.CONTENT_URI,
                null, null, null, "_id DESC");
    }

    private int getReactionCount() {

        Cursor cursor = getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        int returnReactionCount = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER));
        cursor.close();

        return returnReactionCount;
    }

    private void useReaction() {
        int reactionCount = getReactionCount();

        if (reactionCount == 0) {
            insertMessageIntoBattleJournal("У вас закончились реакции");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, --reactionCount);
            getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues,
                    null, null);
            insertMessageIntoBattleJournal("Ручное использование реакции, выполните необходимые действия");
        }
    }

    private void removeShield(long id) {

        String stringId = String.valueOf(id);
        Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

    private void insertMessageIntoBattleJournal(String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.BattleJournalEntry.COLUMN_SYSTEM_MESSAGE, message);
        getContentResolver().insert(NotATryContract.BattleJournalEntry.CONTENT_URI, contentValues);
        mBattleJournalAdapter.swapCursor(getAllBattleJournalMessage());
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
        builder.append("Скан щитов:\n");

        Cursor cursor = getAllShields(null, null, null);
        if (cursor.getCount() == 0) {
            builder.append("Щиты отсутствуют");
            insertMessageIntoBattleJournal(builder.toString());
            return;
        }

        String[] columns = new String[]{"MAX(" + NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + ") as maxRange"};
        cursor = getAllShields(columns, null, null);
        cursor.moveToFirst();
        int columnMaxRange = cursor.getColumnIndex("maxRange");
        int maxRange = cursor.getInt(columnMaxRange);
        cursor.close();

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
                    insertMessageIntoBattleJournal("Скан щитов:\nЗондирование ничего не показывает");
                    return;
                }

                shieldLevel = getShieldLevel(shieldCost);

                builder.append(shieldName).append(": ").append(shieldLevel).append("\n");

                if (shieldType.equals("маг") || shieldType.equals("унив")) {
                    isStop = true;
                }
            }

            if (isStop) {
                insertMessageIntoBattleJournal(builder.toString());
                return;
            }
            cursor.close();
        }

        insertMessageIntoBattleJournal(builder.toString());
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

    private void resetNaturalDefence() {
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

    private void resetNaturalMentalDefence() {
        int currentNaturalMentalDefence;
        if (mCharacter.getCharacterType().equals(getString(R.string.pref_type_value_werewolf))
                && TransformUtil.getCurrentForm(this).equals("человек")) {
            currentNaturalMentalDefence = 0;
        } else {
            currentNaturalMentalDefence = mCharacter.getCharacterNaturalMentalDefence();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_MENTAL_DEFENCE, currentNaturalMentalDefence);
        getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues,
                null, null);
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

    private void resetBattleJournal() {
        getContentResolver().delete(NotATryContract.BattleJournalEntry.CONTENT_URI, null, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.BattleJournalEntry.COLUMN_SYSTEM_MESSAGE, "Бой окончен, журнал очищен");
        getContentResolver().insert(NotATryContract.BattleJournalEntry.CONTENT_URI, contentValues);
        mBattleJournalAdapter.swapCursor(getAllBattleJournalMessage());
    }

    private void resetReactionCount() {
        int reactionCount;

        if (TransformUtil.getCurrentForm(this).equals("человек")) {
            reactionCount = 1;
        } else {
            reactionCount = mCharacter.getCharacterReactionsNumber();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, reactionCount);
        getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues,
                null, null);

        MenuItem menuItem = mMenu.findItem(R.id.action_use_reaction);
        menuItem.setTitle("использовать реакцию (" + getReactionCount() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.battle_menu, menu);

        MenuItem item = menu.getItem(0);
        item.setTitle("использовать реакцию (" + getReactionCount() + ")");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_finish_battle) {
            resetNaturalDefence();
            resetShields();
            resetBattleJournal();
            resetReactionCount();
            resetNaturalMentalDefence();
            Toast.makeText(this, "Бой окончен, сброс параметров", Toast.LENGTH_LONG).show();
        } else if (itemId == R.id.action_reset_reactions) {
            resetReactionCount();
        } else if (itemId == R.id.action_use_reaction) {
            useReaction();
            item.setTitle("использовать реакцию (" + getReactionCount() + ")");
            mBattleJournalAdapter.swapCursor(getAllBattleJournalMessage());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        if (dialogFragment instanceof AddShieldDialogFragment || dialogFragment instanceof UpdateShieldDialogFragment) {
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
        } else if (dialogFragment instanceof DamageDialogFragment) {
            DamageDialogFragment damageDialogFragment = (DamageDialogFragment) dialogFragment;
            if (damageDialogFragment.mShouldBeTransformed) {
                insertMessageIntoBattleJournal(TransformUtil.makeTransform(this));
                mBattleFormTextView.setText(TransformUtil.getCurrentForm(this));
                mShieldListAdapter.swapCursor(getAllShields(null, null, null));
            }
            mShieldListAdapter.swapCursor(getAllShields(null, null, null));
            mBattleJournalAdapter.swapCursor(getAllBattleJournalMessage());
            MenuItem item = mMenu.getItem(0);
            item.setTitle("использовать реакцию (" + getReactionCount() + ")");
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
                insertMessageIntoBattleJournal(TransformUtil.makeTransform(this));
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
