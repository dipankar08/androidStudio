package in.co.dipankar.androidarchcomptestexample.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private LiveData<List<UserEntity>> items;

    private UserDatabase db;

    public UserViewModel(@NonNull Application application) {
        super(application);
        db = UserDatabase.getAppDatabase(this.getApplication());
       // items = db.movieDao().getAllItems();
    }
    public LiveData<List<UserEntity>> getAllItems() {
        return items;
    }
}
