package net.victium.xelg.notatry.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DuskLayerDao {

    @Insert
    void insertAllDuskLayer(List<DuskLayerEntry> list);

    @Insert
    void insertDuskLayer(DuskLayerEntry duskLayerEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAllDuskLayer(List<DuskLayerEntry> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDuskLayer(DuskLayerEntry duskLayerEntry);

    @Query("SELECT * FROM dusk ORDER BY layer")
    LiveData<List<DuskLayerEntry>> loadAllDuskLayer();
}
