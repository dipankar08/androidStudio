package in.co.dipankar.livetv.ui.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.livetv.base.BaseFragment;

public class SplashFragment extends BaseFragment {
  public static SplashFragment getNewFragment(Bundle args) {
    SplashFragment myFragment = new SplashFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    SplashView view = new SplashView(getContext());
    view.setArgs(getArguments());
    return view;
  }
}
