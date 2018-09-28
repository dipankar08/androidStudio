package in.co.dipankar.livetv.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.livetv.base.BaseFragment;

public class PlayerFragment extends BaseFragment {
  public static PlayerFragment getNewFragment(Bundle args) {
    PlayerFragment myFragment = new PlayerFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    PlayerView view = new PlayerView(getContext());
    view.setArgs(getArguments());
    return view;
  }
}
