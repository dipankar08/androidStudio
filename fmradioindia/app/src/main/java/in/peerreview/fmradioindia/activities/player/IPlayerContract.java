package in.peerreview.fmradioindia.activities.player;

import in.peerreview.fmradioindia.common.models.Node;
import java.util.List;

public interface IPlayerContract {
  interface View {
    void showPlayUI(Node node);

    void showPauseUI(Node node);

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
    void loadAlbum(List<Node> name, int start);

    void playNext();

    void playPrevious();

    void play();

    void pause();

    void restart();

    void resume();

    void download();

    void setSeekLocation(int progress);

    void suffle();

    void setRepeat(boolean isRepeat);

    boolean isPlaying();

    void playOrResume();

    void setMuteState(boolean b);

    void setLikeState(boolean b);
  }
}
