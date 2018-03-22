package in.co.dipankar.ping.activities.callscreen;

import android.content.Context;
import android.util.Log;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.common.webrtc.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallPage;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IMultiVideoPane;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcEngine;
import in.co.dipankar.ping.contracts.IRtcUser;

public class CallPresenter implements ICallPage.IPresenter {

    //view
    private ICallPage.IView mView;

    // Utils
    String mCallId;
    IRtcUser mRtcUser;
    IRtcUser mPeerRtcUser;
    IRtcDeviceInfo mRtcDeviceInfo;
    IMultiVideoPane mMultiVideoPane;

    //rtc engine
    IRtcEngine mRtcEngine;
    //singnaling api
    ICallSignalingApi mSignalingApi;

    public CallPresenter(ICallPage.IView view, IRtcUser rtcUser, IRtcDeviceInfo rtcDeviceInfo, IMultiVideoPane multiVideoPane){
        mView = view;
        mRtcUser = rtcUser;
        mRtcDeviceInfo =rtcDeviceInfo;
        mMultiVideoPane = multiVideoPane;
        init();
    }

    private void  init(){
        mSignalingApi = new SocketIOSignaling(mRtcUser, mRtcDeviceInfo, mSignalingCallback);

        //RTC
        mRtcEngine = new WebRtcEngine2((Context)mView,mRtcUser, mSignalingApi, mMultiVideoPane.getSelfView(),mMultiVideoPane.getPeerView());
        if(mRtcEngine != null) {
            mRtcEngine.setCallback(rtcCallback);
        }
    }
    // Callbacks ..
    private ICallSignalingApi.ICallSignalingCallback  mSignalingCallback = new ICallSignalingApi.ICallSignalingCallback(){
        @Override
        public void onTryConnecting() {
            mView.showNetworkNotification("process","Try connecting...");
        }

        @Override
        public void onConnected() {
            mView.showNetworkNotification("success","Now connected...");
        }

        @Override
        public void onDisconnected() {
            mView.showNetworkNotification("error","Not able to connect network.");
        }

        @Override
        public void onReceivedOffer(String callid, SessionDescription sdp, IRtcUser user) {
            mCallId = callid;
            if(mRtcEngine != null) {
                mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            }
            PingApplication.Get().setPeer(user);
            mView.updateIncomingView(user.getUserName() +" calling...");
            mView.switchToView(ICallPage.PageViewType.INCOMMING);
        }

        @Override
        public void onReceivedAnswer(String callid, SessionDescription sdp) {
            if(mRtcEngine != null) {
                mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            }
            mView.switchToView(ICallPage.PageViewType.ONGOING);
        }

        @Override
        public void onReceivedCandidate(String callid, IceCandidate ice) {
            if(mRtcEngine != null) {
                mRtcEngine.addIceCandidateToPeerConnection(ice);
            }
        }

        @Override
        public void onReceivedEndCall(String callid,ICallSignalingApi.EndCallType type, String reason) {
            mView.updateEndView(type.toString().toUpperCase(), reason);
            mView.switchToView(ICallPage.PageViewType.ENDED);
        }
    };

    private IRtcEngine.Callback rtcCallback = new IRtcEngine.Callback(){

        @Override
        public void onSendOffer() {
            mView.switchToView(ICallPage.PageViewType.OUTGOING);
        }

        @Override
        public void onSendAns() {
            mView.switchToView(ICallPage.PageViewType.ONGOING);
        }

        @Override
        public void onFailure(String s) {
            Log.d("DIPANKAR","Some Error:"+s);
        }
    };


    private String getRandomCallId(){
        return "1111";
    }

    @Override
    public void endCall(){
        if(mRtcEngine != null) {
            mRtcEngine.endCall();
        }
        mView.switchToView(ICallPage.PageViewType.ENDED);
    }

    @Override
    public void startAudio(IRtcUser peer){
        PingApplication.Get().setPeer(peer);
        mView.updateOutgoingView("Ringing ....");
        mView.switchToView(ICallPage.PageViewType.OUTGOING);
        mCallId = getRandomCallId();
        mPeerRtcUser = peer;
        if(mRtcEngine != null) {
            mRtcEngine.startAudioCall(mCallId, mPeerRtcUser.getUserId());
        }

    }
    @Override
    public void startVideo(IRtcUser peer){
        PingApplication.Get().setPeer(peer);
        mView.updateOutgoingView("Ringing ....");
        mView.switchToView(ICallPage.PageViewType.OUTGOING);
        mCallId = getRandomCallId();
        mPeerRtcUser = peer;
        if(mRtcEngine != null) {
            mRtcEngine.startVideoCall(mCallId, mPeerRtcUser.getUserId());
        }

    }

    @Override
    public void acceptCall(){
        mView.switchToView(ICallPage.PageViewType.ONGOING);
        if(mRtcEngine != null) {
            mRtcEngine.acceptCall(mCallId);
        }
    }

    @Override
    public void rejectCall(){
        mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.PEER_REJECT," User reject a call");
        if(mRtcEngine != null) {
            mRtcEngine.rejectCall(mCallId);
        }
        mView.switchToView(ICallPage.PageViewType.ENDED);
    }

    @Override
    public void toggleVideo(boolean isOn) {
        if(mRtcEngine != null) {
            mRtcEngine.toggleVideo(isOn);
        }
    }

    @Override
    public void toggleCamera(boolean isOn) {
        if(mRtcEngine != null) {
            mRtcEngine.toggleCamera(isOn);
        }
    }

    @Override
    public void toggleAudio(boolean isOn) {
        if(mRtcEngine != null) {
            mRtcEngine.toggleAudio(isOn);
        }
    }

    @Override
    public void finish() {
        mSignalingApi.disconnect();
    }
}
