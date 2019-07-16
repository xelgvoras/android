package net.victium.xelg.notatry;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.victium.xelg.notatry.adapter.ShieldListAdapter;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.dataBinding.ShieldsActivityInfo;
import net.victium.xelg.notatry.dataBinding.ShieldsActivityInfoBuilder;
import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.AppExecutors;
import net.victium.xelg.notatry.database.ShieldEntry;
import net.victium.xelg.notatry.databinding.ActivityShieldsBinding;
import net.victium.xelg.notatry.dialog.AddShieldDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateCurrentPowerDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateNaturalDefenceDialogFragment;
import net.victium.xelg.notatry.dialog.UpdateShieldDialogFragment;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;
import net.victium.xelg.notatry.viewModel.ShieldViewModel;

import java.util.List;

public class ShieldsActivity extends AppCompatActivity implements
        AddShieldDialogFragment.AddShieldDialogListener,
        UpdateCurrentPowerDialogFragment.UpdateCurrentPowerDialogListener,
        View.OnClickListener,
        ShieldListAdapter.ItemClickListener,
        UpdateShieldDialogFragment.UpdateShieldDialogListener,
        UpdateNaturalDefenceDialogFragment.UpdateNaturalDefenceDialogListener {

    /*private TextView mCurrentPowerTextView;
    private TextView mBattleFormTextView;
    private TextView mNaturalDefenceTextView;*/
    private ActivityShieldsBinding mBinding;
    private ShieldsActivityInfo mInfo;
    private ShieldListAdapter mAdapter;
    private AppDatabase mDb;

    /*private ShieldListAdapter mAdapter;*/
    // COMPLETED(17) Добавить трансформацию в боевую форму

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shields);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shields);
        mInfo = ShieldsActivityInfoBuilder.createShieldsActivityInfo(this);

        displayShieldsInfo(mInfo);

        mBinding.rvActiveShields.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShieldListAdapter(this, this);
        mBinding.rvActiveShields.setAdapter(mAdapter);

        /*RecyclerView activeShieldsRecyclerView = findViewById(R.id.rv_active_shields);
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
        }*/

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<ShieldEntry> shields = mAdapter.getShields();
                        mDb.shieldDao().deleteShield(shields.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mBinding.rvActiveShields);

        // COMPLETED(11) Сделать изменение резерва через диалоговое окно
        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void displayShieldsInfo(ShieldsActivityInfo info) {
        mBinding.tvCurrentPower.setText(info.magicPower);
        mBinding.tvBattleForm.setText(info.battleForm);
        mBinding.tvNaturalDefence.setText(info.naturalDefence);

        if (PreferenceUtilities.isCharacterVop(this)) {
            setVopsInfoVisible();
        } else {
            setVopsInfoInvisible();
        }
    }

    private void setVopsInfoVisible() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.tvBattleForm.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mBinding.tvBattleForm.setVisibility(View.VISIBLE);
        mBinding.tvBattleForm.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) mBinding.tvNaturalDefence.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mBinding.tvNaturalDefence.setVisibility(View.VISIBLE);
        mBinding.tvNaturalDefence.setLayoutParams(params);
    }

    private void setVopsInfoInvisible() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.tvBattleForm.getLayoutParams();
        params.height = 0;
        mBinding.tvBattleForm.setVisibility(View.INVISIBLE);
        mBinding.tvBattleForm.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) mBinding.tvNaturalDefence.getLayoutParams();
        params.height = 0;
        mBinding.tvNaturalDefence.setVisibility(View.INVISIBLE);
        mBinding.tvNaturalDefence.setLayoutParams(params);
    }

    private void setupShieldViewModel() {
        ShieldViewModel viewModel = ViewModelProviders.of(this).get(ShieldViewModel.class);
        viewModel.getShields().observe(this, new Observer<List<ShieldEntry>>() {
            @Override
            public void onChanged(List<ShieldEntry> shieldEntries) {
                mAdapter.setShields(shieldEntries);
            }
        });
    }

    /*private Cursor getAllShields() {
        String sortOrder = NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC";
        return getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, null, null, sortOrder);
    }*/

    /*private Cursor getCharacterStatus() {
        return getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
    }*/

    /*private void setupCurrentPower(@NonNull Cursor cursor) {
        cursor.moveToFirst();
        int currentPower = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        int powerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_POWER_LIMIT));
        mCurrentPowerTextView.setText(String.format("Резерв силы: %s/%s", String.valueOf(currentPower), String.valueOf(powerLimit)));
        cursor.close();
    }*/

    /*private void removeShield(long id) {
        Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();

        getContentResolver().delete(uri, null, null);
    }*/

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
            } else if (textViewId == R.id.tv_battle_form) {
                Toast.makeText(this, "Выполнена трансформация", Toast.LENGTH_LONG).show();
                String battleForm = PreferenceUtilities.getBattleForm(this);
                if (battleForm.equals("человек")) {
                    PreferenceUtilities.setBattleForm(this, "боевая форма");
                } else {
                    PreferenceUtilities.setBattleForm(this, "человек");
                }
                mInfo.battleForm = PreferenceUtilities.getBattleForm(this);
                displayShieldsInfo(mInfo);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.shieldDao().deleteAllShields();
                    }
                });
            } else if (textViewId == R.id.tv_natural_defence) {
                DialogFragment dialogFragment = new UpdateNaturalDefenceDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "UpdateNaturalDefenceDialogFragment");
            }
        }
    }

    @Override
    public void onItemClickListener(int itemId) {
        Bundle args = new Bundle();
        String stringItemId = String.valueOf(itemId);
        args.putString(AddShieldDialogFragment.EXTRA_SHIELD_ID, stringItemId);

        DialogFragment dialogFragment = new UpdateShieldDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "UpdateShieldPower");
    }
}
