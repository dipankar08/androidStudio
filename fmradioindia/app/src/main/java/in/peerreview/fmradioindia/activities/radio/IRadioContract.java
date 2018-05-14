package in.peerreview.fmradioindia.activities.radio;

import in.peerreview.fmradioindia.common.models.Node;
import java.util.List;

public interface IRadioContract {

  public interface View {
    void updateList(List<Node> mNodes);

    void renderTryPlayUI(String msg);

    void renderPauseUI(String msg);

    void renderPlayUI(String msg);

    void showToast(String s);

    void updateQuickList(List<Node> mNodes);

    void onUpdateFevButtonState(boolean isFev);
  }

  public interface Presenter {
    // filters
    void filterByText(String text);

    void filterByTag(String text);

    void clearFilter();

    // plays
    void playPause();

    void playPrevious();

    void playNext();

    void makeCurrentFev(boolean a);

    void play(int pos);

    void loadData();

    void showRecent();

    void showFeb();

    void scheduleAutoSleep(int i);

    void scheduleAutoStart(int i, int mCurrentSelection);

    void playById(String startID);

    void onFevClicked();

    void setCurNodeID(String id);

    Node getItembyID(String channel_id);
  }
}
