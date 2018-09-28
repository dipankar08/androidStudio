package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.setting.SettingView;

public class SettingFragment extends BaseFragment {
  public static SettingFragment getNewFragment(Bundle args) {
    SettingFragment myFragment = new SettingFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    SettingView view = new SettingView(getContext());
    view.setArgs(getArguments());
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {}
}
