package net.victium.xelg.notatry.dataBinding;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import net.victium.xelg.notatry.dataBinding.MainActivityInfo;
import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.utilities.PreferenceUtilities;

public class MainActivityInfoBuilder {

    public static MainActivityInfo createMainActivityInfo(Context context) {

        MainActivityInfo info = new MainActivityInfo();
        AppDatabase database = AppDatabase.getInstance(context);

        String name = PreferenceUtilities.getCharacterName(context);
        int age = PreferenceUtilities.getCharacterAge(context);
        info.fullName = String.format("%s, %s %s", name, age, ageMatcher(age));

        String side = sideMatcher(PreferenceUtilities.getCharacterSide(context));
        String type = PreferenceUtilities.getCharacterType(context);
        int level = PreferenceUtilities.getCharacterLevel(context);
        info.personalInfo = String.format("%s, %s, %s уровень", side, type, level);

        int currentMagicPower = PreferenceUtilities.getCurrentMagicPower(context);
        int magicPowerLimit = PreferenceUtilities.getMagicPowerLimit(context);
        info.magicPower = String.format("Резерв силы: %s/%s", currentMagicPower, magicPowerLimit);

        /*int magicDefence = PreferenceUtilities.getMagicDefence(context);
        int physicDefence = PreferenceUtilities.getPhysicDefence(context);
        int mentalDefence = PreferenceUtilities.getMentalDefence(context);
        int naturalDefence = PreferenceUtilities.getNaturalDefence(context);
        String naturalDefenceString = "";
        if (PreferenceUtilities.isCharacterVop(context)) {
            naturalDefenceString = String.format("\n\t\t\t Естественная защита: %s", naturalDefence);
        }
        int currentNaturalMentalDefence = PreferenceUtilities.getCurrentNaturalMentalDefence(context);
        int naturalMentalDefence = PreferenceUtilities.getNaturalMentalDefence(context);
        info.defence = String.format("Щиты \n" +
                "\t\t\t Магическая защита: %s \n" +
                "\t\t\t Физическая защита: %s \n" +
                "\t\t\t Ментальная защита: %s" +
                "%s \n" +
                "\t\t\t Врожденных ментальных щитов: %s(%s)",
                magicDefence, physicDefence, mentalDefence, naturalDefenceString,
                currentNaturalMentalDefence, naturalMentalDefence);*/
        setupDefence(context, info);

        int duskLimit = PreferenceUtilities.getDuskLimit(context);
        int personalShieldLimit = PreferenceUtilities.getPersonalShieldLimit(context);
        int amuletLimit = PreferenceUtilities.getAmuletLimit(context);
        int amuletInSeries = PreferenceUtilities.getAmuletInSeries(context);
        int reactionsNumber = PreferenceUtilities.getReactionsNumber(context);
        ArrayMap<String, Integer> duskSummary = PreferenceUtilities.getDuskSummary(context);

        StringBuilder builder = new StringBuilder();
        int limit;
        String limitString;
        for (int i = 1; i<7; i++) {
            limit = duskSummary.get("layout-"+i);
            if (limit > 99) {
                limitString = "бесконечно";
            } else {
                limitString = String.valueOf(limit);
            }
            builder.append("\t\t\t Ходов на ").append(i).append(" слое: ").append(limitString).append("\n");
        }

        info.characterDetail = String.format("Максимальный слой сумрака: %s \n" +
                builder.toString() +
                "Максимальное количество персональных щитов: %s \n" +
                "Максимальное количество авто-амулетов: %s(%s) \n" +
                "Количество реакций за бой: %s",
                duskLimit, personalShieldLimit, amuletLimit, amuletInSeries, reactionsNumber);

        return info;
    }

    public static void setupDefence(Context context, MainActivityInfo info) {
        int magicDefence = PreferenceUtilities.getMagicDefence(context);
        int physicDefence = PreferenceUtilities.getPhysicDefence(context);
        int mentalDefence = PreferenceUtilities.getMentalDefence(context);
        int naturalDefence = PreferenceUtilities.getNaturalDefence(context);
        String naturalDefenceString = "";
        if (PreferenceUtilities.isCharacterVop(context)) {
            naturalDefenceString = String.format("\n\t\t\t Естественная защита: %s", naturalDefence);
        }
        int currentNaturalMentalDefence = PreferenceUtilities.getCurrentNaturalMentalDefence(context);
        int naturalMentalDefence = PreferenceUtilities.getNaturalMentalDefence(context);
        info.defence = String.format("Щиты \n" +
                        "\t\t\t Магическая защита: %s \n" +
                        "\t\t\t Физическая защита: %s \n" +
                        "\t\t\t Ментальная защита: %s" +
                        "%s \n" +
                        "\t\t\t Врожденных ментальных щитов: %s(%s)",
                magicDefence, physicDefence, mentalDefence, naturalDefenceString,
                currentNaturalMentalDefence, naturalMentalDefence);
    }

    @NonNull
    private static String ageMatcher(int age) {

        String stringForAge;

        switch (age) {
            case 11:
                stringForAge = "лет";
                break;
            case 12:
                stringForAge = "лет";
                break;
            case 13:
                stringForAge = "лет";
                break;
            case 14:
                stringForAge = "лет";
                break;
            default:
                int ageMod = age % 10;
                switch (ageMod) {
                    case 1:
                        stringForAge = "год";
                        break;
                    case 2:
                        stringForAge = "года";
                        break;
                    case 3:
                        stringForAge = "года";
                        break;
                    case 4:
                        stringForAge = "года";
                        break;
                    default:
                        stringForAge = "лет";
                        break;
                }
                break;
        }

        return stringForAge;
    }

    @NonNull
    private static String sideMatcher(String side) {

        switch (side) {
            case "свет":
                return "Светлый иной";
            case "тьма":
                return "Темный иной";
                default:
                    throw new IllegalArgumentException("Unknown side: " + side);
        }
    }
}
