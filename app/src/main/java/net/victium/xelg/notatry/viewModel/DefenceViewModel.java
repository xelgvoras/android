package net.victium.xelg.notatry.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.victium.xelg.notatry.database.AppDatabase;

public class DefenceViewModel extends AndroidViewModel {

    private static final String TAG = DefenceViewModel.class.getSimpleName();

    private LiveData<Integer> magicDefence;
    private LiveData<Integer> physicDefence;
    private LiveData<Integer> mentalDefence;

    public DefenceViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Retrieving the defence from the DataBase");
        magicDefence = database.shieldDao().getMagicDefence();
        physicDefence = database.shieldDao().getPhysicDefence();
        mentalDefence = database.shieldDao().getMentalDefence();
    }

    public LiveData<Integer> getMagicDefence() {
        return magicDefence;
    }

    public LiveData<Integer> getPhysicDefence() {
        return physicDefence;
    }

    public LiveData<Integer> getMentalDefence() {
        return mentalDefence;
    }
}
