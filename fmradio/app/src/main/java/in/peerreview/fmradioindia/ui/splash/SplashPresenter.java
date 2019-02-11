package in.peerreview.fmradioindia.ui.splash;

import javax.inject.Inject;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.co.dipankar.quickandorid.arch.Error;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;

public class SplashPresenter extends BasePresenter {

    @Inject MusicManager mMusicManager;
    @Inject ChannelManager mChannelManager;

  public SplashPresenter(String name) {
    super(name);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {
            Error error = new Error.Builder().setTitle("Not able to load").setDesc(err).build();
            render(new SplashState.Builder().setError(error).build());
          }

          @Override
          public void onLoadSuccess() {
            render(new SplashState.Builder().setError(null).build());
          }

          @Override
          public void onDataRefreshed() {
            render(new SplashState.Builder().setError(null).build());
          }
        });
  }

  public void startFetch() {
    mChannelManager.fetch();
  }
}
