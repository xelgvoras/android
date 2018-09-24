package net.victium.xelg.notatry.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;

public class Character {

    private String characterName;
    private String characterType;
    private int characterAge;
    private int characterLevel;
    private int characterPowerLimit;
    private int characterDuskLayerLimit;
    private int characterPersonalShieldsLimit;
    private int characterAmuletsLimit;
    private int characterReactionsNumber;
    private int characterNaturalDefence;
    private boolean characterSide;
    private Context mContext;

    private ArrayList<String> listOfSpecialTypes = new ArrayList<>();

    public Character(Context context) {
        mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);

        String nameKey = mContext.getString(R.string.pref_full_name_key);
        String nameDefault = mContext.getString(R.string.pref_full_name_default);
        characterName = sharedPreferences.getString(nameKey, nameDefault);

        String typeKey = mContext.getString(R.string.pref_type_key);
        String typeDefault = mContext.getString(R.string.pref_type_value_mag);
        characterType = sharedPreferences.getString(typeKey, typeDefault);

        String ageKey = mContext.getString(R.string.pref_age_key);
        String ageDefault = mContext.getString(R.string.pref_age_default);
        characterAge = Integer.parseInt(sharedPreferences.getString(ageKey, ageDefault));

        String levelKey = mContext.getString(R.string.pref_level_key);
        String levelDefault = mContext.getString(R.string.pref_level_value_seven);
        characterLevel = Integer.parseInt(sharedPreferences.getString(levelKey, levelDefault));

        String powerKey = mContext.getString(R.string.pref_power_key);
        String powerDefault = mContext.getString(R.string.pref_power_default);
        characterPowerLimit = Integer.parseInt(sharedPreferences.getString(powerKey, powerDefault));

        String sideKey = mContext.getString(R.string.pref_side_key);
        String sideDefault = mContext.getString(R.string.pref_side_light_value);
        String sideString = sharedPreferences.getString(sideKey, sideDefault);
        characterSide = sideString.equals(sideDefault);

        listOfSpecialTypes.add(mContext.getString(R.string.pref_type_value_flipflop));
        listOfSpecialTypes.add(mContext.getString(R.string.pref_type_value_vampire));
        listOfSpecialTypes.add(mContext.getString(R.string.pref_type_value_werewolf));
        listOfSpecialTypes.add(mContext.getString(R.string.pref_type_value_werewolf_mag));

        setupDuskLayerLimit();
        setupPersonalShieldsLimit();
        setupAmuletsLimit();
        setupReactionsNumber();
        setupNaturalDefence();
    }

    private void setupDuskLayerLimit() {

        int depthLimit = 1;

        if (characterPowerLimit > 512) {
            depthLimit = 6;
        } else if (characterPowerLimit > 128) {
            depthLimit = 3;
        } else if (characterPowerLimit > 32) {
            depthLimit = 2;
        }

        characterDuskLayerLimit = depthLimit;
    }

    private void setupPersonalShieldsLimit() {

        int shieldsLimit;

        if (characterLevel >= 6) {
            shieldsLimit = 1;
        } else if (characterLevel >= 4) {
            shieldsLimit = 2;
        } else if (characterLevel >= 2) {
            shieldsLimit = 3;
        } else if (characterLevel == 1){
            shieldsLimit = 4;
        } else {
            shieldsLimit = 5;
        }

        characterPersonalShieldsLimit = shieldsLimit;
    }

    private void setupAmuletsLimit() {

        int amuletsLimit;

        if (characterLevel >= 3) {
            amuletsLimit = 1;
        } else if (characterLevel >= 1) {
            amuletsLimit = 2;
        } else {
            amuletsLimit = 3;
        }

        characterAmuletsLimit = amuletsLimit;
    }

    private void setupReactionsNumber() {

        int reactionsNumber = 1;

        if (listOfSpecialTypes.contains(characterType)) {

            if (characterLevel >= 5) {
                reactionsNumber = 2;
            } else if (characterLevel >= 3) {
                reactionsNumber = 3;
            } else if (characterLevel >= 1) {
                reactionsNumber = 4;
            } else {
                reactionsNumber = 5;
            }
        }

        characterReactionsNumber = reactionsNumber;
    }

    private void setupNaturalDefence() {

        int naturalDefence = 0;

        if (listOfSpecialTypes.contains(characterType)) {
            naturalDefence = characterPowerLimit;
        }

        characterNaturalDefence = naturalDefence;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(SharedPreferences sharedPreferences) {
        String nameKey = mContext.getString(R.string.pref_full_name_key);
        String nameDefault = mContext.getString(R.string.pref_full_name_default);
        this.characterName = sharedPreferences.getString(nameKey, nameDefault);
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(SharedPreferences sharedPreferences) {
        String typeKey = mContext.getString(R.string.pref_type_key);
        String typeDefault = mContext.getString(R.string.pref_type_value_mag);
        this.characterType = sharedPreferences.getString(typeKey, typeDefault);
        setupReactionsNumber();
        setupNaturalDefence();
    }

    public int getCharacterAge() {
        return characterAge;
    }

    public void setCharacterAge(SharedPreferences sharedPreferences) {
        String ageKey = mContext.getString(R.string.pref_age_key);
        String ageDefault = mContext.getString(R.string.pref_age_default);
        this.characterAge = Integer.parseInt(sharedPreferences.getString(ageKey, ageDefault));
    }

    public int getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(SharedPreferences sharedPreferences) {
        String levelKey = mContext.getString(R.string.pref_level_key);
        String levelDefault = mContext.getString(R.string.pref_level_value_seven);
        this.characterLevel = Integer.parseInt(sharedPreferences.getString(levelKey, levelDefault));
        setupPersonalShieldsLimit();
        setupAmuletsLimit();
        setupReactionsNumber();
    }

    public int getCharacterPowerLimit() {
        return characterPowerLimit;
    }

    public void setCharacterPowerLimit(SharedPreferences sharedPreferences) {
        String powerKey = mContext.getString(R.string.pref_power_key);
        String powerDefault = mContext.getString(R.string.pref_power_default);
        this.characterPowerLimit = Integer.parseInt(sharedPreferences.getString(powerKey, powerDefault));
        setupDuskLayerLimit();
    }

    public int getCharacterDuskLayerLimit() {
        return characterDuskLayerLimit;
    }

    public void setCharacterDuskLayerLimit(int characterDuskLayerLimit) {
        this.characterDuskLayerLimit = characterDuskLayerLimit;
    }

    public int getCharacterPersonalShieldsLimit() {
        return characterPersonalShieldsLimit;
    }

    public void setCharacterPersonalShieldsLimit(int characterPersonalShieldsLimit) {
        this.characterPersonalShieldsLimit = characterPersonalShieldsLimit;
    }

    public int getCharacterAmuletsLimit() {
        return characterAmuletsLimit;
    }

    public void setCharacterAmuletsLimit(int characterAmuletsLimit) {
        this.characterAmuletsLimit = characterAmuletsLimit;
    }

    public int getCharacterReactionsNumber() {
        return characterReactionsNumber;
    }

    public void setCharacterReactionsNumber(int characterReactionsNumber) {
        this.characterReactionsNumber = characterReactionsNumber;
    }

    public int getCharacterNaturalDefence() {
        return characterNaturalDefence;
    }

    public void setCharacterNaturalDefence(int characterNaturalDefence) {
        this.characterNaturalDefence = characterNaturalDefence;
    }

    public boolean isCharacterSide() {
        return characterSide;
    }

    public void setCharacterSide(SharedPreferences sharedPreferences) {
        String sideKey = mContext.getString(R.string.pref_side_key);
        String sideDefault = mContext.getString(R.string.pref_side_light_value);
        String sideString = sharedPreferences.getString(sideKey, sideDefault);
        this.characterSide = sideString.equals(sideDefault);
    }
}