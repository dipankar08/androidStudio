package in.peerreview.fmradioindia.ui.mainactivity;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import javax.inject.Inject;

public class MainPresenter extends BasePresenter {

  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;

  public MainPresenter() {
    super("MainPresenter");
    MyApplication.getMyComponent().inject(this);
  }

  @Override
  protected void onViewAttached() {
    super.onViewAttached();
    mMusicManager.startService((MainActivity) getContext());
  }

  @Override
  protected void onViewDettached() {
    super.onViewDettached();
    mMusicManager.stopService();
    mChannelManager.saveList();
  }

  public void onStopPlay() {
    mMusicManager.stopPlay();
  }

  public void onLoadSuccess() {
    mMusicManager.setPlayList(mChannelManager.getAll());
    render(new MainState.Builder().setPage(MainState.Page.HOME).build());
  }
}
