package in.peerreview.fmradioindia.activities.radio;

import static in.peerreview.fmradioindia.common.Configuration.RANK_DOWN_URL;
import static in.peerreview.fmradioindia.common.Configuration.RANK_UP_URL;

import android.support.annotation.Nullable;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.Player;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.Configuration;
import in.peerreview.fmradioindia.common.Constants;
import in.peerreview.fmradioindia.common.models.Node;
import in.peerreview.fmradioindia.common.models.NodeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

public class RadioPresenter implements IRadioContract.Presenter {

  static final String TAG = "RadioPresenter";
  private IRadioContract.View mView;
  private static List<Node> mAllNodes = null;
  @Nullable private static List<Node> mNodes = null;
  private int mCurNodeIdx = 0;
  private String mCurNodeID = null; /* This is the curent node that is playing */
  private String mCurPlayingID = null;

  private Player mPlayer;

  public RadioPresenter(IRadioContract.View loginView) {
    mView = loginView;
    mAllNodes = FMRadioIndiaApplication.Get().getNodeManager().getList();
    mNodes = mAllNodes;

    mPlayer =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(final String id, String msg) {
                mView.renderTryPlayUI("Try playing " + msg);
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit(Constants.TELEMETRY_PLAYER_TRY_PLAYING);
                updateStatOnDBNodes(id, "count_click");
              }

              @Override
              public void onSuccess(String id, String msg) {
                mCurPlayingID = id;
                mView.renderPlayUI("Now Playing " + msg);
                if (mNodes != null && mCurNodeIdx < mNodes.size()) {
                  FMRadioIndiaApplication.Get()
                      .getNodeManager()
                      .addToRecent(mNodes.get(mCurNodeIdx));
                }
                FMRadioIndiaApplication.Get()
                    .getNetwork()
                    .retrive(RANK_UP_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
                updateStatOnDBNodes(id, "count_success");

                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit(Constants.TELEMETRY_PLAYER_SUCCESS);
              }

              @Override
              public void onResume(String id, String msg) {
                mView.renderPlayUI("Resume playing: " + msg);
                mCurPlayingID = id;
              }

              @Override
              public void onPause(String id, String msg) {
                mView.renderPauseUI("Stop Playing: " + msg);
                mCurPlayingID = null;
              }

              @Override
              public void onMusicInfo(String id, HashMap<String, Object> info) {}

              @Override
              public void onSeekBarPossionUpdate(String id, int total, int cur) {}

              @Override
              public void onError(String id, String msg) {
                mView.renderPauseUI(msg);
                mView.notifyError("Looks like this FM is not working anymore :(");
                FMRadioIndiaApplication.Get()
                    .getNetwork()
                    .retrive(RANK_DOWN_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);

                updateStatOnDBNodes(id, "count_error");
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit(Constants.TELEMETRY_PLAYER_ERROR);
                mCurPlayingID = null;
              }

              @Override
              public void onComplete(String id, String msg) {
                mCurPlayingID = null;
                mView.renderPlayUI(msg);
              }
            });

    FMRadioIndiaApplication.Get()
        .getNodeManager()
        .addCallback(
            new NodeManager.Callback() {
              @Override
              public void onItemAddToFeb(List<Node> list) {
                mView.updateQuickList(list);
              }

              @Override
              public void onItemRemFromFeb(List<Node> list) {
                mView.updateQuickList(list);
              }
            });
  }

  @Override
  public void play(int pos) {
    mCurNodeIdx = pos;
    play();
  }

  @Override
  public void loadData() {
    mView.updateList(mNodes);
  }

  private void populateSets(List<Node> nodes) {
    if (nodes == null) return;
    mNodes = nodes;
    mView.updateList(mNodes);
  }

  @Override
  public void showRecent() {
    populateSets(FMRadioIndiaApplication.Get().getNodeManager().getRecent());
  }

  @Override
  public void showFeb() {
    populateSets(FMRadioIndiaApplication.Get().getNodeManager().getFavorite());
  }

  @Override
  public void scheduleAutoSleep(int i) {}

  @Override
  public void scheduleAutoStart(int i, int mCurrentSelection) {}

  @Override
  public void playById(String id) {
    if (id == null || mNodes == null) {
      return;
    }
    boolean ffound = false;
    for (int i = 0; i < mNodes.size(); i++) {
      if (mNodes.get(i).getId().equals(id)) {
        mCurNodeIdx = i;
        break;
      }
    }
    if (FMRadioIndiaApplication.Get().getNodeManager().getNodeById(id) != null) {
      mCurNodeID = FMRadioIndiaApplication.Get().getNodeManager().getNodeById(id).getId();
      play();
    }
  }

  @Override
  public void onFevClicked() {
    Node cur = getCurrentNode();
    if (cur != null) {
      if (FMRadioIndiaApplication.Get().getNodeManager().isFev(cur)) {
        FMRadioIndiaApplication.Get().getNodeManager().handleFavorite(cur, false);
        mView.onUpdateFevButtonState(false);
      } else {
        FMRadioIndiaApplication.Get().getNodeManager().handleFavorite(cur, true);
        mView.onUpdateFevButtonState(true);
      }
    }
  }

  @Override
  public void playPause() {
    if (mPlayer.isPlaying()) {
      mPlayer.pause();
    } else {
      play();
    }
  }

  @Override
  public void playPrevious() {
    if (mNodes == null || mNodes.size() < 2) {
      mView.notifyError("Re-Search and tab on the channel");
      return;
    }
    mCurNodeID = null;
    do {
      mCurNodeIdx = (mCurNodeIdx == 0) ? mNodes.size() - 1 : mCurNodeIdx - 1;
      if (mNodes == null || mCurNodeIdx >= mNodes.size() || mCurNodeIdx < 0) {
        return;
      }
    } while (!mNodes.get(mCurNodeIdx).isSongType());
    DLog.d("playPrevious called : Index:" + mCurNodeIdx);
    play();
  }

  @Override
  public void playNext() {
    if (mNodes == null || mNodes.size() < 2) {
      mView.notifyError("Re-Search and tab on the channel");
      return;
    }

    mCurNodeID = null;
    do {
      mCurNodeIdx = (mCurNodeIdx == mNodes.size() - 1) ? 0 : mCurNodeIdx + 1;
      if (mNodes == null || mCurNodeIdx >= mNodes.size() || mCurNodeIdx < 0) {
        return;
      }
    } while (!mNodes.get(mCurNodeIdx).isSongType());
    DLog.d("PlayNext called : Index:" + mCurNodeIdx);
    play();
  }

  private void play() {
    Node temp = getCurrentNode();
    if (temp != null) {
      mPlayer.play(temp.getId(), temp.getTitle(), temp.getMedia_url());
    } else {
      mView.notifyError("Re-Search and tab on the channel");
    }
  }

  private Node getCurrentNode() {
    Node temp = null;
    if (mCurNodeID != null) {
      temp = FMRadioIndiaApplication.Get().getNodeManager().getNodeById(mCurNodeID);
    } else {
      if (mCurNodeIdx >= 0 && mCurNodeIdx < mNodes.size()) {
        temp = mNodes.get(mCurNodeIdx);
      }
    }
    return temp;
  }

  @Override
  public void makeCurrentFev(boolean a) {
    if (mCurNodeIdx < 0) {
      return;
    }
    FMRadioIndiaApplication.Get().getNodeManager().handleFavorite(getCurrentNode(), a);
  }

  @Override
  public void filterByText(String text) {
    if (mAllNodes == null) return;
    ArrayList<Node> filterdNames = new ArrayList<>();
    for (Node n : mAllNodes) {
      if (!n.isSongType()) continue;
      if (n.getTitle().toLowerCase().contains(text.toLowerCase())) {
        filterdNames.add(n);
      }
    }
    mNodes = filterdNames;
    mView.updateList(mNodes);
  }

  @Override
  public void filterByTag(String text) {
    if (mAllNodes == null) return;
    ArrayList<Node> filterdNames = new ArrayList<>();
    for (Node n : mAllNodes) {
      if (!n.isSongType()) continue;
      if (n.getTags().toLowerCase().contains(text.toLowerCase())) {
        filterdNames.add(n);
      }
    }
    mNodes = filterdNames;
    mView.updateList(mNodes);
  }

  @Override
  public void clearFilter() {
    mNodes = mAllNodes;
    mView.updateList(mNodes);
  }

  @Override
  public void setCurNodeID(String id) {
    mCurNodeID = id;
  }

  @Override
  public Node getItembyID(String channel_id) {
    return FMRadioIndiaApplication.Get().getNodeManager().getNodeById(channel_id);
  }

  @Override
  public void stopPlay() {
    mPlayer.stop();
  }

  private void updateStatOnDBNodes(final String id, final String type) {
    FMRadioIndiaApplication.Get()
        .getNetwork()
        .send(
            Configuration.DB_ENDPOINT,
            new HashMap<String, String>() {
              {
                put("_cmd", "increment");
                put("id", id);
                put("_payload", type);
              }
            },
            new Network.Callback() {
              @Override
              public void onSuccess(JSONObject jsonObject) {}

              @Override
              public void onError(String msg) {}
            });
  }

  public String getCurrentPlayingID() {
    return mCurPlayingID;
  }

  @Override
  public boolean isPlaying() {
    if (mPlayer == null) {
      return false;
    }
    return mPlayer.isPlaying();
  }
}
