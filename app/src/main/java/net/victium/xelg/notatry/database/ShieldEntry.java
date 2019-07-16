package net.victium.xelg.notatry.database;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import net.victium.xelg.notatry.MainActivity;
import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.enums.ShieldTypes;

@Entity(tableName = "shields",
indices = {@Index(value = {"name"}, unique = true)})
public class ShieldEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private ShieldTypes type = ShieldTypes.UNIVERSAL;
    private int minCost = 1;
    private int maxCost;
    private int power;
    @ColumnInfo(name = "magic_defence_multiplier")
    private int magicDefenceMultiplier = 0;
    @ColumnInfo(name = "physic_defence_multiplier")
    private int physicDefenceMultiplier = 0;
    @ColumnInfo(name = "mental_defence")
    private boolean hasMentalDefence = false;
    @ColumnInfo(name = "personal_shield")
    private boolean personalShield = true;
    @ColumnInfo(name = "can-be-destroyed")
    private boolean canBeDestroyed = true;
    private int range = 1;

    @Ignore
    public ShieldEntry(String name, Context context) {
        this.name = name;
        ShieldEntryBuilder.setupShield(context, this);
    }

    public ShieldEntry(int id, String name, ShieldTypes type, int power, int minCost, int maxCost,
                       int magicDefenceMultiplier, int physicDefenceMultiplier,
                       boolean hasMentalDefence, boolean personalShield, boolean canBeDestroyed, int range) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.power = power;
        this.magicDefenceMultiplier = magicDefenceMultiplier;
        this.physicDefenceMultiplier = physicDefenceMultiplier;
        this.hasMentalDefence = hasMentalDefence;
        this.personalShield = personalShield;
        this.canBeDestroyed = canBeDestroyed;
        this.range = range;
    }

    @NonNull
    @Override
    public String toString() {

        String target = personalShield ? "перс." : "груп.";

        return String.format("%s (%s - %s, у.е.: %s-%s)", name, target, type, minCost, maxCost);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShieldTypes getType() {
        return type;
    }

    public void setType(ShieldTypes type) {
        this.type = type;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMagicDefenceMultiplier() {
        return magicDefenceMultiplier;
    }

    public void setMagicDefenceMultiplier(int magicDefenceMultiplier) {
        this.magicDefenceMultiplier = magicDefenceMultiplier;
    }

    public int getPhysicDefenceMultiplier() {
        return physicDefenceMultiplier;
    }

    public void setPhysicDefenceMultiplier(int physicDefenceMultiplier) {
        this.physicDefenceMultiplier = physicDefenceMultiplier;
    }

    public boolean isHasMentalDefence() {
        return hasMentalDefence;
    }

    public void setHasMentalDefence(boolean hasMentalDefence) {
        this.hasMentalDefence = hasMentalDefence;
    }

    public boolean isPersonalShield() {
        return personalShield;
    }

    public void setPersonalShield(boolean personalShield) {
        this.personalShield = personalShield;
    }

    public boolean isCanBeDestroyed() {
        return canBeDestroyed;
    }

    public void setCanBeDestroyed(boolean canBeDestroyed) {
        this.canBeDestroyed = canBeDestroyed;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getMagicDefence() {
        return magicDefenceMultiplier * power;
    }

    public int getPhysicDefence() {
        return physicDefenceMultiplier * power;
    }

    public int getMentalDefence() {
        return hasMentalDefence ? 1 : 0;
    }
}
