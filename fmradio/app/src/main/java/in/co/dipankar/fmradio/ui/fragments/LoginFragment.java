package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.home.HomeView;

public class LoginFragment extends BaseFragment {
    public static LoginFragment getNewFragment(Bundle args) {
        LoginFragment myFragment = new LoginFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return new HomeView(getContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }
}