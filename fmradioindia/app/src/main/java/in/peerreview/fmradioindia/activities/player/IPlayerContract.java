package in.peerreview.fmradioindia.activities.player;

import in.peerreview.fmradioindia.common.models.MusicNode;
import java.util.List;

public interface IPlayerContract {
  interface View {
    void showPlayUI(MusicNode musicNode);

    void showPauseUI(MusicNode musicNode);

    void enableNext();

    void disableNext();

    void enablePrevious();

    void disablePrevious();

    void updateSeekBarInfo(int total, int cur);

    void showLoadUI();

    void hideLoadUI();

    void showDownloading();

    void hideDownloading();

    void markFavorite();

    void unmarkFavorite();

    void showMute();

    void hideMute();

    void markRepeat();

    void unmarkRepeat();
  }

  interface Presenter {
    void loadAlbum(List<MusicNode> name, int start);

    void playNext();

    void playPrevious();

    void play();

    void pause();

    void restart();

    void resume();

    void mute();

    void unMute();

    void download();

    void SetFavorite(boolean isFev);

    void setSeekLocation(int progress);

    void suffle();

    void setRepeat(boolean isRepeat);

    boolean isPlaying();

    void playOrResume();
  }
}
