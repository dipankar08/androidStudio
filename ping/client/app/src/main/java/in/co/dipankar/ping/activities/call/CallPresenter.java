package in.co.dipankar.ping.activities.call;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.model.CallInfo;
import in.co.dipankar.ping.common.model.IContactManager;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.common.webrtc.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.ICallPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IMultiVideoPane;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcEngine;
import in.co.dipankar.ping.contracts.IRtcUser;

import static in.co.dipankar.ping.common.webrtc.Constant.AUDIO_CODEC_OPUS;

public class CallPresenter implements ICallPage.IPresenter {
    private ICallPage.IView mView;

    // Utils
    String mCallId;
    IRtcUser mPeerRtcUser;
    ICallInfo mCallInfo;
    IMultiVideoPane mMultiVideoPane;

    //rtc engine
    IRtcEngine mRtcEngine;
    //singnaling api
    ICallSignalingApi mSignalingApi;

    //Stat
    private int mDuration =0;
    private int mByteUse =0;

    public CallPresenter(ICallPage.IView view, IRtcUser peer, boolean isVideo, IMultiVideoPane multiVideoPane){
        mView = view;
        mPeerRtcUser = peer;
        mMultiVideoPane = multiVideoPane;
        init();
        this.mCallInfo = new CallInfo(getRandomCallId(),
                ICallInfo.CallType.OUTGOING_CALL,
                isVideo,
                PingApplication.Get().getMe().getUserId(),
                peer.getUserId(),
                "0",
                getTimeNow(),
                "0 Kb");
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String getTimeNow() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now).toString(); //2016/11/16 12:08:43
    }

    private void  init(){
        IRtcEngine.RtcConfiguration rtcConfiguration = new IRtcEngine.RtcConfiguration();
        rtcConfiguration.audioCodec = AUDIO_CODEC_OPUS;
        rtcConfiguration.audioStartBitrate  = 6;
        mSignalingApi = PingApplication.Get().getCallSignalingApi();
        //TODO - NOW THIS WILL BE CALLED MUTIPLE TIMES.
        mSignalingApi.addCallback(mSignalingCallback);
        mRtcEngine = new WebRtcEngine2((Context)mView, rtcConfiguration,mSignalingApi, mMultiVideoPane.getSelfView(),mMultiVideoPane.getPeerView());
        if(mRtcEngine != null) {
            mRtcEngine.setCallback(rtcCallback);
            mRtcEngine.enableStatsEvents(true, 1000);
        }
    }


    private ICallSignalingApi.ICallSignalingCallback  mSignalingCallback = new ICallSignalingApi.ICallSignalingCallback(){
        @Override
        public void onTryConnecting() {}

        @Override
        public void onConnected() {}

        @Override
        public void onDisconnected() {}

        @Override
        public void onPresenceChange(IRtcUser user, ICallSignalingApi.PresenceType type) {}

        @Override
        public void onWelcome(List<IRtcUser> liveUserList) {}

        @Override
        public void onReceivedOffer(String callid, SessionDescription sdp, IRtcUser user, boolean isVideoEnabled) {
            // This will taken care bt HomePresenter
            // TODO Incase of Conflict Call
        }

        @Override
        public void onReceivedAnswer(String callid, SessionDescription sdp) {
            if(mRtcEngine != null) {
                mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            }
            mView.switchToView(ICallPage.PageViewType.ONGOING);
            mCallInfo.setType(ICallInfo.CallType.OUTGOING_CALL);
        }

        @Override
        public void onReceivedCandidate(String callid, IceCandidate ice) {
            if(mRtcEngine != null) {
                mRtcEngine.addIceCandidateToPeerConnection(ice);
            }
        }

        @Override
        public void onReceivedEndCall(String callid,ICallSignalingApi.EndCallType type, String reason) {
            handleEndCallInternal(type,reason);
        }

    };

    private IRtcEngine.Callback rtcCallback = new IRtcEngine.Callback(){
        @Override
        public void onSendOffer() {
            mView.switchToView(ICallPage.PageViewType.OUTGOING);
            mCallInfo.setType(ICallInfo.CallType.MISS_CALL_OUTGOING);
        }

        @Override
        public void onSendAns() {
            mView.switchToView(ICallPage.PageViewType.ONGOING);
            mCallInfo.setType(ICallInfo.CallType.INCOMMING_CALL);
        }

        @Override
        public void onFailure(String s) {
            Log.d("DIPANKAR","Some Error:"+s);
        }

        @Override
        public void onCameraClose() {
            mView.onCameraOff();
        }
        @Override
        public void onCameraOpen() {
            mView.onCameraOn();
        }

        @Override
        public void onStat(Map<String, String> reports) {
            mView.onRtcStat(reports);
        }
    };

    @Override
    public void startOutgoingCall(){
        if(mCallInfo == null){
            return;
        }
        if(!PingApplication.Get().hasNetworkConn()){
            mView.showNetworkNotification("error","No network");
            finish();
            return;
        }
        mView.updateOutgoingView("Ringing ....", mCallInfo.getIsVideo());
        mView.switchToView(ICallPage.PageViewType.OUTGOING);
        if(mRtcEngine != null) {
            if(mCallInfo.getIsVideo()){
                mRtcEngine.startVideoCall(mCallInfo.getId(),mCallInfo.getTo());
            } else {
                mRtcEngine.startAudioCall(mCallInfo.getId(),mCallInfo.getTo());
            }
        }
    }

    @Override
    public void startIncommingCall(String callId, SessionDescription sdp) {
        if(mCallInfo == null){
            return;
        }
        if(!PingApplication.Get().hasNetworkConn()){
            mView.showNetworkNotification("error","No network");
            finish();
            return;
        }

        mCallId = callId;
        if(mRtcEngine != null) {
            mRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
            mRtcEngine.toggleVideo(mCallInfo.getIsVideo());
            mView.toggleViewBasedOnVideoEnabled(mCallInfo.getIsVideo());
        }

        mView.updateIncomingView(mPeerRtcUser.getUserName() +" calling...");
        mView.switchToView(ICallPage.PageViewType.INCOMMING);
        mCallInfo.setType(ICallInfo.CallType.MISS_CALL_INCOMMING);
    }

    @Override
    public void endCall(){
        handleEndCallInternal(ICallSignalingApi.EndCallType.NORMAL_END,"You ended the call");
    }
    @Override
    public void acceptCall(){
        assert(mRtcEngine != null);
        mRtcEngine.acceptCall(mCallId);
        mView.switchToView(ICallPage.PageViewType.ONGOING);
        mCallInfo.setType(ICallInfo.CallType.INCOMMING_CALL);
    }

    @Override
    public void rejectCall(){
        assert(mRtcEngine != null);
        mSignalingApi.sendEndCall(mCallId, ICallSignalingApi.EndCallType.PEER_REJECT," User reject a call");
        mRtcEngine.rejectCall(mCallId);
        handleEndCallInternal(ICallSignalingApi.EndCallType.PEER_REJECT," User reject a call" );
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
    public void toggleSpeaker(boolean isOn) {
       // Note to do.
    }

    @Override
    public void finish() {
        // Not to do anything here..
        mView = null;
    }

    private void handleEndCallInternal(ICallSignalingApi.EndCallType type, String reason) {
        mView.updateEndView(type.toString().toUpperCase(), reason);
        mView.switchToView(ICallPage.PageViewType.ENDED);
        if(mRtcEngine != null) {
            mRtcEngine.endCall();
        }
        IContactManager contactManager = PingApplication.Get().getUserManager();
        mCallInfo.setDataUses(mByteUse+"");
        mCallInfo.setDuration(mDuration+"");
        contactManager.addCallInfo(mCallInfo);
    }

    private String getRandomCallId(){
        return UUID.randomUUID().toString();
    }
}
