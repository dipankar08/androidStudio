package in.co.dipankar.livetv.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.livetv.base.BaseFragment;

public class HomeFragment extends BaseFragment {
  public static HomeFragment getNewFragment(Bundle args) {
    HomeFragment myFragment = new HomeFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    HomeView view = new HomeView(getContext());
    view.setArgs(getArguments());
    return view;
  }
}
