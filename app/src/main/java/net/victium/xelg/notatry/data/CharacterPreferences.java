package net.victium.xelg.notatry.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CharacterPreferences {

    public static String getCharacterNameAndAge(Character character) {

        //COMPLETED(7) Изменить %s лет на "возраст: %s"
        //TODO(8) Добавить обработчик возраста для выбора склонения

        return String.format("%s, возраст: %s", character.getCharacterName(), character.getCharacterAge());
    }

    public static String getPersonalInfoFromPreferences(Character character) {

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

    public static String getCharacterMagicPower(Character character, Cursor cursor) {

        cursor.moveToFirst();
        String currentPower = cursor.getString(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_CURRENT_POWER));
        // COMPLETED(2) Добавить реализацию получения значения текущего размера резерва силы из базы данных

        return String.format("Резерв силы: %s/%s",
                currentPower,
                character.getCharacterPowerLimit());
    }

    public static String getCharacterDefence(Cursor cursor) {
        // TODO(3) На текущий момент - это заглушка, требуется реализация получения значений из базы данных

        cursor.moveToFirst();
        String magicDefence = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM));
        if (null == magicDefence) magicDefence = "0";
        String physicDefence = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM));
        if (null == physicDefence) physicDefence = "0";
        String mentalShields = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM));
        if (null == mentalShields) mentalShields = "0";

        cursor.close();

        return String.format("Щиты \n" +
                        "\t\t\t Магическая защита: %s \n" +
                        "\t\t\t Физическая защита: %s \n" +
                        "\t\t\t Ментальная защита: %s",
                magicDefence, physicDefence, mentalShields);
    }

    public static String getCharacterDetails(Cursor characterStatusCursor, Cursor duskLayersCursor) {
        characterStatusCursor.moveToFirst();
        duskLayersCursor.moveToFirst();
        // COMPLETED(4) Реализовать получение значений из базы данных
        int duskLimitCol = characterStatusCursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_DEPTH_LIMIT);
        String duskLimit = characterStatusCursor.getString(duskLimitCol);
        int shieldsLimitCol = characterStatusCursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_SHIELDS_LIMIT);
        String shieldsLimit = characterStatusCursor.getString(shieldsLimitCol);
        int amuletsLimitCol = characterStatusCursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_AMULETS_LIMIT);
        String amuletsLimit = characterStatusCursor.getString(amuletsLimitCol);
        int reactionsNumberCol = characterStatusCursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER);
        String reactionsNumber = characterStatusCursor.getString(reactionsNumberCol);

        StringBuilder builder = new StringBuilder();
        int duskLayerCol = duskLayersCursor.getColumnIndex(NotATryContract.DuskLayersSummaryEntry.COLUMN_LAYER);
        int duskRoundCol = duskLayersCursor.getColumnIndex(NotATryContract.DuskLayersSummaryEntry.COLUMN_ROUNDS);
        boolean hasEntry = true;

        while (hasEntry) {
            String layer = duskLayersCursor.getString(duskLayerCol);
            String rounds = duskLayersCursor.getString(duskRoundCol);

            if (null == rounds || rounds.isEmpty()) {
                rounds = "недоступно";
            } else if (rounds.equals("999")) {
                rounds = "бесконечно";
            }

            builder.append("\t\t\t Ходов на ").append(layer).append(" слое: ").append(rounds).append("\n");

            hasEntry = duskLayersCursor.moveToNext();
        }

        String duskSummary = builder.toString();

        return String.format("Максимальный слой сумрака: %s \n" +
                        duskSummary +
                        "Максимальное количество персональных щитов: %s \n" +
                        "Максимальное количество авто-амулетов: %s \n" +
                        "Количество реакций за бой: %s",
                duskLimit, shieldsLimit, amuletsLimit, reactionsNumber);
    }
}
