package in.co.dipankar.samplemvp.mainview.model;

import java.util.List;

/**
 * Created by dip on 1/24/18.
 */

public interface IItemProvider {

    interface OnFinishedListener {
        void onFinished(List<String> items);
    }

    void loadItems(OnFinishedListener listener);
}
