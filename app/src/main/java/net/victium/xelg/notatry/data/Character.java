package net.victium.xelg.notatry.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;

public class Character {

    private String characterName;
    private String characterType;
    private int characterLevel;
    private int characterPowerLimit;
    private int characterDuskLayerLimit;
    private int characterPersonalShieldsLimit;
    private int characterAmuletsLimit;
    private int characterReactionsNumber;
    private int characterNaturalDefence;
    private boolean characterSide;

    private ArrayList<String> listOfSpecialTypes = new ArrayList<>();

    public Character(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String nameKey = context.getString(R.string.pref_full_name_key);
        String nameDefault = context.getString(R.string.pref_full_name_default);
        characterName = sharedPreferences.getString(nameKey, nameDefault);

        String typeKey = context.getString(R.string.pref_type_key);
        String typeDefault = context.getString(R.string.pref_type_value_mag);
        characterType = sharedPreferences.getString(typeKey, typeDefault);

        String levelKey = context.getString(R.string.pref_level_key);
        String levelDefault = context.getString(R.string.pref_level_value_seven);
        characterLevel = Integer.parseInt(sharedPreferences.getString(levelKey, levelDefault));

        String powerKey = context.getString(R.string.pref_power_key);
        String powerDefault = context.getString(R.string.pref_power_default);
        characterPowerLimit = Integer.parseInt(sharedPreferences.getString(powerKey, powerDefault));

        String sideKey = context.getString(R.string.pref_side_key);
        String sideDefault = context.getString(R.string.pref_side_light_value);
        String sideString = sharedPreferences.getString(sideKey, sideDefault);
        characterSide = sideString.equals(sideDefault);

        listOfSpecialTypes.add(context.getString(R.string.pref_type_value_flipflop));
        listOfSpecialTypes.add(context.getString(R.string.pref_type_value_vampire));
        listOfSpecialTypes.add(context.getString(R.string.pref_type_value_werewolf));
        listOfSpecialTypes.add(context.getString(R.string.pref_type_value_werewolf_mag));

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

        if (characterLevel >= 5) {
            shieldsLimit = 1;
        } else if (characterLevel >= 3) {
            shieldsLimit = 2;
        } else if (characterLevel >= 1) {
            shieldsLimit = 3;
        } else {
            shieldsLimit = 4;
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

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
        setupReactionsNumber();
        setupNaturalDefence();
    }

    public int getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(int characterLevel) {
        this.characterLevel = characterLevel;
        setupPersonalShieldsLimit();
        setupAmuletsLimit();
        setupReactionsNumber();
    }

    public int getCharacterPowerLimit() {
        return characterPowerLimit;
    }

    public void setCharacterPowerLimit(int characterPowerLimit) {
        this.characterPowerLimit = characterPowerLimit;
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

    public void setCharacterSide(boolean characterSide) {
        this.characterSide = characterSide;
    }
}