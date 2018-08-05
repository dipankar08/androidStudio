package in.co.dipankar.fmradio.ui.base;

import android.support.annotation.Nullable;

public class BasePresenter<V> {
    private V mView;
    public final void attachView(V view) {
        if (view == null) {
            return;
        }
        mView = view;
    }

    public final void detachView() {
        mView = null;
    }

    public @Nullable
    V getView() {
        return mView;
    }
}
