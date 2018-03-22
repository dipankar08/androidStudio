package in.co.dipankar.ping.activities.callscreen;

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

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.activities.callscreen.subviews.CallEndedPageView;
import in.co.dipankar.ping.activities.callscreen.subviews.CallIncomingPageView;
import in.co.dipankar.ping.activities.callscreen.subviews.CallLandingPageView;
import in.co.dipankar.ping.activities.callscreen.subviews.CallOngoingPageView;
import in.co.dipankar.ping.activities.callscreen.subviews.CallOutgoingPageView;
import in.co.dipankar.ping.activities.callscreen.subviews.CallVideoGridView;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.common.webrtc.RtcDeviceInfo;
import in.co.dipankar.ping.common.webrtc.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallPage;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcEngine;
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

    //Presneter
    ICallPage.IPresenter mPresenter;

    // Andpid Utisl
    ICallPage.PageViewType mPageViewType;
    MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);

        RuntimePermissionUtils.getInstance().init(this);
        RuntimePermissionUtils.getInstance().askPermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO

        }, new RuntimePermissionUtils.CallBack() {
            @Override
            public void onSuccess() {
                proceedAfterPermission();
            }

            @Override
            public void onFail() {
                finish();
            }
        });
    }

    private void proceedAfterPermission(){
        // this order is importnat
        processIntent();
        initView();
        initPresenter();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        proceedAfterPermission();
    }

    private void processIntent(){
        Intent intent = getIntent();
        IRtcUser mRtcUser = (IRtcUser) intent.getSerializableExtra("RtcUser");
        String deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        IRtcDeviceInfo mRtcDeviceInfo= new RtcDeviceInfo(deviceid,android.os.Build.MODEL,"10");
        PingApplication.Get().setMe(mRtcUser);
        PingApplication.Get().setDevice(mRtcDeviceInfo);
    }

    private void initPresenter(){
        mPresenter = new CallPresenter(this, PingApplication.Get().getMe(), PingApplication.Get().getDevice(), mCallVideoGridView);
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

        // test
        Button test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToView(ICallPage.PageViewType.ONGOING);
            }
        });
        mCallLandingPageView.updateView();
    }

    private CallLandingPageView.Callback mCallLandingPageViewCallBack= new CallLandingPageView.Callback(){
        @Override
        public void onClickAudioCallBtn(IRtcUser user) {

            mPresenter.startAudio(user);
        }
        @Override
        public void onClickVideoCallBtn(IRtcUser user) {
          mPresenter.startVideo(user);
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
    };
    private CallEndedPageView.Callback mCallEndedPageViewCallBack= new CallEndedPageView.Callback(){

        @Override
        public void onClickClose() {
            switchToView(LANDING);
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
    public void onPause() {
        super.onPause();
        if (VERBOSE) Log.v(TAG, "- ON PAUSE -");
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
                break;
            case INCOMMING:
                mCallIncomingPageView.setVisibility(View.VISIBLE);
                mCallIncomingPageView.requestFocus();
                //mCallIncomingPageView.invalidate();
                break;
            case ONGOING:
                mCallOngoingPageView.setVisibility(View.VISIBLE);
                break;
            case ENDED:
                mCallEndedPageView.setVisibility(View.VISIBLE);
                break;
            case OUTGOING:
                mCallOutgoingPageView.setVisibility(View.VISIBLE);
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
        } else  if(type.equals("error")){
            mNotificationView.setBackgroundResource(R.color.Notification_Error);
        } else{
            mNotificationView.setBackgroundResource(R.color.Notification_Progress);
        }
        mNotificationView.setText(s);
        mNotificationView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mNotificationView.setVisibility(View.GONE);
            }
        }, 15000);
    }

    @Override
    public void updateEndView(String title, String subtitle) {
        mCallEndedPageView.updateView(title, subtitle);
    }

    @Override
    public void updateOutgoingView(String subtitle) {
        mCallOutgoingPageView.updateView(subtitle);
    }

    @Override
    public void updateIncomingView(String subtitle) {
        mCallIncomingPageView.updateView(subtitle);
    }

}
