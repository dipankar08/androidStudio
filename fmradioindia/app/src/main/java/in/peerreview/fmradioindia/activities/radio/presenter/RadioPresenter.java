package in.peerreview.fmradioindia.activities.radio.presenter;

import in.peerreview.fmradioindia.activities.radio.IRadioContract;
import in.peerreview.fmradioindia.activities.radio.model.RadioNode;
import in.peerreview.fmradioindia.activities.welcome.presenter.WelcomePresenter;
import in.peerreview.fmradioindia.common.utils.Player;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RadioPresenter implements IRadioContract.Presenter {

  static final String TAG = "RadioPresenter";
  private IRadioContract.View mView;
  private static List<RadioNode> mAllNodes;
  private static List<RadioNode> mNodes;
  private static LinkedList<RadioNode> feblist;
  private int mCurNodeIdx = 0;
  private Player mPlayer;

  public RadioPresenter(IRadioContract.View loginView) {
    mView = loginView;
    mAllNodes = WelcomePresenter.getPreFetchedNode();
    mNodes = mAllNodes;

    mPlayer =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(String msg) {
                mView.renderTryPlayUI(msg);
              }

              @Override
              public void onSuccess(String msg) {
                mView.renderPauseUI(msg);
              }

              @Override
              public void onError(String msg) {
                mView.renderPlayUI(msg);
              }

              @Override
              public void onComplete(String msg) {
                mView.renderPlayUI(msg);
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

  @Override
  public void playCurrent() {

    if (mCurNodeIdx >= 0 && mCurNodeIdx < mNodes.size()) {
      RadioNode cur = mNodes.get(mCurNodeIdx);
      mPlayer.play(cur.getName(), cur.getUrl());
    } else {

    }
  }

  @Override
  public void playPrevious() {

    do {
      mCurNodeIdx = (mCurNodeIdx == 0) ? mNodes.size() - 1 : mCurNodeIdx - 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());

    RadioNode cur = mNodes.get(mCurNodeIdx);
    mPlayer.play(cur.getName(), cur.getUrl());
  }

  @Override
  public void playNext() {
    do {
      mCurNodeIdx = (mCurNodeIdx == mNodes.size() - 1) ? 0 : mCurNodeIdx + 1;
    } while (!mNodes.get(mCurNodeIdx).isSongType());

    RadioNode cur = mNodes.get(mCurNodeIdx);
    mPlayer.play(cur.getName(), cur.getUrl());
  }

  @Override
  public void makeCurrentFev() {}

  @Override
  public void filterByText(String text) {
    if (mAllNodes == null) return;
    ArrayList<RadioNode> filterdNames = new ArrayList<>();
    for (RadioNode n : mAllNodes) {
      if (!n.isSongType()) continue;
      if (n.getName().toLowerCase().contains(text.toLowerCase())) {
        filterdNames.add(n);
      }
    }
    mNodes = filterdNames;
    mView.updateList(mNodes);
  }

  @Override
  public void filterByTag(String text) {
    if (mAllNodes == null) return;
    ArrayList<RadioNode> filterdNames = new ArrayList<>();
    for (RadioNode n : mAllNodes) {
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

  /*

      public static List<RadioNode> getFavorite(){
          if(feblist == null){
              feblist = Paper.book().read("FevList", new LinkedList());
          }
          return feblist;
      }
      public static void handleFavorite(RadioNode temp){
          if(feblist == null){
              feblist = Paper.book().read("FevList", new LinkedList());
          }
          RadioNode found = null;
          for (RadioNode a : feblist) {
              if(!a.isSongType()) continue;
              if(a.getName().equals(temp.getName())){
                  //exist - remove
                  found = a;
                  break;
              }
          }
          if(found != null){
              feblist.remove(found);
              .disableFeb();
          } else{
              feblist.add(0,temp);
              if(feblist.size() == 11){
                  feblist.remove(feblist.size() - 1);
              }
              MainActivity.Get().enableFeb();
          }
          Paper.book().write("FevList", feblist);
      }
      public static boolean isFev(Nodes n) {
          if(feblist == null){
              feblist = Paper.book().read("FevList", new LinkedList());
          }
          for (Nodes a : feblist) {
              if(a.getType() == 0) continue;
              if(a.getName().equals(n.getName())){
                  return true;
              }
          }
          return false;
      }



      private static LinkedList<Nodes> rectlist;
      public static List<Nodes> getRecent(){
          if(rectlist == null){
              rectlist = Paper.book().read("RecentList", new LinkedList());
          }
          return rectlist;
      }
      public static void addToRecent(Nodes n){
          if(n.getType() == 0) return;
          if(rectlist == null){
              rectlist = Paper.book().read("RecentList", new LinkedList());
          }
          for (Nodes a : rectlist) {
              if(a.getType() == 0) continue;
              if(a.getName().equals(n.getName())){
                  return;
              }
          }
          rectlist.add(0,n);
          if(rectlist.size() == 11){
              rectlist.remove(rectlist.size() - 1);
          }
          Paper.book().write("RecentList", rectlist);
      }
  */

}
