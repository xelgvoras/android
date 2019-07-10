package net.victium.xelg.notatry.enums;

public enum ShieldTypes {
    UNIVERSAL ("унив"),
    MAGIC ("маг"),
    PHYSIC ("физ"),
    MENTAL ("мент"),
    SPECIAL ("особый");

    private final String name;

    ShieldTypes(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
