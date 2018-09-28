package in.co.dipankar.fmradio.ui.viewpresenter.splash;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.quickandorid.utils.DLog;
import java.util.List;

public class SplashViewPresenter extends BasePresenter {

  public interface ViewContract {
    void onFetchSuccess();

    void onFetchFailed();
  }

  RadioManager mRadioManager;

  public SplashViewPresenter() {
    mRadioManager = FmRadioApplication.Get().getRadioManager();
  }

  public void fetchData() {
    mRadioManager.fetchData(
        new RadioManager.RadioManagerCallback() {
          @Override
          public void onSuccess(List<Radio> radio) {
            DLog.d("SplashViewPresenter:: Fetch Success");
            if (getView() != null) {
              ((SplashView) getView()).onFetchSuccess();
            }
          }

          @Override
          public void onFail(String msg) {
            DLog.d("SplashViewPresenter:: Fetch Failed");
            if (getView() != null) {
              ((SplashView) getView()).onFetchFailed();
            }
          }
        });
  }
}
