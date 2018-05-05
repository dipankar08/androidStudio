package in.peerreview.fmradioindia.activities.radio;

import in.co.dipankar.quickandorid.utils.Player;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.models.Node;
import in.peerreview.fmradioindia.common.models.NodeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class RadioPresenter implements IRadioContract.Presenter {

  static final String TAG = "RadioPresenter";
  private IRadioContract.View mView;
  private static List<Node> mAllNodes;
  private static List<Node> mNodes;
  private int mCurNodeIdx = 0;

  private Player mPlayer;

  public RadioPresenter(IRadioContract.View loginView) {
    mView = loginView;
    mAllNodes = FMRadioIndiaApplication.Get().getNodeManager().getList();
    mNodes = mAllNodes;

    mPlayer =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(String msg) {
                mView.renderTryPlayUI("Try playing " + msg);
              }

              @Override
              public void onSuccess(String msg) {
                mView.renderPauseUI("Now Playing " + msg);
                FMRadioIndiaApplication.Get().getNodeManager().addToRecent(mNodes.get(mCurNodeIdx));
              }

              @Override
              public void onResume(String msg) {
                mView.renderPauseUI(msg + " stopped");
              }

              @Override
              public void onPause(String msg) {
                mView.renderPlayUI("Now Playing " + msg);
              }

              @Override
              public void onMusicInfo(HashMap<String, Object> info) {}

              @Override
              public void onSeekBarPossionUpdate(int total, int cur) {}

              @Override
              public void onError(String msg) {
                mView.renderPlayUI(msg);
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit("RadioPresenter_player_onError");
              }

              @Override
              public void onComplete(String msg) {
                mView.renderPlayUI(msg);
              }
            });

    FMRadioIndiaApplication.Get()
        .getNodeManager()
        .addCallback(
            new NodeManager.Callback() {
              @Override
              public void onItemAddToFeb() {
                mView.showToast("Successfully added to fev");
              }

              @Override
              public void onItemRemFromFeb() {
                mView.showToast("Successfully removed from fev");
              }
            });
  }

  @Override
  public void play(int pos) {
    mCurNodeIdx = pos;
    playCurrent();
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
    public void scheduleAutoSleep(int i) {

    }

    @Override
    public void scheduleAutoStart(int i, int mCurrentSelection) {

    }

    @Override
    public void playById(String id) {
      if(id == null){
          return;
      }
        for(int i =0; i<mNodes.size();i++){
            if(mNodes.get(i).getId().equals(id)){
                mCurNodeIdx = i;
                playCurrent();
            }
        }
    }

    @Override
  public void playCurrent() {
    if (mCurNodeIdx >= 0 && mCurNodeIdx < mNodes.size()) {
      Node cur = mNodes.get(mCurNodeIdx);
      if (mPlayer.isPlaying()) {
        mPlayer.pause();
      } else {
        mPlayer.play(cur.getTitle(), cur.getMedia_url());
      }
    }
  }

  @Override
  public void playPrevious() {
    do {
      mCurNodeIdx = (mCurNodeIdx == 0) ? mNodes.size() - 1 : mCurNodeIdx - 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());

    Node cur = mNodes.get(mCurNodeIdx);
    mPlayer.play(cur.getTitle(), cur.getMedia_url());
  }

  @Override
  public void playNext() {
    do {
      mCurNodeIdx = (mCurNodeIdx == mNodes.size() - 1) ? 0 : mCurNodeIdx + 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());

    Node cur = mNodes.get(mCurNodeIdx);
    mPlayer.play(cur.getTitle(), cur.getMedia_url());
  }

  @Override
  public void makeCurrentFev(boolean a) {
    if (mCurNodeIdx < 0) {
      return;
    }
    FMRadioIndiaApplication.Get().getNodeManager().handleFavorite(mNodes.get(mCurNodeIdx), a);
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
}
