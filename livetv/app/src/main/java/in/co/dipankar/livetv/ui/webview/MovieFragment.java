package in.co.dipankar.livetv.ui.webview;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.co.dipankar.livetv.base.BaseFragment;
import in.co.dipankar.livetv.ui.splash.SplashView;

public class MovieFragment extends BaseFragment {
    public static MovieFragment getNewFragment(Bundle args) {
        MovieFragment myFragment = new MovieFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        MovieView view = new MovieView(getContext());
        view.setArgs(getArguments());
        return view;
    }
}
