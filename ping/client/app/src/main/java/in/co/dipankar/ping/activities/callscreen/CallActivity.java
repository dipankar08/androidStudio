package in.co.dipankar.ping.activities.callscreen;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.common.webrtc.RtcDeviceInfo;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.common.webrtc.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcEngine;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.DLog;

public class CallActivity extends Activity {
    private enum CallState{
        FREE,
        OUTGOING_CALL,
        INCOMMING_CALL,
        ONCALL
    };
    IRtcEngine mRtcEngine;
    ICallSignalingApi mSignalingApi;
    private boolean VERBOSE = true;
    private final  String TAG ="DIAPNAKR";

    //Views
    CallLandingPageView mCallLandingPageView;
    CallIncomingPageView mCallIncomingPageView;
    CallOngoingPageView mCallOngoingPageView;
    CallOutgoingPageView mCallOutgoingPageView;
    CallEndedPageView mCallEndedPageView;
    CallVideoGridView mCallVideoGridView;

    // Utils
    MediaPlayer mMediaPlayer;
    CallState mCallState;
    String mCallId;
    IRtcUser mRtcUser;
    IRtcDeviceInfo mRtcDeviceInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);

        processIntent();
        initView();
        initLibs();
    }

    private void processIntent(){
        //Build this from intent.
        Intent intent = getIntent();
        mRtcUser = new RtcUser(intent.getStringExtra("name"),intent.getStringExtra("id"),"123","xxx");
        mRtcDeviceInfo= new RtcDeviceInfo("10","Pixel","10");
    }

    private void initView() {
        mCallLandingPageView = findViewById(R.id.call_landing_page_view);
        mCallLandingPageView.setData(mRtcUser);
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

    }

    private void initLibs() {
        DLog.d("init Libs....");



        // Singlaing info
        mSignalingApi = new SocketIOSignaling();
        mSignalingApi.addCallback(callback);
        mSignalingApi.connect();
        mSignalingApi.sendRegister(mRtcUser,mRtcDeviceInfo);

        //RTC
        mRtcEngine = new WebRtcEngine2(this,mRtcUser, mSignalingApi, mCallVideoGridView.getSelfVideoView(),
                mCallVideoGridView.getPeerVideoView());
        mRtcEngine.setCallback(rtcCallback);

        //Mediaplayer
        mMediaPlayer = MediaPlayer.create(this, R.raw.tone);
        mCallState = CallState.FREE;
    }

    //private helper
    void switchToView(View view) {
        mCallLandingPageView.setVisibility(View.GONE);
        mCallOutgoingPageView.hide();
        mCallIncomingPageView.setVisibility(View.GONE);
        mCallOngoingPageView.setVisibility(View.GONE);
        mCallEndedPageView.setVisibility(View.GONE);
        if(view != null) {
            view.setVisibility(View.VISIBLE);
        }
        String viewName = view.getClass().getSimpleName();
        if(viewName.equals("CallOutgoingPageView") || viewName.equals("CallIncomingPageView")){
            mMediaPlayer.start();
        }
        else{
            if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
        }
        // update the state
        switch(view.getClass().getSimpleName()){
            case "CallOutgoingPageView":
                mCallState = CallState.OUTGOING_CALL;
                break;
            case "CallIncomingPageView":
                mCallState = CallState.INCOMMING_CALL;
                break;
            case "OngoingPageView":
                mCallState = CallState.OUTGOING_CALL;
                break;
            case "CallEndedPageView":
                mCallState = CallState.FREE;
                break;
            case "CallLandingPageView":
                mCallState = CallState.FREE;
                break;
        }
    }

    // Callbacks ..
    private ICallSignalingApi.ICallSignalingCallback  callback = new ICallSignalingApi.ICallSignalingCallback(){
        @Override
        public void onReceivedOffer(String callid, SessionDescription sdp, IRtcUser user) {
            mCallId = callid;
            if(mCallState == CallState.FREE) {
                mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
                switchToView(mCallIncomingPageView);
            } else{
                mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.BUSY ,"user is busy with:"+mCallState.toString());
            }
        }

        @Override
        public void onReceivedAnswer(String callid, SessionDescription sdp) {
            mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            switchToView(mCallOngoingPageView);
        }

        @Override
        public void onReceivedCandidate(String callid, IceCandidate ice) {
            mRtcEngine.addIceCandidateToPeerConnection(ice);
        }

        @Override
        public void onReceivedEndCall(String callid,ICallSignalingApi.EndCallType type, String reason) {
            mCallEndedPageView.setEndString(type,reason);
            switchToView(mCallEndedPageView);
        }
    };

    private IRtcEngine.Callback rtcCallback = new IRtcEngine.Callback(){

        @Override
        public void onSendOffer() {
            switchToView(mCallOutgoingPageView);
        }

        @Override
        public void onSendAns() {
            switchToView(mCallOngoingPageView);
        }

        @Override
        public void onFailure(String s) {
            Log.d("DIPANKAR","Some Error:"+s);
        }
    };

    private CallLandingPageView.Callback mCallLandingPageViewCallBack= new CallLandingPageView.Callback(){
        @Override
        public void onClickAudioCallBtn() {
            startAudioInternal();
        }
        @Override
        public void onClickVideoCallBtn() {
          startVideoInternal();
        }
    };

    private CallIncomingPageView.Callback mCallIncomingPageViewCallBack= new CallIncomingPageView.Callback(){
        @Override
        public void onAccept() {
           acceptCallInternal();
        }
        @Override
        public void onReject() {
            rejectCallInternal();
        }
    };


    private CallOngoingPageView.Callback mCallOngoingPageViewCallBack= new CallOngoingPageView.Callback(){
        @Override
        public void onClickEnd() {
            mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.REGULAR,"Normal ends");
            endCallInternal();
        }
        @Override
        public void onClickToggleVideo(boolean isOn) {
            mRtcEngine.toggleVideo(isOn);
        }

        @Override
        public void onClickToggleAudio(boolean isOn) {
            mRtcEngine.toggleAudio(isOn);
        }

        @Override
        public void onClickToggleCamera(boolean isOn) {
            mRtcEngine.toggleCamera(isOn);
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
            mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.OTHER,"Normal ends");
            endCallInternal();
        }

        @Override
        public void onClickedToggleVideo(boolean isOn) {
            mRtcEngine.toggleVideo(isOn);
        }

        @Override
        public void onClickedToggleAudio(boolean isOn) {
            mRtcEngine.toggleAudio(isOn);
        }
    };
    private CallEndedPageView.Callback mCallEndedPageViewCallBack= new CallEndedPageView.Callback(){

        @Override
        public void onClickClose() {
            finish();
        }

        @Override
        public void onClickRedail() {
            startVideoInternal();
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

    // Intrenal Calls.
    private void endCallInternal(){
        mRtcEngine.endCall();
        switchToView(mCallEndedPageView);
    }
    private void startAudioInternal(){
        switchToView(mCallOutgoingPageView);
        mRtcEngine.startAudioCall("1");
    }
    private  void startVideoInternal(){
        switchToView(mCallOutgoingPageView);
        mRtcEngine.startVideoCall("1");
    }
    private void acceptCallInternal(){
        switchToView(mCallOngoingPageView);
        mRtcEngine.acceptCall();
    }
    private void rejectCallInternal(){
        mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.USER_REJECT," User reject a call");
        mRtcEngine.rejectCall();
        switchToView(mCallEndedPageView);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (VERBOSE) Log.v(TAG, "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VERBOSE) Log.v(TAG, "+ ON RESUME +");
        mSignalingApi.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (VERBOSE) Log.v(TAG, "- ON PAUSE -");
        mSignalingApi.connect();
        mSignalingApi.sendRegister(mRtcUser,mRtcDeviceInfo);
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

}
