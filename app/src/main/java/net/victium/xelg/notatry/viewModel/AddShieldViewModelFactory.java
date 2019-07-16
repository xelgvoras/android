package net.victium.xelg.notatry.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.victium.xelg.notatry.database.AppDatabase;

public class AddShieldViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mShieldId;

    public AddShieldViewModelFactory(AppDatabase database, int shieldId) {
        mDb = database;
        mShieldId = shieldId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddShieldViewModel(mDb, mShieldId);
    }
}
