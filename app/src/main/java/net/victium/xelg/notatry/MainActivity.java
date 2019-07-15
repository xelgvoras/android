package net.victium.xelg.notatry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.victium.xelg.notatry.adapter.DuskLayersAdapter;
import net.victium.xelg.notatry.databinding.ActivityMainBinding;
import net.victium.xelg.notatry.utilities.MainActivityInfoBuilder;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;
import net.victium.xelg.notatry.viewModel.DefenceViewModel;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    ActivityMainBinding mBinding;
    MainActivityInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mInfo = MainActivityInfoBuilder.createMainActivityInfo(this);
        displayMainInfo(mInfo);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mBinding.bShields.setOnClickListener(this);
        mBinding.bBattle.setOnClickListener(this);
        mBinding.bTravel.setOnClickListener(this);
    }

    private void displayMainInfo(MainActivityInfo info) {
        mBinding.tvCharacterFullName.setText(info.fullName);
        mBinding.tvCharacterPersonalInfo.setText(info.personalInfo);
        mBinding.tvCharacterMagicPower.setText(info.magicPower);
        mBinding.tvCharacterDefence.setText(info.defence);
        mBinding.tvCharacterDetails.setText(info.characterDetail);

        if (PreferenceUtilities.isCharacterVop(this)) {
            setVopsInfoVisible();
        } else {
            setVopsInfoInvisible();
        }
    }

    private void setVopsInfoVisible() {
        String stringBattleForm = "текущая форма: " + PreferenceUtilities.getBattleForm(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.tvCharacterBattleForm.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mBinding.tvCharacterBattleForm.setVisibility(View.VISIBLE);
        mBinding.tvCharacterBattleForm.setLayoutParams(params);
        mBinding.tvCharacterBattleForm.setText(stringBattleForm);
    }

    private void setVopsInfoInvisible() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.tvCharacterBattleForm.getLayoutParams();
        params.height = 0;
        mBinding.tvCharacterBattleForm.setVisibility(View.INVISIBLE);
        mBinding.tvCharacterBattleForm.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pasport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_import_export) {
            Intent intent = new Intent(this, ImportExportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivityInfo info = MainActivityInfoBuilder.createMainActivityInfo(this);

        mBinding.tvCharacterMagicPower.setText(info.magicPower);
        mBinding.tvCharacterDefence.setText(info.defence);

        if (PreferenceUtilities.isCharacterVop(this)) {
            setVopsInfoVisible();
        } else {
            setVopsInfoInvisible();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button clickedButton = (Button) v;
            int buttonId = clickedButton.getId();

            if (buttonId == R.id.b_shields) {
                Intent intent = new Intent(this, ShieldsActivity.class);
                startActivity(intent);
            } else if (buttonId == R.id.b_battle) {
                Intent intent = new Intent(this, BattleActivity.class);
                startActivity(intent);
            } else if (buttonId == R.id.b_travel) {
                Intent intent = new Intent(this, TravelActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        mInfo = MainActivityInfoBuilder.createMainActivityInfo(this);
        displayMainInfo(mInfo);
    }
}