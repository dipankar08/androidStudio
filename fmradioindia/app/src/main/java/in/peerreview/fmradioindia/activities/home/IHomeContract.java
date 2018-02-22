package in.peerreview.fmradioindia.activities.home;

import java.util.List;

import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.views.MusicHozListView;

/**
 * Created by dip on 2/21/18.
 */

public interface IHomeContract {

    interface View {
        void renderItem(String type, List<MusicNode> list);
    }

    interface Presenter {
         void loadAlbum(final String type);
        List<MusicNode> getData(String name);
    }
}
