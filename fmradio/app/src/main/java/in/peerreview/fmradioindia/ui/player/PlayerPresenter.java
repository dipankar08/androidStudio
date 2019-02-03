package in.peerreview.fmradioindia.ui.player;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.model.Channel;
import javax.annotation.Nullable;

public class PlayerPresenter extends BasePresenter {
  private MusicManager mMusicManager;
  private ChannelManager mChannelManager;
  @Nullable private Channel mChannel;

  public PlayerPresenter(String name) {
    super(name);
    mMusicManager = MusicManager.Get();
    mChannelManager = ChannelManager.Get();
    mMusicManager.addCallback(
        new MusicManager.Callback() {
          @Override
          public void onTryPlaying(Channel channel) {
            mChannel = channel;
            render(
                new PlayerState.Builder()
                    .setChannel(channel)
                    .setState(PlayerState.State.TRY_PLAYING)
                    .build());
          }

          @Override
          public void onSuccess(Channel channelForId) {
            render(new PlayerState.Builder().setState(PlayerState.State.RESUME).build());
          }

          @Override
          public void onResume(Channel channelForId) {
            render(new PlayerState.Builder().setState(PlayerState.State.RESUME).build());
          }

          @Override
          public void onError(Channel channelForId, String msg) {
            render(new PlayerState.Builder().setState(PlayerState.State.ERROR).build());
          }

          @Override
          public void onPause(Channel channelForId) {
            render(new PlayerState.Builder().setState(PlayerState.State.PAUSE).build());
          }
        });
    mChannelManager.addCallback(
        new ChannelManager.Callback() {
          @Override
          public void onLoadError(String err) {}

          @Override
          public void onLoadSuccess() {}

          @Override
          public void onDataRefreshed() {}
        });
  }

  void onClickPlayPause() {
    mMusicManager.playPause();
  }

  void onClickNext() {
    mMusicManager.playNext();
  }

  void onClickPrevious() {
    mMusicManager.playPrev();
  }

  public void toggleFev() {
    if (mChannel != null) {
      mChannelManager.toggleFev(mChannel.getId());
    }
  }
}
