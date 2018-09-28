package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.categoriesview.CategoriesView;

public class CategoriesFragment extends BaseFragment {
  public static CategoriesFragment getNewFragment(Bundle args) {
    CategoriesFragment myFragment = new CategoriesFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    CategoriesView view = new CategoriesView(getContext());
    view.setArgs(getArguments());
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    // ((BaseView)view).setArgs(getArguments());
  }
}
