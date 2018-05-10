package in.peerreview.fmradioindia.activities.player;

import in.co.dipankar.quickandorid.utils.Player;
import in.peerreview.fmradioindia.common.Configuration;
import in.peerreview.fmradioindia.common.models.Node;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayerPresenter implements IPlayerContract.Presenter {
  static final String TAG = "WelcomePresenter";
  private static final String url = Configuration.DB_ENDPOINT + "?limit=300&state=Active";
  private IPlayerContract.View mView;

  private Player mPlayer;

  private List<Node> mNodeList;
  int curIndex = 0;

  private boolean mIsRepeat = false;

  public PlayerPresenter(IPlayerContract.View loginView) {
    mView = loginView;
    mPlayer =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(String id, String msg) {
                Node m = getMusicNodeForID(id);
                mView.showPauseUI(m);
              }

              @Override
              public void onSuccess(String id, String msg) {
                Node m = getMusicNodeForID(id);
                mView.showPauseUI(m);
              }

              @Override
              public void onResume(String id, String msg) {
                Node m = getMusicNodeForID(id);
                mView.showPauseUI(m);
              }

              @Override
              public void onPause(String id, String msg) {
                Node m = getMusicNodeForID(id);
                mView.showPlayUI(m);
              }

              @Override
              public void onMusicInfo(String id, HashMap<String, Object> info) {
                mView.updateSeekBarInfo(
                    (int) info.get("Duration"), (int) info.get("CurrentPosition"));
              }

              @Override
              public void onSeekBarPossionUpdate(String msg, int total, int cur) {
                mView.updateSeekBarInfo(total, cur);
                // tricks to restrat without buffering again.
                if ((total - cur < 5) && mIsRepeat) {
                  restart();
                }
              }

              @Override
              public void onError(String id, String msg) {}

              @Override
              public void onComplete(String id, String msg) {
                if (mIsRepeat) {
                  restart();
                } else {
                  playNext();
                }
              }
            });
  }

  @Override
  public void loadAlbum(List<Node> name, int start) {
    mNodeList = name;
    curIndex = start;
  }

  @Override
  public void playNext() {
    if (curIndex < mNodeList.size() - 1) {
      curIndex++;
      play();
    }
  }

  @Override
  public void playPrevious() {
    if (curIndex > 0) {
      curIndex--;
      play();
    }
  }

  @Override
  public void play() {
    if (curIndex >= 0) {
      mPlayer.play(
          mNodeList.get(curIndex).getId(),
          mNodeList.get(curIndex).getId(),
          mNodeList.get(curIndex).getMedia_url());
    }
  }

  @Override
  public void pause() {
    mPlayer.pause();
  }

  @Override
  public void restart() {
    mPlayer.seekTo(0);
  }

  @Override
  public void resume() {
    mPlayer.resume();
  }

  @Override
  public void download() {}

  @Override
  public void setSeekLocation(int time) {
    mPlayer.seekTo(time);
  }

  @Override
  public void suffle() {
    long seed = System.nanoTime();
    Collections.shuffle(mNodeList, new Random(seed));
    Collections.shuffle(mNodeList, new Random(seed));
  }

  @Override
  public void setRepeat(boolean isRepeat) {
    mIsRepeat = isRepeat;
  }

  @Override
  public boolean isPlaying() {
    return mPlayer.isPlaying();
  }

  @Override
  public void playOrResume() {
    if (mPlayer.isPaused()) {
      mPlayer.resume();
    } else {
      play();
    }
  }

  @Override
  public void setMuteState(boolean b) {}

  @Override
  public void setLikeState(boolean b) {}

  private Node getMusicNodeForID(String id) {
    for (Node mn : mNodeList) {
      if (id.equals(mn.getId())) {
        return mn;
      }
    }
    return null;
  }
}
