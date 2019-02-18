package in.peerreview.fmradioindia.ui.splash;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.co.dipankar.quickandorid.arch.Error;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.PrefUtils;
import in.peerreview.fmradioindia.ui.MyApplication;
import javax.inject.Inject;

public class SplashPresenter extends BasePresenter {

  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;
  @Inject PrefUtils mPrefUtils;

  public SplashPresenter(String name) {
    super(name);
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {
            Error error = new Error.Builder().setTitle("Not able to load").setDesc(err).build();
            render(new SplashState.Builder().setError(error).build());
          }

          @Override
          public void onLoadSuccess() {
            render(new SplashState.Builder().setType(SplashState.Type.Done).setError(null).build());
          }

          @Override
          public void onDataRefreshed() {
            render(new SplashState.Builder().setError(null).build());
          }
        });
    if (mPrefUtils.isFtux()) {
      onCompelteFTUX();
    } else {
      render(new SplashState.Builder().setType(SplashState.Type.Ftux).build());
    }
  }

  public void startFetch() {
    mChannelManager.fetch();
  }

  public void onCompelteFTUX() {
    mPrefUtils.setFtuxDone(true);
    render(new SplashState.Builder().setType(SplashState.Type.Boot).build());
    mChannelManager.fetch();
  }
}
