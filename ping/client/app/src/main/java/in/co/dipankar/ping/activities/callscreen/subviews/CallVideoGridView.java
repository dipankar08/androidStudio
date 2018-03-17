package in.co.dipankar.ping.activities.callscreen.subviews;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.webrtc.SurfaceViewRenderer;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IMultiVideoPane;


public class CallVideoGridView extends RelativeLayout implements View.OnClickListener, IMultiVideoPane{

    public void setlayout(Layout layout) {
        switch(layout){
            case SELF_VIEW_FULL_SCREEN:
                FrameLayout.LayoutParams lay = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                FrameLayout.LayoutParams lay1 = new FrameLayout.LayoutParams(mDeviceWidth/5, mDeviceHeight/5);
                mSelfView.setLayoutParams(lay);
                mPeerView.setLayoutParams(lay1);
                mSelfView.bringToFront();
                break;
            case PEER_VIEW_FULL_SCREEN:
                FrameLayout.LayoutParams lay2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                FrameLayout.LayoutParams lay3 = new FrameLayout.LayoutParams(mDeviceWidth/5, mDeviceHeight/5);
                mPeerView.setLayoutParams(lay2);
                mSelfView.setLayoutParams(lay3);
                mPeerView.bringToFront();
                break;
            case SPLIT_VIEW:
                FrameLayout.LayoutParams lay5 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mDeviceHeight/2);
                FrameLayout.LayoutParams lay6 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mDeviceHeight/2);
                lay5.gravity= Gravity.TOP;
                lay6.gravity= Gravity.BOTTOM;
                mPeerView.setLayoutParams(lay5);
                mSelfView.setLayoutParams(lay6);
                break;
        }
        mSelfView.requestLayout();
        mPeerView.requestLayout();
    }

    @Override
    public SurfaceViewRenderer getSelfView() {
        return mSelfView;
    }

    @Override
    public SurfaceViewRenderer getPeerView() {
        return mPeerView;
    }

    public enum Layout {
        SELF_VIEW_FULL_SCREEN,
        PEER_VIEW_FULL_SCREEN,
        SPLIT_VIEW
    }

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

    private int mDeviceHeight, mDeviceWidth;

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
        mPeerView = (SurfaceViewRenderer) findViewById(R.id.peer_view);
        mSelfView = (SurfaceViewRenderer) findViewById(R.id.self_view);
        mPeerView.setOnClickListener(this);
        mSelfView.setOnClickListener(this);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDeviceHeight = displayMetrics.heightPixels;
    }
/*
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
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.peer_view:
                setlayout(Layout.PEER_VIEW_FULL_SCREEN);
                break;
            case R.id.self_view:
                setlayout(Layout.SELF_VIEW_FULL_SCREEN);
                break;
        }
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