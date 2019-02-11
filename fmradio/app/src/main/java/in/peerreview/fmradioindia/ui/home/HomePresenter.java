package in.peerreview.fmradioindia.ui.home;

import javax.inject.Inject;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;

public class HomePresenter extends BasePresenter {
    @Inject MusicManager mMusicManager;
    @Inject ChannelManager mChannelManager;

  public HomePresenter() {
    super("MainPresenter");
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
