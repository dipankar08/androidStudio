package in.co.dipankar.fmradio.ui.fragments.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseFragment;

public class SplashFragment extends BaseFragment {
    public static SplashFragment getNewFragment(Bundle args) {
        SplashFragment myFragment = new SplashFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}