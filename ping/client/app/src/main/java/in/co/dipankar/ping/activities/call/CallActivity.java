package in.co.dipankar.ping.activities.call;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.webrtc.SessionDescription;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.Utils;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.activities.call.subviews.CallEndedPageView;
import in.co.dipankar.ping.activities.call.subviews.CallIncomingPageView;
import in.co.dipankar.ping.activities.call.subviews.CallLandingPageView;
import in.co.dipankar.ping.activities.call.subviews.CallOngoingPageView;
import in.co.dipankar.ping.activities.call.subviews.CallOutgoingPageView;
import in.co.dipankar.ping.activities.call.subviews.CallVideoGridView;
import in.co.dipankar.ping.activities.home.HomePresenter;
import in.co.dipankar.ping.common.model.CallInfo;
import in.co.dipankar.ping.common.model.IContactManager;
import in.co.dipankar.ping.common.utils.AudioManagerUtils;
import in.co.dipankar.ping.common.webrtc.RtcDeviceInfo;
import in.co.dipankar.ping.common.webrtc.RtcStatView;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.ICallPage;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.CustomFontTextView;

import static in.co.dipankar.ping.contracts.ICallPage.PageViewType.INCOMMING;
import static in.co.dipankar.ping.contracts.ICallPage.PageViewType.LANDING;
import static in.co.dipankar.ping.contracts.ICallPage.PageViewType.OUTGOING;

public class CallActivity extends Activity implements ICallPage.IView{

    private boolean VERBOSE = true;
    private final  String TAG ="DIAPNAKR";

    //Views
    RelativeLayout mRootView;
    CallLandingPageView mCallLandingPageView;
    CallIncomingPageView mCallIncomingPageView;
    CallOngoingPageView mCallOngoingPageView;
    CallOutgoingPageView mCallOutgoingPageView;
    CallEndedPageView mCallEndedPageView;

    CallVideoGridView mCallVideoGridView;
    CustomFontTextView mNotificationView;
    RtcStatView mRtcStatView;
    //Presneter
    ICallPage.IPresenter mPresenter;

    // Andpid Utils
    MediaPlayer mMediaPlayer;
    AudioManagerUtils  mAudioManagerUtils;



    //private state
    private boolean mIsCameraOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        proceedAfterPermission();
    }

    private void proceedAfterPermission(){
        initView();
        initCall();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        proceedAfterPermission();
    }

    private void initCall(){
        Intent intent = getIntent();
        boolean isComing = intent.getBooleanExtra("isComing",false);
        IRtcUser peer = (IRtcUser) intent.getSerializableExtra("peer");
        PingApplication.Get().setPeer(peer);

        boolean isVideo = intent.getBooleanExtra("isVideo",false);
        PingApplication.Get().getUserManager().addCallback(mContactMangerCallback);

        mPresenter = new CallPresenter(this, peer, isVideo , mCallVideoGridView);
        if(!isComing) {
            mPresenter.startOutgoingCall();
        } else{
            String callId = intent.getStringExtra("callId");
            String sdp_data = intent.getStringExtra("sdp_data");
            String sdp_type = intent.getStringExtra("sdp_type");
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.valueOf(sdp_type),sdp_data);
            mPresenter.startIncommingCall(callId, sdp);
        }
    }

    private void initView() {
        mRootView = findViewById(R.id.root_view);
        mCallLandingPageView = findViewById(R.id.call_landing_page_view);
        mCallLandingPageView.setCallback(mCallLandingPageViewCallBack);

        mCallIncomingPageView = findViewById(R.id.call_incoming_page_view);
        mCallIncomingPageView.setCallback(mCallIncomingPageViewCallBack);


        mCallOngoingPageView = findViewById(R.id.call_ongoing_page_view);
        mCallOngoingPageView.setCallback(mCallOngoingPageViewCallBack);

        mCallOutgoingPageView = findViewById(R.id.call_outgoing_page_view);
        mCallOutgoingPageView.setCallback(mCallOutgoingPageViewCallBack);

        mCallEndedPageView = findViewById(R.id.call_ended_page_view);
        mCallEndedPageView.setCallback(mCallEndedPageViewCallBack);

        mCallVideoGridView = findViewById(R.id.call_video_grid_view);
        mCallVideoGridView.setCallback(mCallVideoGridViewCallBack);

        mCallLandingPageView.setVisibility(View.VISIBLE);

        mMediaPlayer = MediaPlayer.create(this, R.raw.tone);

        mNotificationView = findViewById(R.id.notification);
        mRtcStatView = findViewById(R.id.rtc_stat_view);

        // test
        Button test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToView(ICallPage.PageViewType.ONGOING);
            }
        });

        //update
        mAudioManagerUtils = new AudioManagerUtils(this, new AudioManagerUtils.Callback(){
            @Override
            public void onSpeakerOn() {}
            @Override
            public void onSpeakerOff() {}
        });
    }

    private CallLandingPageView.Callback mCallLandingPageViewCallBack= new CallLandingPageView.Callback(){
        @Override
        public void onClickAudioCallBtn(IRtcUser user) {
            //mPresenter.startAudio(user);
            //mCallOutgoingPageView.setVisibilityOfPeerInfo(true);
        }
        @Override
        public void onClickVideoCallBtn(IRtcUser user) {
         // mPresenter.startVideo(user);
         // mCallOutgoingPageView.setVisibilityOfPeerInfo(false);
        }

        @Override
        public void onClickPokeBtn(IRtcUser user) {
            //todo
        }
    };

    private CallIncomingPageView.Callback mCallIncomingPageViewCallBack= new CallIncomingPageView.Callback(){
        @Override
        public void onAccept() {
            mPresenter.acceptCall();
        }
        @Override
        public void onReject() {
            mPresenter.rejectCall();
        }
    };


    private CallOngoingPageView.Callback mCallOngoingPageViewCallBack= new CallOngoingPageView.Callback(){
        @Override
        public void onClickEnd() {
            mPresenter.endCall();
        }
        @Override
        public void onClickToggleVideo(boolean isOn) {
            mPresenter.toggleVideo(isOn);
        }

        @Override
        public void onClickToggleAudio(boolean isOn) {
            mPresenter.toggleAudio(isOn);
        }

        @Override
        public void onClickToggleCamera(boolean isOn) {
            mPresenter.toggleCamera(isOn);
        }

        @Override
        public void onClickToggleSpeaker(boolean isOn) {
            mAudioManagerUtils.setEnableSpeaker(isOn);
        }

        @Override
        public void onClickToggleLayout(int idx) {
            switch(idx){
                case 0:
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN);
                    break;
                case 1:
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);
                    break;
                case 3:
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.SPLIT_VIEW);
                    break;
            }

        }
    };

    private CallOutgoingPageView.Callback mCallOutgoingPageViewCallBack= new CallOutgoingPageView.Callback(){

        @Override
        public void onClickedEnd() {
            mPresenter.endCall();
        }

        @Override
        public void onClickedToggleVideo(boolean isOn) {
            mPresenter.toggleVideo(isOn);
        }

        @Override
        public void onClickedToggleAudio(boolean isOn) {
            mPresenter.toggleAudio(isOn);
        }

        @Override
        public void onClickedToggleCamera(boolean isOn) {
            mPresenter.toggleCamera(isOn);
        }

        @Override
        public void onClickedToggleSpeaker(boolean isOn) {
            mAudioManagerUtils.setEnableSpeaker(isOn);
        }
    };
    private CallEndedPageView.Callback mCallEndedPageViewCallBack= new CallEndedPageView.Callback(){

        @Override
        public void onClickClose() {
            finish();
        }

        @Override
        public void onClickRedail() {
        }
    };
    private CallVideoGridView.Callback mCallVideoGridViewCallBack = new CallVideoGridView.Callback(){

        @Override
        public void onClickedEnd() {

        }

        @Override
        public void onClickedToggleVideo(boolean isOn) {

        }

        @Override
        public void onClickedToggleAudio(boolean isOn) {

        }
    };


    @Override
    public void onStart() {
        super.onStart();
        if (VERBOSE) Log.v(TAG, "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VERBOSE) Log.v(TAG, "+ ON RESUME +");
    }


    @Override
    public void onStop() {
        super.onStop();
        if (VERBOSE) Log.v(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (VERBOSE) Log.v(TAG, "- ON DESTROY -");
    }
    @Override
    public void finish(){
        super.finish();
        if(mPresenter != null) {
            mPresenter.finish();
        }
        overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
        mCallEndedPageView = null;
        mPresenter = null;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
                RuntimePermissionUtils.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void hideAll(){
        mCallLandingPageView.requestFocus();
        mCallLandingPageView.setVisibility(View.GONE);
        mCallOutgoingPageView.setVisibility(View.INVISIBLE);
        mCallIncomingPageView.setVisibility(View.INVISIBLE);
        mCallOngoingPageView.setVisibility(View.INVISIBLE);
        mCallEndedPageView.setVisibility(View.INVISIBLE);
    }
    //all override
    @Override
    public void switchToView(ICallPage.PageViewType pageViewType) {
        DLog.e("Swicth To View: "+pageViewType);
        Toast.makeText(getApplicationContext(), "Swicth To View: "+pageViewType, Toast.LENGTH_SHORT).show();
        hideAll();
        switch(pageViewType){
            case LANDING:
                mCallLandingPageView.setVisibility(View.VISIBLE);
                mCallVideoGridView.setVisibility(View.GONE);
                break;
            case OUTGOING:
                mCallOutgoingPageView.setVisibility(View.VISIBLE);
                if(mIsCameraOpen) {
                    mCallVideoGridView.setVisibility(View.VISIBLE);
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN);
                } else {
                    mCallVideoGridView.setVisibility(View.GONE);
                }
                break;
            case INCOMMING:
                mCallIncomingPageView.setVisibility(View.VISIBLE);
                mCallIncomingPageView.requestFocus();
                if(mIsCameraOpen) {
                    mCallVideoGridView.setVisibility(View.VISIBLE);
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);//_VIEW_FULL_SCREEN);
                } else  {
                    mCallVideoGridView.setVisibility(View.GONE);
                }
                break;
            case ONGOING:
                mCallOngoingPageView.setVisibility(View.VISIBLE);
                if(mIsCameraOpen) {
                    mCallVideoGridView.setVisibility(View.VISIBLE);
                    mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);
                } else {
                    mCallVideoGridView.setVisibility(View.GONE);
                }
                mRtcStatView.reset();
                break;
            case ENDED:
                mCallEndedPageView.setVisibility(View.VISIBLE);
                mCallVideoGridView.setVisibility(View.GONE);
                break;
        }
        if(pageViewType == OUTGOING|| pageViewType == INCOMMING){
            mMediaPlayer.start();
        }
        else{
            if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void showNetworkNotification(String type, String s) {
        if(type.equals("success")){
            mNotificationView.setBackgroundResource(R.color.Notification_Success);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mNotificationView.setVisibility(View.GONE);
                }
            }, 5000);
        } else  if(type.equals("error")){
            mNotificationView.setBackgroundResource(R.color.Notification_Error);
        } else{
            mNotificationView.setBackgroundResource(R.color.Notification_Progress);
        }
        mNotificationView.setText(s);
        mNotificationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateEndView(String title, String subtitle) {
        mCallEndedPageView.updateView(title, subtitle);
    }

    @Override
    public void updateOutgoingView(String subtitle, boolean isAudio) {
        mCallOutgoingPageView.updateView(subtitle, isAudio);
    }

    @Override
    public void updateIncomingView(String subtitle) {
        mCallIncomingPageView.updateView(subtitle);
    }

    @Override
    public void onCameraOff() {
        DLog.e("onCameraOff");
        mCallVideoGridView.setVisibility(View.GONE);
        mIsCameraOpen = false;
    }

    @Override
    public void onCameraOn() {
        DLog.e("onCameraOn");
        mCallVideoGridView.setVisibility(View.VISIBLE);
        mIsCameraOpen = true;
    }

    @Override
    public void onRtcStat(Map<String, String> reports) {
        DLog.e(reports.toString());
    }

    @Override
    public void toggleViewBasedOnVideoEnabled(boolean isVideoEnabled) {
        if(isVideoEnabled){
            mCallVideoGridView.setVisibility(View.GONE);
        } else{
            mCallVideoGridView.setVisibility(View.VISIBLE);
        }
    }

    private IContactManager.Callback mContactMangerCallback= new IContactManager.Callback(){
        @Override
        public void onContactListChange(List<IRtcUser> userList) {
            mCallLandingPageView.updateUserList(userList);
        }
        @Override
        public void onSingleContactChange(IRtcUser user) {

        }
        @Override
        public void onPresenceChange(IRtcUser user, boolean isOnline) {

        }

        @Override
        public void onCallListChange(List<ICallInfo> mCallInfo) {

        }
    };
}
