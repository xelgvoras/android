package net.victium.xelg.notatry.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.victium.xelg.notatry.database.AppDatabase;
import net.victium.xelg.notatry.database.ShieldEntry;

import java.util.List;

public class DefenceViewModel extends AndroidViewModel {

    private static final String TAG = DefenceViewModel.class.getSimpleName();

    private LiveData<List<ShieldEntry>> mDefence;

    public DefenceViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Retrieving the defence from the DataBase");
        mDefence = database.shieldDao().loadAllShields();
    }

    public LiveData<List<ShieldEntry>> getDefence() {
        return mDefence;
    }
}
