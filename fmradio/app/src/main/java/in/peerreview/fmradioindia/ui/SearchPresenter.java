package in.peerreview.fmradioindia.ui;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;

public class SearchPresenter extends BasePresenter {
  ChannelManager mChannelManager;

  public SearchPresenter(String name) {
    super(name);
    mChannelManager = ChannelManager.Get();
  }

  public void onBackClieked() {}

  public void onCloseClicked() {}

  public void onSearch(String s) {
    List<Channel> result = mChannelManager.applySearch(s);
    render(
        new SearchState.Builder()
            .setSearchChannel(result)
            .setShouldShowClose(s.length() > 0 ? true : false)
            .build());
  }

  public void onClickItem(String id) {}

    public void onVisibilityChanged(int visibility) {
        render(
                new SearchState.Builder()
                        .setRecentSearch(mChannelManager.getRecentSearch())
                        .setSearchChannel(mChannelManager.getAll())
                        .build());
    }
}
