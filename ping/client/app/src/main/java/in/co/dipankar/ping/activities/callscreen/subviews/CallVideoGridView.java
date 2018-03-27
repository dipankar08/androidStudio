package in.co.dipankar.ping.activities.callscreen.subviews;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.webrtc.SurfaceViewRenderer;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.common.webrtc.ExtendedSurfaceView;
import in.co.dipankar.ping.contracts.IMultiVideoPane;

import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN;
import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN;
import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.MiniViewAlignment.BOTTOM_LEFT;
import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.MiniViewAlignment.BOTTOM_RIGHT;
import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.MiniViewAlignment.TOP_LEFT;
import static in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView.MiniViewAlignment.TOP_RIGHT;


public class CallVideoGridView extends RelativeLayout implements IMultiVideoPane{

    private Callback mCallback;
    private Context mContext;

    View mRootView;
    private ExtendedSurfaceView mPeerView;
    private ExtendedSurfaceView mSelfView;

    private final int mMiniViewSize = (int)pxFromDp(100);
    private final int mButtonHeight = 400;

    private Layout mCurrentLayout = SELF_VIEW_FULL_SCREEN;

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
        LayoutInflater mInflater = LayoutInflater.from(context);
        mRootView = mInflater.inflate(R.layout.view_video_grid, this, true);

        mPeerView = (ExtendedSurfaceView) findViewById(R.id.peer_view);
        mSelfView = (ExtendedSurfaceView) findViewById(R.id.self_view);

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsMovingMiniView){
                    //click evet
                    if (mCurrentLayout != SELF_VIEW_FULL_SCREEN) {
                        setlayout(SELF_VIEW_FULL_SCREEN);
                    } else {
                        setlayout(PEER_VIEW_FULL_SCREEN);
                    }
                }
            }
        });

        mRootView.setOnTouchListener(mOntouchListner);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDeviceHeight = displayMetrics.heightPixels;
    }


    public void setlayout(Layout layout) {
        mCurrentLayout = layout;
        switch(layout){
            case SELF_VIEW_FULL_SCREEN:
                FrameLayout.LayoutParams lay = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mSelfView.setLayoutParams(lay);
                FrameLayout.LayoutParams lay1 = new FrameLayout.LayoutParams(mMiniViewSize, mMiniViewSize);
                mPeerView.setLayoutParams(lay1);

                mPeerView.setZOrderOnTop(true);
                mSelfView.setZOrderOnTop(false);

                mSelfView.setX(0);
                mSelfView.setY(0);
                break;
            case PEER_VIEW_FULL_SCREEN:

                FrameLayout.LayoutParams lay3 = new FrameLayout.LayoutParams(mMiniViewSize, mMiniViewSize);
                mSelfView.setLayoutParams(lay3);

                FrameLayout.LayoutParams lay4 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mPeerView.setLayoutParams(lay4);

                mPeerView.setZOrderOnTop(false);
                mSelfView.setZOrderOnTop(true);

                mPeerView.setX(0);
                mPeerView.setY(0);
                break;
            case SPLIT_VIEW:

                break;
        }
        alignMiniView(mCurMiniViewAlign);
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

    private ExtendedSurfaceView getCurrentMiniView(){
        if(mCurrentLayout == Layout.PEER_VIEW_FULL_SCREEN){
            return mSelfView;
        } else{
            return mPeerView;
        }
    }
    public enum MiniViewAlignment{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    };
    private MiniViewAlignment mCurMiniViewAlign = TOP_LEFT;
    private void alignMiniView(MiniViewAlignment alignment) {
        mCurMiniViewAlign = alignment;
        ExtendedSurfaceView curMini = getCurrentMiniView();
        int x =0, y=0;
        switch (alignment){
            case TOP_LEFT:
                x = 0;
                y =0;
                break;
            case TOP_RIGHT:
                x = mDeviceWidth - mMiniViewSize;
                y =0;
                break;
            case BOTTOM_LEFT:
                x = 0;
                y =mDeviceHeight-mMiniViewSize-mButtonHeight;
                break;
            case BOTTOM_RIGHT:
                x = mDeviceWidth - mMiniViewSize;
                y =mDeviceHeight-mMiniViewSize -mButtonHeight;
                break;
        }
        curMini.setX(x);
        curMini.setY(y);
    }



    public interface Callback {
        void onClickedEnd();
        void onClickedToggleVideo(boolean isOn);
        void onClickedToggleAudio(boolean isOn);
    }

    private boolean mIsMovingMiniView = false;
    private OnTouchListener mOntouchListner = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int)event.getX();
            int y = (int) event.getY();
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(isViewContains(getCurrentMiniView(), x, y)){
                        mIsMovingMiniView = true;
                        return true;
                    } else{
                        mIsMovingMiniView = false;
                        return  false;
                    }
                case MotionEvent.ACTION_UP:
                    if(mIsMovingMiniView) {
                        if (x < mDeviceWidth / 2 && y < mDeviceHeight / 2) {
                            alignMiniView(TOP_LEFT);
                        } else if (x > mDeviceWidth / 2 && y < mDeviceHeight / 2) {
                            alignMiniView(TOP_RIGHT);
                        } else if (x < mDeviceWidth / 2 && y > mDeviceHeight / 2) {
                            alignMiniView(BOTTOM_LEFT);
                        } else {
                            alignMiniView(BOTTOM_RIGHT);
                        }
                    }
                    mIsMovingMiniView = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //DLog.e("X:"+event.getX()+" Y: "+event.getY()+"View:"+v);
                    if(mIsMovingMiniView) {
                        if (mCurrentLayout == Layout.PEER_VIEW_FULL_SCREEN) {
                            mSelfView.setX(event.getX() - mMiniViewSize / 2);
                            mSelfView.setY(event.getY() - mMiniViewSize / 2);
                        } else if (mCurrentLayout == Layout.SELF_VIEW_FULL_SCREEN) {
                            mPeerView.setX(event.getX() - mMiniViewSize / 2);
                            mPeerView.setY(event.getY() - mMiniViewSize / 2);
                        }
                    }
                    break;
            }
            return false;
        }
    };
    private float dpFromPx(float px) {
        return px / this.getContext().getResources().getDisplayMetrics().density;
    }

    private float pxFromDp(float dp) {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }
    private boolean isViewContains(View view, int rx, int ry) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return false;
        }
        return true;
    }
}