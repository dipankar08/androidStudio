package in.co.dipankar.fmradio.ui.base;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
  public Navigation getNavigation() {
    return ((BaseNavigationActivity) getActivity()).getNavigation();
  }
}
