package in.peerreview.fmradioindia.activities.radio;

import java.util.List;

import in.peerreview.fmradioindia.activities.radio.model.RadioNode;

/**
 * Created by dip on 2/14/18.
 */

public interface IRadioContract {

    public interface View {

        void updateList(List<RadioNode> mNodes);

        void renderTryPlayUI(String msg);
        void renderPauseUI(String msg);
        void renderPlayUI(String msg);
    }

    public interface Presenter{
        //filters
        void filterByText(String text);
        void filterByTag(String text);
        void clearFilter();

        //plays
        void playCurrent();
        void playPrevious();
        void playNext();
        void makeCurrentFev();

        void play(int pos);

        void loadData();
    }

}
