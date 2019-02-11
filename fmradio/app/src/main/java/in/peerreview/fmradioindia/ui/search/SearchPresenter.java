package in.peerreview.fmradioindia.ui.search;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;

import javax.inject.Inject;

public class SearchPresenter extends BasePresenter {
  @Inject
  ChannelManager mChannelManager;
  @Inject MusicManager mMusicManager;

  public SearchPresenter(String name) {
    super(name);
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {
            render(
                new SearchState.Builder()
                    .setRecentSearch(mChannelManager.getRecentSearch())
                    .build());
          }

          @Override
          public void onDataRefreshed() {
            render(
                new SearchState.Builder()
                    .setRecentSearch(mChannelManager.getRecentSearch())
                    .build());
          }
        });
  }

  public void onSearch(String s) {
    List<Channel> result = mChannelManager.applySearch(s);
    render(
        new SearchState.Builder()
            .setSearchChannel(result)
            .setShouldShowClose(s.length() > 0 ? true : false)
            .build());
  }

  public void onClickItem(String id) {
    mChannelManager.markRecentSearch(id);
    mMusicManager.playById(id);
  }

  public void onVisibilityChanged(int visibility) {
    render(
        new SearchState.Builder()
            .setRecentSearch(mChannelManager.getRecentSearch())
            .setSearchChannel(mChannelManager.getAll())
            .build());
  }
}
