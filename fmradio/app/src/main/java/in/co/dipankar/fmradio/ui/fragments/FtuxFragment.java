package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.viewpresenter.ftux.FtuxView;

public class FtuxFragment extends BaseFragment {
  public static FtuxFragment getNewFragment(Bundle args) {
    FtuxFragment myFragment = new FtuxFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  // The onCreateView method is called when Fragment should create its View object hierarchy,
  // either dynamically or via XML layout inflation.
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    // return inflater.inflate(R.layout.fragment_home, parent, false);
    return new FtuxView(getContext());
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ((BaseView) view).setArgs(savedInstanceState);
  }
}
