package in.co.dipankar.ping.activities;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.common.signaling.WebRtcEngine;
import in.co.dipankar.ping.common.signaling.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcEngine;

/**
 * Created by dip on 3/8/18.
 */

public class CallActivity extends Activity {
    IRtcEngine mRtcEngine;
    ICallSignalingApi mSignalingApi;

    //Views
    CallLandingPageView mCallLandingPageView;
    CallIncommingPageView mCallIncommingPageView;
    CallOngoingPageView mCallOngoingPageView;
    CallOutgoingPageView mCallOutgoingPageView;
    CallEndedPageView mCallEndedPageView;
    CallVideoGridView mCallVideoGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        initView();
       initLibs();
    }

    private void initView() {
        mCallLandingPageView = findViewById(R.id.call_landing_page_view);
        mCallLandingPageView.setCallback(mCallLandingPageViewCallBack);

        mCallIncommingPageView = findViewById(R.id.call_incoming_page_view);
        mCallIncommingPageView.setCallback(mCallIncommingPageViewCallBack);


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
        mSignalingApi = new SocketIOSignaling();
        mSignalingApi.addCallback(callback);
        mRtcEngine = new WebRtcEngine2(this,mSignalingApi, mCallVideoGridView.getSelfVideoView(),
                mCallVideoGridView.getPeerVideoView());
        mRtcEngine.setCallback(rtcCallback);
        mSignalingApi.connect();
    }

    //private helper
    void hideAll(){
        mCallLandingPageView.setVisibility(View.GONE);
        mCallOutgoingPageView.hide();
        mCallIncommingPageView.setVisibility(View.GONE);
        mCallOngoingPageView.setVisibility(View.GONE);
        mCallEndedPageView.setVisibility(View.GONE);
    }

    // Callbacks ..
    private ICallSignalingApi.ICallSignalingCallback  callback = new ICallSignalingApi.ICallSignalingCallback(){
        @Override
        public void onReceivedOffer(SessionDescription sdp) {
            mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            hideAll();
            mCallIncommingPageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedAnswer(SessionDescription sdp) {
            mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            hideAll();
            mCallOngoingPageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedCandidate(IceCandidate ice) {
            mRtcEngine.addIceCandidateToPeerConnection(ice);
        }
    };

    private IRtcEngine.Callback rtcCallback = new IRtcEngine.Callback(){

        @Override
        public void onSendOffer() {
            hideAll();
            mCallOutgoingPageView.show();
        }

        @Override
        public void onSendAns() {
            hideAll();
            mCallOngoingPageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(String s) {
            Log.d("DIPANKAR","Some Error:"+s);
        }
    };

    private CallLandingPageView.Callback mCallLandingPageViewCallBack= new CallLandingPageView.Callback(){
        @Override
        public void onClickAudioCallBtn() {
            hideAll();
            mRtcEngine.startAudioCall("1");
            mCallOutgoingPageView.show();
        }
        @Override
        public void onClickVideoCallBtn() {
            hideAll();
            mRtcEngine.startVideoCall("1");
            mCallOutgoingPageView.show();
        }
    };

    private CallIncommingPageView.Callback mCallIncommingPageViewCallBack= new CallIncommingPageView.Callback(){
        @Override
        public void onAccept() {
            mRtcEngine.acceptCall();
        }
        @Override
        public void onReject() {
            mRtcEngine.rejectCall();
            hideAll();
        }
    };

    private CallOngoingPageView.Callback mCallOngoingPageViewCallBack= new CallOngoingPageView.Callback(){
        @Override
        public void onClickEnd() {
            mRtcEngine.endCall();
            hideAll();
            mCallEndedPageView.setVisibility(View.VISIBLE);
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
    };

    private CallOutgoingPageView.Callback mCallOutgoingPageViewCallBack= new CallOutgoingPageView.Callback(){

        @Override
        public void onClickedEnd() {
            mRtcEngine.endCall();
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
        public void onClickAudioCallBtn() {

        }
        @Override
        public void onClickVideoCallBtn() {

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
}
