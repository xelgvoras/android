package net.victium.xelg.notatry.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;

import java.util.List;

public class ShieldViewModel extends AndroidViewModel {

    private static final String TAG = ShieldViewModel.class.getSimpleName();

    private LiveData<List<ShieldEntry>> mShieldEntries;

    public ShieldViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Retrieving the shields from the Database");
        mShieldEntries = database.shieldDao().loadAllShields();
    }

    public LiveData<List<ShieldEntry>> getShields() {
        return mShieldEntries;
    }
}
