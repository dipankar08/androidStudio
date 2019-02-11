package in.peerreview.fmradioindia.ui.mainactivity;

import javax.inject.Inject;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;

public class MainPresenter extends BasePresenter {

  @Inject MusicManager mMusicManager;
  @Inject ChannelManager mChannelManager;


  public MainPresenter() {
    super("MainPresenter");
  }

  @Override
  protected void onViewAttached() {
    super.onViewAttached();
    mMusicManager.startService(getContext());
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
