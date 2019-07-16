package net.victium.xelg.notatry.database;

import android.content.Context;

import net.victium.xelg.notatry.MainActivity;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.enums.ShieldTypes;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ShieldEntryBuilder {

    public static void setupShield(Context context, ShieldEntry shield) {

        int maxCost = PreferenceUtilities.getMagicPowerLimit(context) * 2;
        shield.setMaxCost(maxCost);
        String name = shield.getName();

        switch (name) {
            case "Щит мага":
                shield.setMagicDefenceMultiplier(1);
                shield.setPhysicDefenceMultiplier(1);
                shield.setHasMentalDefence(true);
                break;
            case "Чистый разум":
                shield.setType(ShieldTypes.MENTAL);
                shield.setMinCost(2);
                shield.setMaxCost(2);
                shield.setHasMentalDefence(true);
                shield.setCanBeDestroyed(false);
                shield.setRange(0);
                break;
            case "Барьер воли":
                shield.setType(ShieldTypes.MENTAL);
                shield.setMinCost(2);
                shield.setMaxCost(2);
                shield.setHasMentalDefence(true);
                shield.setCanBeDestroyed(false);
                shield.setRange(0);
                break;
            case "Сфера спокойствия":
                shield.setType(ShieldTypes.MENTAL);
                shield.setMinCost(2);
                shield.setMaxCost(2);
                shield.setHasMentalDefence(true);
                shield.setCanBeDestroyed(false);
                shield.setRange(0);
                break;
            case "Ледяная кора":
                shield.setType(ShieldTypes.MENTAL);
                shield.setMinCost(2);
                shield.setMaxCost(2);
                shield.setHasMentalDefence(true);
                shield.setCanBeDestroyed(false);
                shield.setRange(0);
                break;
            case "Силовой барьер":
                shield.setMinCost(4);
                shield.setMagicDefenceMultiplier(1);
                shield.setPhysicDefenceMultiplier(1);
                shield.setRange(2);
                break;
            case "Вогнутый щит":
                shield.setType(ShieldTypes.PHYSIC);
                shield.setMinCost(4);
                shield.setPhysicDefenceMultiplier(2);
                shield.setPersonalShield(false);
                shield.setRange(3);
                break;
            case "Сфера отрицания":
                shield.setType(ShieldTypes.MAGIC);
                shield.setMinCost(4);
                shield.setMagicDefenceMultiplier(2);
                shield.setPersonalShield(false);
                shield.setRange(3);
                break;
            case "Спаренный щит":
                shield.setMinCost(8);
                shield.setMagicDefenceMultiplier(1);
                shield.setPhysicDefenceMultiplier(1);
                shield.setHasMentalDefence(true);
                shield.setPersonalShield(false);
                shield.setRange(5);
                break;
            case "Плащ тьмы":
                shield.setMinCost(16);
                shield.setPersonalShield(false);
                shield.setCanBeDestroyed(false);
                shield.setRange(5);
                break;
            case "Радужная сфера":
                shield.setMinCost(64);
                shield.setMagicDefenceMultiplier(2);
                shield.setPhysicDefenceMultiplier(2);
                shield.setHasMentalDefence(true);
                shield.setRange(4);
                break;
            case "Высший щит мага":
                shield.setMinCost(16);
                shield.setMagicDefenceMultiplier(2);
                shield.setPhysicDefenceMultiplier(2);
                shield.setHasMentalDefence(true);
                shield.setRange(1);
                break;
            case "Большая радужная сфера":
                shield.setMinCost(64);
                shield.setMagicDefenceMultiplier(2);
                shield.setPhysicDefenceMultiplier(2);
                shield.setHasMentalDefence(true);
                shield.setPersonalShield(false);
                shield.setRange(5);
                break;
            case "Защитный купол":
                shield.setMinCost(256);
                shield.setMagicDefenceMultiplier(2);
                shield.setPhysicDefenceMultiplier(2);
                shield.setHasMentalDefence(true);
                shield.setPersonalShield(false);
                shield.setRange(6);
                break;
            case "Хрустальный щит":
                shield.setType(ShieldTypes.PHYSIC);
                shield.setMinCost(512);
                shield.setPhysicDefenceMultiplier(100);
                shield.setPersonalShield(false);
                shield.setRange(6);
                break;
            default:
                throw new IllegalArgumentException("Unknown shield: " + name);
        }
    }

    public static List<ShieldEntry> getShieldList(Context context) {

        String[] shieldArray = context.getResources().getStringArray(R.array.shields_array);
        List<String> immutableShieldList = Arrays.asList(shieldArray);
        ArrayList<String> shieldStringList = new ArrayList<>(immutableShieldList);
        ArrayList<ShieldEntry> shieldList = new ArrayList<>();

        for (String shieldName : shieldStringList) {
            shieldList.add(new ShieldEntry(shieldName, context));
        }

        return shieldList;
    }
}
