package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.search.SearchView;

public class SearchFragment extends BaseFragment {
  public static SearchFragment getNewFragment(Bundle args) {
    SearchFragment myFragment = new SearchFragment();
    myFragment.setArguments(args);
    return myFragment;
  }

  // The onCreateView method is called when Fragment should create its View object hierarchy,
  // either dynamically or via XML layout inflation.
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    SearchView view = new SearchView(getContext());
    view.setArgs(getArguments());
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    // Setup any handles to view objects here
    // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
  }
}
