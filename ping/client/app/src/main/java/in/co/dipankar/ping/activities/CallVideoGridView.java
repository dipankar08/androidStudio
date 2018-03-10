package in.co.dipankar.ping.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.webrtc.SurfaceViewRenderer;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.common.signaling.IVideoView;


public class CallVideoGridView extends RelativeLayout implements View.OnClickListener, IVideoView{

    public interface Callback {
        void onClickedEnd();
        void onClickedToggleVideo(boolean isOn);
        void onClickedToggleAudio(boolean isOn);
    }

    private Callback mCallback;
    private Context mContext;
    LayoutInflater mInflater;
    private  MediaPlayer ring;
    private SurfaceViewRenderer mPeerView;
    private SurfaceViewRenderer mSelfView;

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public CallVideoGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallVideoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallVideoGridView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_video_grid, this, true);
        mPeerView = (SurfaceViewRenderer) findViewById(R.id.pip_video_view);
        mSelfView = (SurfaceViewRenderer) findViewById(R.id.fullscreen_video_view);
        mPeerView.setOnClickListener(this);
        mSelfView.setOnClickListener(this);
    }

    private void makeFullScreen(View view){
        FrameLayout.LayoutParams lay = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lay);
        view.requestLayout();
    }

    private void makeSmallView(View view){
        FrameLayout.LayoutParams lay = new FrameLayout.LayoutParams(300,400);
        view.setLayoutParams(lay);
        view.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pip_video_view:
                makeFullScreen((View)mPeerView);
                makeSmallView((View)mSelfView);
                mSelfView.bringToFront();
                break;
            case R.id.fullscreen_video_view:
                makeFullScreen((View)mSelfView);
                makeSmallView((View)mPeerView);
                mPeerView.bringToFront();
                break;
        }
    }

    @Override
    public SurfaceViewRenderer getSelfVideoView(){
        return mSelfView;
    }

    @Override
    public SurfaceViewRenderer getPeerVideoView(){
        return mPeerView;
    }
    
    public void show(){
        ring.start();
        this.setVisibility(View.VISIBLE);
    }
    public void hide(){
        ring.pause();
        this.setVisibility(View.GONE);
    }

}