package in.co.dipankar.fmradio.ui.viewpresenter.splash;

import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.data.DataManager;
import in.co.dipankar.fmradio.data.RadioListManager;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.entity.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.quickandorid.utils.DLog;

public class SplashViewPresenter extends BasePresenter {

    public interface ViewContract {
        void onFetchSuccess();
        void onFetchFailed();
    }

    RadioManager mRadioManager;
    public SplashViewPresenter(){
        mRadioManager = FmRadioApplication.Get().getRadioManager();
    }

    public void fetchData(){
        mRadioManager.fetchData(new RadioManager.RadioManagerCallback() {
            @Override
            public void onSuccess(List<Radio> radio) {
                DLog.d("SplashViewPresenter:: Fetch Success");
                if(getView() != null){
                    ((SplashView)getView()).onFetchSuccess();
                }
            }

            @Override
            public void onFail(String msg) {
                DLog.d("SplashViewPresenter:: Fetch Failed");
                if(getView() != null){
                    ((SplashView)getView()).onFetchFailed();
                }
            }
        });
    }
}