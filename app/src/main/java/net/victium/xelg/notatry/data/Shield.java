package net.victium.xelg.notatry.data;

public class Shield {

    public String name;
    public String type;
    public int magicDefenceMultiplier;
    public int physicDefenceMultiplier;
    public boolean hasMentalDefence;
    public boolean isPersonalShield;
    public int range;

    public Shield(String name, String type, int magicDefenceMultiplier, int physicDefenceMultiplier, boolean hasMentalDefence, boolean isPersonalShield, int range) {
        this.name = name;
        this.type = type;
        this.magicDefenceMultiplier = magicDefenceMultiplier;
        this.physicDefenceMultiplier = physicDefenceMultiplier;
        this.hasMentalDefence = hasMentalDefence;
        this.isPersonalShield = isPersonalShield;
        this.range = range;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, type);
    }
}
