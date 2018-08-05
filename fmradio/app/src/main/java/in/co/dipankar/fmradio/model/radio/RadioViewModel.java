package in.co.dipankar.fmradio.model.radio;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import in.co.dipankar.fmradio.entity.Radio;
import in.co.dipankar.fmradio.model.BaseViewModel;
import in.co.dipankar.fmradio.ui.base.BaseNavigationActivity;

public class RadioViewModel extends BaseViewModel {

    private MutableLiveData<Radio> mCurrentPlaying;
    private MutableLiveData<List<Radio>> allList;
    private MutableLiveData<List<Radio>> filteredList;

    private MutableLiveData<R> roundSiteList;

    public RadioViewModel(@NonNull Application application) {
        super(application);
    }
}
