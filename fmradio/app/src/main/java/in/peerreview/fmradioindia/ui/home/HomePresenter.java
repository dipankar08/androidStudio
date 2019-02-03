package in.peerreview.fmradioindia.ui.home;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;

public class HomePresenter extends BasePresenter {
  private MusicManager mMusicManager;
  private ChannelManager mChannelManager;

  public HomePresenter() {
    super("MainPresenter");
    mMusicManager = MusicManager.Get();
    mChannelManager = ChannelManager.Get();
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(new HomeState.Builder().setCategoriesList(mChannelManager.getCatMap()).build());
          }

          @Override
          public void onDataRefreshed() {
            render(new HomeState.Builder().setCategoriesList(mChannelManager.getCatMap()).build());
          }
        });
  }

  @Override
  protected void onViewAttached() {
    super.onViewAttached();
  }

  @Override
  protected void onViewDettached() {
    super.onViewDettached();
  }

  public void onItemClick(String id) {
    mMusicManager.playById(id);
  }
}
