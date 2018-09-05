package net.victium.xelg.notatry.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import net.victium.xelg.notatry.R;

public class CharacterPreferences {

    public static String getCharacterNameAndAge(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForCharName = context.getString(R.string.pref_full_name_key);
        String defaultName = context.getString(R.string.pref_full_name_default);
        String keyForCharAge = context.getString(R.string.pref_age_key);
        String defaultAge = context.getString(R.string.pref_age_default);

        String fullName = sharedPreferences.getString(keyForCharName, defaultName);
        String age = sharedPreferences.getString(keyForCharAge, defaultAge);
        String summary = String.format("%s, %s лет", fullName, age);

        return summary;
    }

    public static String getPersonalInfoFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForCharSide = context.getString(R.string.pref_side_key);
        String defaultSide = context.getString(R.string.pref_side_light_value);
        String keyForCharType = context.getString(R.string.pref_type_key);
        String defaultType = context.getString(R.string.pref_type_value_mag);
        String keyForCharLevel = context.getString(R.string.pref_level_key);
        String defaultLevel = context.getString(R.string.pref_level_value_seven);

        String side = sharedPreferences.getString(keyForCharSide, defaultSide);
        String type = sharedPreferences.getString(keyForCharType, defaultType);
        String level = sharedPreferences.getString(keyForCharLevel, defaultLevel);

        String sideString;
        if (side.equals(defaultSide)) {
            sideString = "Светлый иной";
        } else {
            sideString = "Темный иной";
        }

        String summary = String.format("%s, %s, %s уровень", sideString, type, level);

        return summary;
    }

    public static String getCharacterMagicPower(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForCharPower = context.getString(R.string.pref_power_key);
        String powerDefault = context.getString(R.string.pref_power_default);
        String characterPowerLimit = sharedPreferences.getString(keyForCharPower, powerDefault);
        // TODO(2) Добавить реализацию получения значения текущего размера резерва силы из базы данных
        String summary = String.format("Резерв силы: 50/%s", characterPowerLimit);

        return summary;
    }

    public static String getCharacterDefence(Context context) {
        // TODO(3) На текущий момент - это заглушка, требуется реализация получения значений из базы данных
        String magicDefence = "36";
        String physicDefence = "36";
        String mentalShields = "2";

        String summary = String.format("Щиты \n" +
                "\t\t\t Магическая защита: %s \n" +
                "\t\t\t Физическая защита: %s \n" +
                "\t\t\t Ментальная защита: %s",
                magicDefence, physicDefence, mentalShields);

        return summary;
    }

    public static String getCharacterDetails(Context context) {
        // TODO(4) Реализовать получение значений из базы данных
        String maxDuskDepth = "2";
        String[] duskLayersTime = {"20", "1", "-", "-", "-", "1", "20"};
        String maxPersonalShields = "3";
        String maxAutoAmulets = "4";

        String duskSummary = "";
        int i = 1;
        for (String s : duskLayersTime) {
            duskSummary = duskSummary + "\t\t\t Время на " + i++ + " слое: " + s +"\n";
        }

        String summary = String.format("Максимальный слой сумрака: %s \n" +
                duskSummary +
                "Максимальное количество персональных щитов: %s \n" +
                "Максимальное количество авто-амулетов: %s",
                maxDuskDepth, maxPersonalShields, maxAutoAmulets);

        return summary;
    }
}
