package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.splash.SplashView;

public class SplashFragment extends BaseFragment {
  public static SplashFragment getNewFragment(Bundle args) {
    SplashFragment myFragment = new SplashFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return new SplashView(getContext());
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {}
}
