package in.co.dipankar.livetv.ui.splash;

import in.co.dipankar.livetv.base.BasePresenter;
import in.co.dipankar.livetv.data.Channel;
import in.co.dipankar.livetv.data.ChannelManager;
import in.co.dipankar.quickandorid.utils.DLog;
import java.util.List;

public class SplashViewPresenter extends BasePresenter {

  private ChannelManager mChannelManager;

  public interface ViewContract {
    void onFetchSuccess();

    void onFetchFailed();
  }

  public SplashViewPresenter() {
    mChannelManager = ChannelManager.Get();
  }

  public void fetchData() {
    mChannelManager.fetchData(
        new ChannelManager.Callback() {
          @Override
          public void onSuccess(List<Channel> radio) {
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
