package net.victium.xelg.notatry.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShieldDao {

    @Insert
    void insertShield(ShieldEntry shieldEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateShield(ShieldEntry shieldEntry);

    @Delete
    void deleteShield(ShieldEntry shieldEntry);

    @Query("SELECT * FROM shields ORDER BY range DESC")
    LiveData<List<ShieldEntry>> loadAllShields();

    @Query("SELECT COUNT(*) FROM shields WHERE personal_shield = :type")
    LiveData<Integer> getShieldsCount(boolean type);

    @Query("SELECT SUM(power * magic_defence_multiplier) FROM shields")
    LiveData<Integer> getMagicDefence();

    @Query("SELECT SUM(power * physic_defence_multiplier) FROM shields")
    LiveData<Integer> getPhysicDefence();

    @Query("SELECT COUNT(*) FROM shields WHERE mental_defence = 1")
    LiveData<Integer> getMentalDefence();
}
