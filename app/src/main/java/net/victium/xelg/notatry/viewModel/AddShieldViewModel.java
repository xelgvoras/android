package net.victium.xelg.notatry.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;

public class AddShieldViewModel extends ViewModel {

    private LiveData<ShieldEntry> shield;

    public AddShieldViewModel(AppDatabase database, int shieldId) {
        shield = database.shieldDao().loadShieldById(shieldId);
    }

    public LiveData<ShieldEntry> getShield() {
        return shield;
    }
}
