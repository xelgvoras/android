package net.victium.xelg.notatry;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.utilities.TransformUtil;

import java.util.ArrayList;
import java.util.List;

public class TravelActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final double BASIC_SPEED = 0.25d; // км/мин
    static final double DISTANCE_CITY = 4d; // км
    static final int MEDIUM_ACCELERATION = 4;
    static final int HIGH_ACCELERATION = 8;

    NumberPicker mStartCoordsX;
    NumberPicker mStartCoordsY;
    NumberPicker mEndCoordsX;
    NumberPicker mEndCoordsY;
    TextView mTimeSubject;
    TextView mTimeObject;
    Spinner mCharacterDuskLayerSpinner;
    Spinner mTargetDuskLayerSpinner;
    Spinner mWayToTravelSpinner;

    Character mCharacter;
    ArrayList<Integer> mCharacterDuskLayers;
    ArrayList<Integer> mTargetDuskLayers;
    ArrayList<AccelerationMode> mWayToTravel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        mStartCoordsX = findViewById(R.id.np_start_coords_x);
        mStartCoordsY = findViewById(R.id.np_start_coords_y);
        mEndCoordsX = findViewById(R.id.np_end_coords_x);
        mEndCoordsY = findViewById(R.id.np_end_coords_y);
        mTimeSubject = findViewById(R.id.tv_subject_time);
        mTimeObject = findViewById(R.id.tv_object_time);
        mCharacterDuskLayerSpinner = findViewById(R.id.sp_dusk_layer_list);
        mTargetDuskLayerSpinner = findViewById(R.id.sp_target_dusk_layer_list);
        mWayToTravelSpinner = findViewById(R.id.sp_way_to_travel_list);

        String[] displayedValues = new String[81];
        for (int i = 0; i < displayedValues.length; i++) {
            displayedValues[i] = String.valueOf(i - 40);
        }

        mStartCoordsX.setDisplayedValues(displayedValues);
        mStartCoordsX.setMinValue(0);
        mStartCoordsX.setMaxValue(80);
        mStartCoordsX.setValue(40);
        mStartCoordsY.setDisplayedValues(displayedValues);
        mStartCoordsY.setMinValue(0);
        mStartCoordsY.setMaxValue(80);
        mStartCoordsY.setValue(40);
        mEndCoordsX.setDisplayedValues(displayedValues);
        mEndCoordsX.setMinValue(0);
        mEndCoordsX.setMaxValue(80);
        mEndCoordsX.setValue(40);
        mEndCoordsY.setDisplayedValues(displayedValues);
        mEndCoordsY.setMinValue(0);
        mEndCoordsY.setMaxValue(80);
        mEndCoordsY.setValue(40);

        mCharacter = new Character(this);

        setupCharacterDuskLayersList();
        setupTargetDuskLayersList();
        setupWayToTravelList();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupWayToTravelList() {
        mWayToTravel = new ArrayList<>();
        mWayToTravel.add(new AccelerationMode("пешком", 1));
        mWayToTravel.add(new AccelerationMode("машина", MEDIUM_ACCELERATION));
        mWayToTravel.add(new AccelerationMode("легкая поступь", MEDIUM_ACCELERATION));
        mWayToTravel.add(new AccelerationMode("супер машина", HIGH_ACCELERATION));

        String characterType = mCharacter.getCharacterType();
        if (characterType.equals(getString(R.string.pref_type_value_werewolf))
                || characterType.equals(getString(R.string.pref_type_value_werewolf_mag))
                || characterType.equals(getString(R.string.pref_type_value_flipflop))) {
            if (TransformUtil.getCurrentForm(this).equals("боевая форма")) {
                mWayToTravel.add(new AccelerationMode("оборотень с грузом", MEDIUM_ACCELERATION));
                mWayToTravel.add(new AccelerationMode("оборотень без груза", HIGH_ACCELERATION));
            }
        }

        final WayToTravelAdapter wayToTravelAdapter = new WayToTravelAdapter(this, mWayToTravel);
        wayToTravelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWayToTravelSpinner.setAdapter(wayToTravelAdapter);
        mWayToTravelSpinner.setOnItemSelectedListener(this);
    }

    private void setupCharacterDuskLayersList() {
        mCharacterDuskLayers = new ArrayList<>();

        Cursor cursor = getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        int duskLayerLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT));
        cursor.close();

        for (int i = 0; i <= duskLayerLimit; i++) {
            mCharacterDuskLayers.add(i);
        }

        final DuskLayerAdapter characterDuskLayerAdapter = new DuskLayerAdapter(this, mCharacterDuskLayers);
        characterDuskLayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCharacterDuskLayerSpinner.setAdapter(characterDuskLayerAdapter);
        mCharacterDuskLayerSpinner.setOnItemSelectedListener(this);
    }

    private void setupTargetDuskLayersList() {
        mTargetDuskLayers = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            mTargetDuskLayers.add(i);
        }

        final DuskLayerAdapter targetDuskLayerAdapter = new DuskLayerAdapter(this, mTargetDuskLayers);
        targetDuskLayerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetDuskLayerSpinner.setAdapter(targetDuskLayerAdapter);
        mTargetDuskLayerSpinner.setOnItemSelectedListener(this);
    }

    public void onClickCalculateTime(View view) {
        int mCurrentCharacterDuskLayer = (int) mCharacterDuskLayerSpinner.getSelectedItem();
        int mCurrentTargetDuskLayer = (int) mTargetDuskLayerSpinner.getSelectedItem();
        AccelerationMode mCurrentAccelerationMode = (AccelerationMode) mWayToTravelSpinner.getSelectedItem();

        int timeSubject;
        int timeObject;
        double distance;
        int module = mCurrentAccelerationMode.getModule();

        double X0 = mStartCoordsX.getValue() - 40;
        double Y0 = mStartCoordsY.getValue() - 40;

        double X1 = mEndCoordsX.getValue() - 40;
        double Y1 = mEndCoordsY.getValue() - 40;

        distance = DISTANCE_CITY + Math.sqrt(Math.pow(X1-X0, 2d) + Math.pow(Y1-Y0, 2d));

        String warningMessage = "";

        while(true) {
            String selection = NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(mCurrentCharacterDuskLayer)};
            Cursor cursor = getContentResolver().query(NotATryContract.DuskLayersSummaryEntry.CONTENT_URI,
                    null, selection, selectionArgs, null);
            int duskTimeLimit = 999;
            if (cursor.moveToFirst()) {
                duskTimeLimit = cursor.getInt(cursor.getColumnIndex(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS));
            }
            cursor.close();

            timeSubject = (int) Math.ceil(distance / (BASIC_SPEED * module));
            timeObject = (int) Math.ceil(distance * Math.pow(4, mCurrentTargetDuskLayer - mCurrentCharacterDuskLayer) / (BASIC_SPEED * module));

            if (mCurrentCharacterDuskLayer > 0) {
                if (timeSubject < duskTimeLimit) {
                    break;
                } else {
                    mCurrentCharacterDuskLayer--;
                    mCharacterDuskLayerSpinner.setSelection(mCurrentCharacterDuskLayer);
                    warningMessage = "Вы не можете столько времени находиться на выбранном слое сумрака, для перемещения выбран слой: " + mCurrentCharacterDuskLayer;
                }
            } else {
                break;
            }
        }

        if (warningMessage.length()>0) {
            Toast.makeText(this, warningMessage, Toast.LENGTH_LONG).show();
        }

        int ceilDistance = (int) Math.ceil(distance);

        String stringTimeSubject = "расстояние: " + ceilDistance + "км\n" + "слой сумрака: " + mCurrentCharacterDuskLayer + "\n" + timeSubject + " ходов (минут)";
        String stringTimeObject = "слой сумрака: " + mCurrentTargetDuskLayer + "\n" + timeObject + " ходов (минут)";

        mTimeSubject.setText(stringTimeSubject);
        mTimeObject.setText(stringTimeObject);
    }

    public class WayToTravelAdapter extends ArrayAdapter<AccelerationMode> {

        private Context mContext;
        private List<AccelerationMode> wayToTravelList;

        public WayToTravelAdapter(@NonNull Context context, ArrayList<AccelerationMode> list) {
            super(context, 0, list);
            mContext = context;
            wayToTravelList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;

            if (null == listItem) {
                listItem = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            AccelerationMode currentMode = wayToTravelList.get(position);

            CheckedTextView checkedTextView = listItem.findViewById(android.R.id.text1);
            checkedTextView.setText(currentMode.toString());

            return listItem;
        }
    }

    public class DuskLayerAdapter extends ArrayAdapter<Integer> {

        private Context mContext;
        private List<Integer> duskLayerList;

        public DuskLayerAdapter(@NonNull Context context, ArrayList<Integer> list) {
            super(context, 0, list);
            mContext = context;
            duskLayerList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;

            if (null == listItem) {
                listItem = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            Integer currentDuskLayer = duskLayerList.get(position);

            CheckedTextView checkedTextView = listItem.findViewById(android.R.id.text1);
            checkedTextView.setText(String.valueOf(currentDuskLayer));

            return listItem;
        }
    }

    private class AccelerationMode {
        private String title;
        private int module;

        public AccelerationMode(String title, int module) {
            this.title = title;
            this.module = module;
        }

        public int getModule() {
            return module;
        }

        @Override
        public String toString() {
            return title + " (x" + module + ")";
        }
    }
}
