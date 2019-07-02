package net.victium.xelg.notatry;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateCurrentPowerDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateNaturalDefenceDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateShieldDialogFragment;
import net.victium.xelg.notatry.utilities.TransformUtil;

public class ShieldsActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        UpdateCurrentPowerDialogFragment.UpdateCurrentPowerDialogListener,
        View.OnClickListener,
        ShieldListAdapter.ShieldListAdapterOnClickHandler,
        UpdateShieldDialogFragment.UpdateShieldDialogListener,
        UpdateNaturalDefenceDialogFragment.UpdateNaturalDefenceDialogListener {

    private TextView mCurrentPowerTextView;
    private TextView mBattleFormTextView;
    private TextView mNaturalDefenceTextView;

    private ShieldListAdapter mAdapter;
    // COMPLETED(17) Добавить трансформацию в боевую форму

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        RecyclerView activeShieldsRecyclerView = findViewById(R.id.rv_active_shields);
        mCurrentPowerTextView = findViewById(R.id.tv_current_power);
        mBattleFormTextView = findViewById(R.id.tv_character_battle_form);
        mNaturalDefenceTextView = findViewById(R.id.tv_natural_defence);

        mCurrentPowerTextView.setOnClickListener(this);
        mBattleFormTextView.setOnClickListener(this);
        mNaturalDefenceTextView.setOnClickListener(this);

        activeShieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShieldListAdapter(getAllShields(), this, this);
        activeShieldsRecyclerView.setAdapter(mAdapter);

        setupCurrentPower(getCharacterStatus());

        Character character = new Character(this);
        if (character.isCharacterVop()) {
            setVopsInfoVisible();
        } else {
            setVopsInfoInvisible();
        }

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

    private Cursor getAllShields() {
        String sortOrder = NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC";
        return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, null, null, sortOrder);
    }

    private Cursor getCharacterStatus() {
        return getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
    }

    private void setupCurrentPower(@NonNull Cursor cursor) {
        cursor.moveToFirst();
        int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        int powerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT));
        mCurrentPowerTextView.setText(String.format("Резерв силы: %s/%s", String.valueOf(currentPower), String.valueOf(powerLimit)));
        cursor.close();
    }

    private void removeShield(long id) {
        Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();

        getContentResolver().delete(uri, null, null);
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

        Cursor cursor = getCharacterStatus();
        cursor.moveToFirst();
        int naturalDefence = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));
        cursor.close();
        mNaturalDefenceTextView.setText(String.format("Естественная защита: %s", String.valueOf(naturalDefence)));
    }

    @Override
    public void onClick(View v) {

        if (v instanceof TextView) {
            TextView tappedTextView = (TextView) v;
            int textViewId = tappedTextView.getId();

            if (textViewId == R.id.tv_current_power) {
                DialogFragment dialogFragment = new UpdateCurrentPowerDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateCurrentPowerDialogFragment");
            } else if (textViewId == R.id.tv_character_battle_form) {
                Toast.makeText(this, TransformUtil.makeTransform(this), Toast.LENGTH_LONG).show();
                mBattleFormTextView.setText(TransformUtil.getCurrentForm(this));
                mAdapter.swapCursor(getAllShields());
            } else if (textViewId == R.id.tv_natural_defence) {
                DialogFragment dialogFragment = new UpdateNaturalDefenceDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateNaturalDefenceDialogFragment");
            }
        }
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
