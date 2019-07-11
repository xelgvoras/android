package net.victium.xelg.notatry.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "dusk",
indices = {@Index(value = {"layer"}, unique = true)})
public class DuskLayerEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int layer;
    @ColumnInfo(name = "round_limit")
    private int roundLimit;

    public DuskLayerEntry(int id, int layer, int roundLimit) {
        this.id = id;
        this.layer = layer;
        this.roundLimit = roundLimit;
    }

    public DuskLayerEntry(int layer, int roundLimit) {
        this.layer = layer;
        this.roundLimit = roundLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

    public void setRoundLimit(int roundLimit) {
        this.roundLimit = roundLimit;
    }
}
