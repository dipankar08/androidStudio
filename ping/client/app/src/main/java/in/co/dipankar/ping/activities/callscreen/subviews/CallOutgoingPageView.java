package in.co.dipankar.ping.activities.callscreen.subviews;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;
import in.co.dipankar.quickandorid.views.StateImageButton;


public class CallOutgoingPageView extends RelativeLayout{

    public interface Callback {
        void onClickedEnd();
        void onClickedToggleVideo(boolean isOn);
        void onClickedToggleAudio(boolean isOn);
        void onClickedToggleCamera(boolean isOn);
        void onClickedToggleSpeaker(boolean isOn);
    }

    private Callback mCallback;
    LayoutInflater mInflater;


    UserInfoView mPeerInfo;

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public CallOutgoingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallOutgoingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallOutgoingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_outgoing_page, this, true);

        mPeerInfo =  v.findViewById(R.id.peer_info);

        StateImageButton video =  v.findViewById(R.id.toggle_video);
        StateImageButton audio =  v.findViewById(R.id.toggle_video);
        StateImageButton camera =  v.findViewById(R.id.toggle_camera);
        StateImageButton speaker =  v.findViewById(R.id.toggle_speaker);
        StateImageButton end = v.findViewById(R.id.end);

        audio.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickedToggleAudio(b);
            }
        });
        video.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickedToggleVideo(b);
            }
        });
        end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickedEnd();
            }
        });

        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickedToggleCamera(!camera.isViewEnabled());
            }
        });

        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickedToggleSpeaker(!speaker.isViewEnabled());
            }
        });
    }

    public void updateView(String subtitle, boolean isAudiocalll) {
        IRtcUser peer = PingApplication.Get().getPeer();
        if(peer == null) return;
        mPeerInfo.updateView(peer);
        mPeerInfo.mTitle.setText("Calling "+ peer.getUserName() + " ...");
        mPeerInfo.mSubTitle.setText("Ringing ...");
        if(isAudiocalll){
            mPeerInfo.mPeerBackgroud.setVisibility(INVISIBLE);
        } else{
            mPeerInfo.mPeerBackgroud.setVisibility(VISIBLE);
        }
    }

    public void setVisibilityOfPeerInfo(boolean v){
        mPeerInfo.setVisibility(v?VISIBLE:GONE);
    }
}