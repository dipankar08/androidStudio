package in.co.dipankar.fmradio.ui.fragments.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.viewpresenter.player.FullScreenPlayerView;

public class PlayerFragment extends BaseFragment {
    public static PlayerFragment getNewFragment(Bundle args) {
        PlayerFragment myFragment = new PlayerFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_player, parent, false);
         FullScreenPlayerView view = new FullScreenPlayerView(getContext());
         view.setArgs(getArguments());
         return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //((BaseView)view).setArgs(getArguments());
    }
}