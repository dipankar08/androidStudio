package in.peerreview.fmradioindia.ui.player;

import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.model.Channel;

public class PlayerPresenter extends BasePresenter {
    MusicManager mMusicManager;
    public PlayerPresenter(String name) {
        super(name);
        mMusicManager = MusicManager.Get();
        mMusicManager.addCallback(new MusicManager.Callback() {
            @Override
            public void onTryPlaying(Channel channel) {
                render(new PlayerState.Builder().setChannel(channel).setState(PlayerState.State.TRY_PLAYING).build());
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
    }
    void onClickPlayPause(){
        mMusicManager.playPause();
    }

    void onClickNext(){
        mMusicManager.playNext();
    }

    void onClickPrevious(){
        mMusicManager.playPrev();
    }
}


