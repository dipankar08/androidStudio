package in.co.dipankar.fragmentnavigation.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BasePageFragment extends Fragment {

  private BasePageView mView;
  private Bundle mState;
  private ActivityListener mActivityListener;

  public BasePageFragment() {
    super();
  }

  public static Fragment newInstance(BasePageView view, Bundle state) {
    BasePageFragment fragment = new BasePageFragment();
    fragment.mView = view;
    fragment.mState = state;
    return fragment;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mView.setActivityListener(mActivityListener);
    return mView;
  }

  @Override
  public void setArguments(@Nullable Bundle args) {
    super.setArguments(args);
    mView.setArguments(args);
  }

  public void setActivityListener(ActivityListener listener) {
    mActivityListener = listener;
  }
}
