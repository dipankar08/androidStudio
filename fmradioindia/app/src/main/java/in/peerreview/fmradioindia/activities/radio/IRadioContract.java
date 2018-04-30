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
  }

  public interface Presenter {
    // filters
    void filterByText(String text);

    void filterByTag(String text);

    void clearFilter();

    // plays
    void playCurrent();

    void playPrevious();

    void playNext();

    void makeCurrentFev(boolean a);

    void play(int pos);

    void loadData();

    void showRecent();

    void showFeb();
  }
}
