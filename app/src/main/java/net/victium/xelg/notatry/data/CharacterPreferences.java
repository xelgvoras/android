package net.victium.xelg.notatry.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

public class CharacterPreferences {

    public static String getCharacterNameAndAge(@NonNull Character character) {

        //COMPLETED(7) Изменить %s лет на "возраст: %s"
        //COMPLETED(8) Добавить обработчик возраста для выбора склонения
        String ageString = ageMatcher(character.getCharacterAge());

        return String.format("%s, %s %s", character.getCharacterName(), character.getCharacterAge(), ageString);
    }

    public static String getPersonalInfo(@NonNull Character character) {

        String sideString;
        if (character.isCharacterSide()) {
            sideString = "Светлый иной";
        } else {
            sideString = "Темный иной";
        }

        return String.format("%s, %s, %s уровень",
                sideString,
                character.getCharacterType(),
                character.getCharacterLevel());
    }

    public static String getCharacterMagicPower(@NonNull Character character, @NonNull Context context) {

        Cursor cursor = context.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        String currentPower = cursor.getString(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        cursor.close();
        // COMPLETED(2) Добавить реализацию получения значения текущего размера резерва силы из базы данных

        return String.format("Резерв силы: %s/%s",
                currentPower,
                character.getCharacterPowerLimit());
    }

    public static String getCharacterDefence(@NonNull Character character, @NonNull Context context) {
        // COMPLETED(3) На текущий момент - это заглушка, требуется реализация получения значений из базы данных

        String[] defenceSummary = new String[]{
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };
        Cursor cursor = context.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                defenceSummary, null, null, null);

        cursor.moveToFirst();
        String magicDefence = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM));
        if (null == magicDefence) magicDefence = "0";
        String physicDefence = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM));
        if (null == physicDefence) physicDefence = "0";
        String mentalShields = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM));
        if (null == mentalShields) mentalShields = "0";
        cursor.close();

        String naturalDefence = "";
        if (character.isCharacterVop()) {
            cursor = context.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                    null, null, null, null);
            cursor.moveToFirst();
            naturalDefence = cursor.getString(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));
            naturalDefence = String.format("\n\t\t\t Естественная защита: %s", naturalDefence);
            cursor.close();
        }

        return String.format("Щиты \n" +
                        "\t\t\t Магическая защита: %s \n" +
                        "\t\t\t Физическая защита: %s \n" +
                        "\t\t\t Ментальная защита: %s" +
                        "%s",
                magicDefence, physicDefence, mentalShields, naturalDefence);
    }

    public static String getCharacterDetails(@NonNull Character character, @NonNull Context context) {
        // COMPLETED(4) Реализовать получение значений из базы данных
        String duskLimit = String.valueOf(character.getCharacterDuskLayerLimit());
        String shieldsLimit = String.valueOf(character.getCharacterPersonalShieldsLimit());
        String amuletsLimit = String.valueOf(character.getCharacterAmuletsLimit());
        String reactionsNumber = String.valueOf(character.getCharacterReactionsNumber());

        StringBuilder builder = new StringBuilder();
        Cursor cursor = context.getContentResolver().query(NotATryContract.DuskLayersSummaryEntry.CONTENT_URI,
                null, null, null, null);
        int duskLayerCol = cursor.getColumnIndex(NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER);
        int duskRoundCol = cursor.getColumnIndex(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS);
        cursor.moveToFirst();

        boolean hasEntry = true;

        while (hasEntry) {
            String layer = cursor.getString(duskLayerCol);
            String rounds = cursor.getString(duskRoundCol);

            if (rounds.equals("999")) {
                rounds = "бесконечно";
            }

            builder.append("\t\t\t Ходов на ").append(layer).append(" слое: ").append(rounds).append("\n");

            hasEntry = cursor.moveToNext();
        }
        cursor.close();

        String duskSummary = builder.toString();

        return String.format("Максимальный слой сумрака: %s \n" +
                        duskSummary +
                        "Максимальное количество персональных щитов: %s \n" +
                        "Максимальное количество авто-амулетов: %s \n" +
                        "Количество реакций за бой: %s",
                duskLimit, shieldsLimit, amuletsLimit, reactionsNumber);
    }

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
}
