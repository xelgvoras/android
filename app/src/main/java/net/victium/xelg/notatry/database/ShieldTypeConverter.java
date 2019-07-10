package net.victium.xelg.notatry.database;

import androidx.room.TypeConverter;

import net.victium.xelg.notatry.enums.ShieldTypes;

import java.util.UnknownFormatConversionException;

public class ShieldTypeConverter {

    @TypeConverter
    public static ShieldTypes toShieldTypes(String typeString) {
        switch (typeString) {
            case "унив":
                return ShieldTypes.UNIVERSAL;
            case "маг":
                return ShieldTypes.MAGIC;
            case "физ":
                return ShieldTypes.PHYSIC;
            case "мент":
                return ShieldTypes.MENTAL;
            case "особый":
                return ShieldTypes.SPECIAL;
            default: {
                throw new UnknownFormatConversionException("Unknown type of shield: " + typeString);
            }
        }
    }

    @TypeConverter
    public static String toTypeString(ShieldTypes shieldType) {
        return shieldType == null ? null : shieldType.toString();
    }
}
