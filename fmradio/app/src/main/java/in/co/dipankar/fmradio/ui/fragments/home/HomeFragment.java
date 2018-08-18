package in.co.dipankar.fmradio.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.viewpresenter.home.HomeView;

public class HomeFragment extends BaseFragment {
    public static HomeFragment getNewFragment(Bundle args) {
        HomeFragment myFragment = new HomeFragment();
        myFragment.setArguments(args);
        return myFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_home, parent, false);
        return new HomeView(getContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((BaseView)view).setArgs(savedInstanceState);
    }
}