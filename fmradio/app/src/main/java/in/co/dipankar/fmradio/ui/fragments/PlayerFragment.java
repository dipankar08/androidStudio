package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.player.FullScreenPlayerView;

public class PlayerFragment extends BaseFragment {
  public static PlayerFragment getNewFragment(Bundle args) {
    PlayerFragment myFragment = new PlayerFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    FullScreenPlayerView view = new FullScreenPlayerView(getContext());
    view.setArgs(getArguments());
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    // ((BaseView)view).setArgs(getArguments());
  }
}
