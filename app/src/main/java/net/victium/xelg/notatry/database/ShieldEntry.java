package net.victium.xelg.notatry.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "shields",
indices = {@Index(value = {"name"}, unique = true)})
public class ShieldEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String type;
    @ColumnInfo(name = "min_cost")
    private int minCost;
    @ColumnInfo(name = "max_cost")
    private int maxCost;
    private int power;
    @ColumnInfo(name = "magic_defence_multiplier")
    private int magicDefenceMultiplier;
    @ColumnInfo(name = "physic_defence_multiplier")
    private int physicDefenceMultiplier;
    @ColumnInfo(name = "mental_defence")
    private int mentalDefence;
    private String target;
    private int range;

    @Ignore
    public ShieldEntry(String name, String type, int minCost, int maxCost, int power,
                       int magicDefenceMultiplier, int physicDefenceMultiplier, int mentalDefence,
                       String target, int range) {
        this.name = name;
        this.type = type;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.power = power;
        this.magicDefenceMultiplier = magicDefenceMultiplier;
        this.physicDefenceMultiplier = physicDefenceMultiplier;
        this.mentalDefence = mentalDefence;
        this.target = target;
        this.range = range;
    }

    public ShieldEntry(int id, String name, String type, int minCost, int maxCost, int power,
                       int magicDefenceMultiplier, int physicDefenceMultiplier, int mentalDefence,
                       String target, int range) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.power = power;
        this.magicDefenceMultiplier = magicDefenceMultiplier;
        this.physicDefenceMultiplier = physicDefenceMultiplier;
        this.mentalDefence = mentalDefence;
        this.target = target;
        this.range = range;
    }

    @NonNull
    @Override
    public String toString() {
        String s = String.format("%s (%s, %s, у.е.: %s-%s)", name, target, type, minCost, maxCost);
        return s;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public int getMentalDefence() {
        return mentalDefence;
    }

    public void setMentalDefence(int mentalDefence) {
        this.mentalDefence = mentalDefence;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
}