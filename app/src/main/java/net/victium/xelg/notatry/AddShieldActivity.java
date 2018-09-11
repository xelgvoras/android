package net.victium.xelg.notatry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import net.victium.xelg.notatry.adapter.ShieldArrayAdapter;
import net.victium.xelg.notatry.data.Shield;

import java.util.ArrayList;
import java.util.Collection;

public class AddShieldActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText mShieldCost;
    private Spinner mShieldList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shield);

        mShieldCost = findViewById(R.id.et_shield_cost);
        mShieldList = findViewById(R.id.sp_shields_list);

        ArrayList<Shield> shieldArrayList = createShieldArrayList();
        ShieldArrayAdapter shieldArrayAdapter = new ShieldArrayAdapter(this, shieldArrayList);
        shieldArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShieldList.setAdapter(shieldArrayAdapter);
        mShieldList.setOnItemSelectedListener(this);
    }

    public void onClickAddShield(View view) {
        String input = mShieldCost.getText().toString();
        Shield currentShield = (Shield) mShieldList.getSelectedItem();
    }

    private ArrayList<Shield> createShieldArrayList() {
        ArrayList<Shield> shieldArrayList = new ArrayList<>();
        shieldArrayList.add(new Shield(
                getString(R.string.shields_mag_shield),
                "унив",
                1,
                1,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_clean_mind),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_will_barrier),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_sphere_of_tranquility),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_icecrown),
                "мент",
                0,
                0,
                true,
                true,
                0
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_concave_shield),
                "физ",
                0,
                2,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_sphere_of_negation),
                "маг",
                2,
                0,
                false,
                true,
                2
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_pair_shield),
                "унив",
                1,
                1,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_cloack_of_darkness),
                "особый",
                0,
                0,
                false,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                true,
                3
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_highest_mag_shield),
                "унив",
                2,
                2,
                true,
                true,
                1
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_big_rainbow_sphere),
                "унив",
                2,
                2,
                true,
                false,
                4
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_protective_dome),
                "унив",
                2,
                2,
                true,
                false,
                5
        ));
        shieldArrayList.add(new Shield(
                getString(R.string.shields_crystal_shield),
                "физ",
                0,
                100,
                false,
                false,
                5
        ));

        return shieldArrayList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*private class Shield {

        public String name;
        public String type;
        public int magicDefenceMultiplier;
        public int physicDefenceMultiplier;
        public boolean hasMentalDefence;
        public boolean isPersonalShield;
        public int range;

        public Shield(String name, String type, int magicDefenceMultiplier, int physicDefenceMultiplier, boolean hasMentalDefence, boolean isPersonalShield, int range) {
            this.name = name;
            this.type = type;
            this.magicDefenceMultiplier = magicDefenceMultiplier;
            this.physicDefenceMultiplier = physicDefenceMultiplier;
            this.hasMentalDefence = hasMentalDefence;
            this.isPersonalShield = isPersonalShield;
            this.range = range;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", name, type);
        }
    }*/
}
