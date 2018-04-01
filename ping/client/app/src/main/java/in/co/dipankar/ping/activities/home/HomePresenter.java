package in.co.dipankar.ping.activities.home;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.List;

import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.model.IContactManager;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcUser;

public class HomePresenter implements IHome.Presenter {

    private IHome.View mView;
    private ICallSignalingApi mCallSignalingApi;
    private IContactManager mContactManager;

    HomePresenter(IHome.View view){
        mView = view;
        init();
    }

    private void init() {
        initSignal();
        initModel();
    }

    private void initModel() {
        mContactManager = PingApplication.Get().getUserManager();
        mContactManager.addCallback(mContactManagerCallback);
    }

    private void initSignal() {
        mCallSignalingApi = PingApplication.Get().getCallSignalingApi();
        mCallSignalingApi.addCallback(mCallSignalingCallback);
        mCallSignalingApi.connect();
    }

    private ICallSignalingApi.ICallSignalingCallback mCallSignalingCallback =  new ICallSignalingApi.ICallSignalingCallback() {
        @Override
        public void onTryConnecting() {
            mView.showNetworkNotification("progress","Connecting ...");
        }

        @Override
        public void onConnected() {
            mView.showNetworkNotification("success","You are connected ...");
            PingApplication.Get().setNetworkConn(true);
        }

        @Override
        public void onDisconnected() {
            PingApplication.Get().setNetworkConn(false);
            mView.showNetworkNotification("error","Not able to connect to internet.");
        }

        @Override
        public void onPresenceChange(IRtcUser user, ICallSignalingApi.PresenceType type) {
            boolean isOnline =  type == ICallSignalingApi.PresenceType.ONLINE ? true: false;
            mContactManager.changeOnlineState(user,isOnline);
        }

        @Override
        public void onWelcome(List<IRtcUser> liveUserList) {
            mContactManager.changeOnlineState(liveUserList, true);
        }

        //RTC - Nothing to do here.
        @Override
        public void onReceivedOffer(String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled) {
            if(PingApplication.Get().isOnCall()){
                //TODO : Handling Conflicting calls. We recv a call when I am in another call.
            } else {
                mView.navigateToInComingCallView(callId, sdp, rtcUser, isVideoEnabled);
            }
        }

        @Override
        public void onReceivedAnswer(String callId, SessionDescription sdp) {

        }

        @Override
        public void onReceivedCandidate(String callId, IceCandidate ice) {

        }

        @Override
        public void onReceivedEndCall(String callID, ICallSignalingApi.EndCallType type, String reason) {

        }
    };

    IContactManager.Callback mContactManagerCallback = new IContactManager.Callback() {
        @Override
        public void onContactListChange(List<IRtcUser> userList) {
            mView.updateQuickUserView(userList);
        }

        @Override
        public void onSingleContactChange(IRtcUser user) {

        }

        @Override
        public void onPresenceChange(IRtcUser user, boolean isOnline) {

        }

        @Override
        public void onCallListChange(List<ICallInfo> mCallInfo) {
            mView.updateRecentCallView(mCallInfo);
        }
    };

    @Override
    public void finish(){
        mCallSignalingApi.removeCallback(mCallSignalingCallback);
        mContactManager.removeCallback(mContactManagerCallback);
        mCallSignalingApi.disconnect();
        mContactManager = null;
        mCallSignalingApi = null;
    }
}
