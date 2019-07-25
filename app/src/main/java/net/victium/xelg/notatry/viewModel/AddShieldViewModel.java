package net.victium.xelg.notatry.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;

public class AddShieldViewModel extends AndroidViewModel {

    private LiveData<ShieldEntry> shield;
    private AppDatabase database;

    public AddShieldViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
    }

    public LiveData<ShieldEntry> getShield(int id) {
        return database.shieldDao().loadShieldById(id);
    }
}
