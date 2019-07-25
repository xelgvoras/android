package net.victium.xelg.notatry;

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

    private ActivityShieldsBinding mBinding;
    private ShieldsActivityInfo mInfo;
    private ShieldListAdapter mAdapter;
    private AppDatabase mDb;

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

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupShieldViewModel();
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

    public void onClickAddShield(View view) {

        Bundle args = new Bundle();
        args.putString(AddShieldDialogFragment.ACTIVITY_KEY, AddShieldDialogFragment.SHIELDS_ACTIVITY);

        DialogFragment dialogFragment = new AddShieldDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "AddShieldDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        setupShieldViewModel();
    }

    @Override
    public void onDialogClick(DialogFragment dialogFragment) {

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
        args.putInt(AddShieldDialogFragment.EXTRA_SHIELD_ID, itemId);
        args.putString(AddShieldDialogFragment.ACTIVITY_KEY, AddShieldDialogFragment.SHIELDS_ACTIVITY);

        DialogFragment dialogFragment = new AddShieldDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "UpdateShieldPower");
    }
}
