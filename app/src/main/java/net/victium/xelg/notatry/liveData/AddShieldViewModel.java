package net.victium.xelg.notatry.liveData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import net.victium.xelg.notatry.database.AppDatabase;

public class AddShieldViewModel extends ViewModel {

    private AppDatabase mDb;

    public AddShieldViewModel(AppDatabase database) {
        mDb = database;
    }

    public LiveData<Integer> getShieldCount(boolean shieldType) {
        return mDb.shieldDao().getShieldsCount(shieldType);
    }
}
