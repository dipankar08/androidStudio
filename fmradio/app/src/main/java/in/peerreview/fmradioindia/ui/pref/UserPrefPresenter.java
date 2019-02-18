package in.peerreview.fmradioindia.ui.pref;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.Map;
import javax.inject.Inject;

public class UserPrefPresenter extends BasePresenter {
  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;

  @Inject
  public UserPrefPresenter() {
    super("UserPrefPresenter");
    MyApplication.getMyComponent().inject(this);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(new UserPrefState.Builder().setConfig(mChannelManager.getUserPref()).build());
          }

          @Override
          public void onDataRefreshed() {
            // render(new
            // UserPrefState.Builder().setConfig(mChannelManager.getCurrentConfig()).build());
          }
        });
  }

  public void onOptionChanged(Map<String, Boolean> opt) {
    mChannelManager.setFilterUserPref(opt);
  }
}
