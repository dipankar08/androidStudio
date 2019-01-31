package in.peerreview.fmradioindia.ui;

import android.content.Intent;
import android.net.Uri;
import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.TelemetryManager;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;

public class MainPresenter extends BasePresenter {

  private TelemetryManager mTelemetryManager;
  private MusicManager mMusicManager;
  private ChannelManager mChannelManager;
  private List<Channel> mFullChannelList;

  private final ChannelManager.Callback mChannelManagerCallback =
      new ChannelManager.Callback() {
        @Override
        public void onLoadError(String err) {
          render(new MainState.Builder().setErrorMsg(err).build());
        }

        @Override
        public void onLoadSuccess(List<Channel> channels) {
          mFullChannelList = channels;
          mMusicManager.setPlayList(mFullChannelList);
          render(
              new MainState.Builder()
                  .setChannel(mChannelManager.getAll())
                  .setSuggestionList(mChannelManager.getRecent())
                  .setCategoriesList(mChannelManager.getCatMap())
                  .setCurPage(MainState.Page.HOME)
                  .build());
        }
      };

  private final MusicManager.Callback mMusicManagerCallback =
      new MusicManager.Callback() {
        @Override
        public void onTryPlaying(Channel channel) {
          render(new MainState.Builder().setIsShowLoading(true).setCurChannel(channel).build());
          showErrorMsg("Hold on! Try playing " + channel.getName());
        }

        @Override
        public void onSuccess(Channel channel) {
          render(new MainState.Builder().setIsPlaying(true).setIsShowLoading(false).build());
          showErrorMsg("Now Playing " + channel.getName());
        }

        @Override
        public void onResume(Channel channel) {
          render(new MainState.Builder().setIsPlaying(true).build());
          showErrorMsg("Now playing " + channel.getName());
        }

        @Override
        public void onError(Channel channel, String msg) {
          render(new MainState.Builder().setIsPlaying(false).setIsShowLoading(false).build());
          showErrorMsg(channel.getName() + " is offline.");
        }

        @Override
        public void onPause(Channel channel) {
          render(new MainState.Builder().setIsPlaying(false).build());
          showErrorMsg("" + channel.getName() + " Paused");
        }
      };

  public MainPresenter() {
    super("MainPresenter");
    mChannelManager = ChannelManager.Get();
    mMusicManager = MusicManager.Get();
    mTelemetryManager = TelemetryManager.Get();
    mChannelManager.addCallback(mChannelManagerCallback);
    mMusicManager.addCallback(mMusicManagerCallback);
    render(new MainState.Builder().setCurPage(MainState.Page.SPASH).build());
    mChannelManager.fetch();
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
  }

  public void onNextClicked() {
    mMusicManager.playNext();
  }

  public void onPrevClicked() {
    mMusicManager.playPrev();
  }

  public void onStopPlay() {
    mMusicManager.stopPlay();
  }

  public void onItemClick(int position) {
    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_MAIN_LIST_ITEM);
      startPlayInternal(position);
  }

  private void startPlayInternal(int position) {
    mMusicManager.play(position);
    render(new MainState.Builder().setCurChannel(mFullChannelList.get(position)).build());
  }

  public void onSubmitRating(float rating) {
    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_RATING_BAR);
    final String appPackageName =
        getContext().getPackageName(); // getPackageName() from Context or Activity object
    try {
      getContext()
          .startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (android.content.ActivityNotFoundException anfe) {
      getContext()
          .startActivity(
              new Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }
  }

  private void showErrorMsg(String msg) {
    render(new MainState.Builder().setErrorMsg(msg).build());
  }

  public void onItemClick(String id) {
     Channel ch =  mChannelManager.getChannelForId(id);
     mMusicManager.playById(id);
  }
}
