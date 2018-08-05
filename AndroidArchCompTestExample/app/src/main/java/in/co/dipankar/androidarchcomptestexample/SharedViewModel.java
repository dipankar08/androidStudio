package in.co.dipankar.androidarchcomptestexample;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

public class SharedViewModel extends AndroidViewModel {

    private final MutableLiveData<User> selected = new MutableLiveData<User>();

    public SharedViewModel(Application application) {
        super(application);
    }

    public void select(User item) {
        selected.setValue(item);
    }

    public LiveData<User> getSelected() {
        return selected;
    }
    @Override
    public void onCleared(){
        Log.d("DIPANKAR","SharedViewModel::onCleared called ");
    }
}
