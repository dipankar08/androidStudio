package in.co.dipankar.ping.activities.home;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.SessionDescription;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.model.IContactManager;
import in.co.dipankar.ping.contracts.Configuration;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomePresenter implements IHome.Presenter {

    private IHome.View mView;
    private ICallSignalingApi mCallSignalingApi;
    private IContactManager mContactManager;
    private  String mPendingCallId = null;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        updateToken();
    }

    private void updateToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString("FIREBASE_TOKEN","");
        if(token.length() > 0 && PingApplication.Get().getMe() != null){
            HashMap<String, String> data = new HashMap<String, String>() {{
                put("user_id",PingApplication.Get().getMe().getUserId());
                put("token",token);
            }};

            PingApplication.Get().getNetwork().send(Configuration.SERVER_ENDPOINT+"/add_token",data, new Network.Callback(){
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    DLog.e("Token sent to server");
                }
                @Override
                public void onError(String s) {
                    DLog.e("Token fail to sent to server");
                }
            });
        }
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
            invokePendingCall();
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

    @Override
    public void requestSdp(String pending_call_id) {
        mPendingCallId = pending_call_id;
        /*
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //requestSdpInternal(pending_call_id);
            }
        });
        */
    }

    public void invokePendingCall() {
        if (mPendingCallId != null) {
            mCallSignalingApi.resendOffer(PingApplication.Get().getMe().getUserId(), mPendingCallId);
            mPendingCallId = null;
        }
    }
}
