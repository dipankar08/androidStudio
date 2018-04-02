package in.co.dipankar.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.co.dipankar.quickandorid.views.StateImageButton;


public class CallOngoingPageView extends RelativeLayout {

    public interface Callback {
        void onClickEnd();
        void onClickToggleVideo(boolean isOn);
        void onClickToggleAudio(boolean isOn);
        void onClickToggleCamera(boolean isOn);
        void onClickToggleSpeaker(boolean isOn);
        void onClickToggleLayout(int idx);
    }

    private Callback mCallback;
    LayoutInflater mInflater;

    private View mRootView;
    private ViewletPeerInfoAudio mViewletPeerInfoAudio;
    private ViewletPeerInfoVideo mViewletPeerInfoVideo;


    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public CallOngoingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallOngoingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallOngoingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        mRootView = mInflater.inflate(R.layout.view_call_ongoing_page, this, true);
        initButtons();
        mViewletPeerInfoAudio = findViewById(R.id.peer_audio_info);
        mViewletPeerInfoVideo = findViewById(R.id.peer_video_info);
    }

    private void initButtons(){
        StateImageButton audio =  mRootView.findViewById(R.id.toggle_audio);
        ImageButton end =  mRootView.findViewById(R.id.end);
        StateImageButton video =  mRootView.findViewById(R.id.toggle_video);
        StateImageButton camera =  mRootView.findViewById(R.id.toggle_camera);
        StateImageButton speaker =  mRootView.findViewById(R.id.toggle_speaker);
        audio.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickToggleAudio(b);
            }
        });
        video.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickToggleVideo(b);
            }
        });
        end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickEnd();
            }
        });
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickToggleCamera(!camera.isViewEnabled());
            }
        });
        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickToggleSpeaker(!speaker.isViewEnabled());
            }
        });
    }

    public void renderAudioPeerView(IRtcUser user){
        mViewletPeerInfoAudio.setVisibility(VISIBLE);
        mViewletPeerInfoVideo.setVisibility(GONE);
        mViewletPeerInfoAudio.updateView(user);
    }

    public void renderVideoPeerView(IRtcUser user){
        mViewletPeerInfoAudio.setVisibility(GONE);
        mViewletPeerInfoVideo.setVisibility(VISIBLE);
        mViewletPeerInfoVideo.updateView(user);
        mViewletPeerInfoVideo.setVisibilityCenterView(View.GONE);
    }

    public void updateTitle(String title){
        mViewletPeerInfoAudio.updateSubTitle(title);
        mViewletPeerInfoVideo.updateSubTitle(title);
    }
    public void updateSubtitle(String title){
        mViewletPeerInfoAudio.updateSubTitle(title);
        mViewletPeerInfoVideo.updateSubTitle(title);
    }
}