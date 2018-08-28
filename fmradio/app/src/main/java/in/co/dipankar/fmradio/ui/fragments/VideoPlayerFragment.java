package in.co.dipankar.fmradio.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.viewpresenter.player.FullScreenPlayerView;
import in.co.dipankar.fmradio.ui.viewpresenter.videoplayer.VideoPlayerView;

public class VideoPlayerFragment extends BaseFragment {
    public static VideoPlayerFragment getNewFragment(Bundle args) {
        VideoPlayerFragment myFragment = new VideoPlayerFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        VideoPlayerView view = new VideoPlayerView(getContext());
         view.setArgs(getArguments());
         return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //((BaseView)view).setArgs(getArguments());
    }
}