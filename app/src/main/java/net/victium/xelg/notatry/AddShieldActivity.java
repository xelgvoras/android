package net.victium.xelg.notatry;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.data.NotATryContract;

public class AddShieldActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner mShieldListSpinner;
    private String mShieldName;
    private int mCost;
    private int mMagDef;
    private int mPhysDef;
    private int mMentDef;
    private int mRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shield);

        mShieldListSpinner = findViewById(R.id.sp_shield_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.shields_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShieldListSpinner.setAdapter(adapter);
        mShieldListSpinner.setOnItemSelectedListener(this);
    }

    public void onClickAddShield(View view) {

        EditText input = findViewById(R.id.et_shield_cost);
        if (0 == input.length()) {
            return;
        }

        mCost = Integer.parseInt(input.getText().toString());
        mMagDef = 0;
        mPhysDef = 0;
        mMentDef = 0;
        mRange = 1;

        switch (mShieldName) {
            case "Щит мага":
                mMagDef= mCost;
                mPhysDef = mCost;
                mMentDef = 1;
                mRange = 1;
                break;
            case "Чистый разум":
                mMentDef = 1;
                mRange = 0;
                break;
            case "Барьер воли":
                mMentDef = 1;
                mRange = 0;
                break;
            case "Сфера спокойствия":
                mMentDef = 1;
                mRange = 0;
                break;
            case "Ледяная кора":
                mMentDef = 1;
                mRange = 0;
                break;
            case "Вогнутый щит":
                mPhysDef = (mCost * 2);
                mRange = 2;
                break;
            case "Сфера отрицания":
                mMagDef = (mCost * 2);
                mRange = 2;
                break;
            case "Спаренный щит":
                mMagDef = mCost;
                mPhysDef = mCost;
                mMentDef = 1;
                mRange = 4;
                break;
            case "Плащ тьмы":
                mRange = 4;
                break;
            case "Радужная сфера":
                mMagDef = (mCost * 2);
                mPhysDef = (mCost * 2);
                mMentDef = 1;
                mRange = 3;
                break;
            case "Высший щит мага":
                mMagDef = (mCost * 2);
                mPhysDef = (mCost * 2);
                mMentDef = 1;
                mRange = 1;
                break;
            case "Большая радужная сфера":
                mMagDef = (mCost * 2);
                mPhysDef = (mCost * 2);
                mMentDef = 1;
                mRange = 4;
                break;
            case "Зашитный купол":
                mMagDef = (mCost * 2);
                mPhysDef = (mCost * 2);
                mMentDef = 1;
                mRange = 99;
                break;
            case "Хрустальный щит":
                mRange = 0;
                break;
                default:
                    break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_SHIELD_NAME, mShieldName);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, mCost);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, mMagDef);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, mPhysDef);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE, mMentDef);
        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_RANGE, mRange);

        Uri uri = getContentResolver().insert(NotATryContract.ActiveShieldsEntry.CONTENT_URI, contentValues);

        if (null != uri) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mShieldName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
