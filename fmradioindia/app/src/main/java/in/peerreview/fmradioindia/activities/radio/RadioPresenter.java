package in.peerreview.fmradioindia.activities.radio;

import static in.peerreview.fmradioindia.common.Configuration.RANK_DOWN_URL;
import static in.peerreview.fmradioindia.common.Configuration.RANK_UP_URL;

import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.Player;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.models.Node;
import in.peerreview.fmradioindia.common.models.NodeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadioPresenter implements IRadioContract.Presenter {

  static final String TAG = "RadioPresenter";
  private IRadioContract.View mView;
  private static List<Node> mAllNodes;
  private static List<Node> mNodes;
  private int mCurNodeIdx = 0;
  private String mCurNodeID = null; /* This is the curent node that is playing */

  private Player mPlayer;

  public RadioPresenter(IRadioContract.View loginView) {
    mView = loginView;
    mAllNodes = FMRadioIndiaApplication.Get().getNodeManager().getList();
    mNodes = mAllNodes;

    mPlayer =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(String id, String msg) {
                mView.renderTryPlayUI("Try playing " + msg);
              }

              @Override
              public void onSuccess(String id, String msg) {
                mView.renderPlayUI("Now Playing " + msg);
                FMRadioIndiaApplication.Get().getNodeManager().addToRecent(mNodes.get(mCurNodeIdx));
                FMRadioIndiaApplication.Get()
                    .getNetwork()
                    .retrive(RANK_UP_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
              }

              @Override
              public void onResume(String id, String msg) {
                mView.renderPlayUI("Resume playing: " + msg);
              }

              @Override
              public void onPause(String id, String msg) {
                mView.renderPauseUI("Stop Playing: " + msg);
              }

              @Override
              public void onMusicInfo(String id, HashMap<String, Object> info) {}

              @Override
              public void onSeekBarPossionUpdate(String id, int total, int cur) {}

              @Override
              public void onError(String id, String msg) {
                mView.renderPauseUI("Error occured while playing: " + msg);
                FMRadioIndiaApplication.Get()
                    .getNetwork()
                    .retrive(RANK_DOWN_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);

                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit("RadioPresenter_player_onError");
              }

              @Override
              public void onComplete(String id, String msg) {
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
    if (id == null) {
      return;
    }
    boolean ffound = false;
    for (int i = 0; i < mNodes.size(); i++) {
      if (mNodes.get(i).getId().equals(id)) {
        mCurNodeIdx = i;
        break;
      }
    }

    mCurNodeID = FMRadioIndiaApplication.Get().getNodeManager().getNodeById(id).getId();
    play();
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
    mCurNodeID = null;
    do {
      mCurNodeIdx = (mCurNodeIdx == 0) ? mNodes.size() - 1 : mCurNodeIdx - 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());
    play();
  }

  @Override
  public void playNext() {
    mCurNodeID = null;
    do {
      mCurNodeIdx = (mCurNodeIdx == mNodes.size() - 1) ? 0 : mCurNodeIdx + 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());
    play();
  }

  private void play() {
    Node temp = getCurrentNode();
    if (temp != null) {
      mPlayer.play(temp.getId(), temp.getTitle(), temp.getMedia_url());
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
}
