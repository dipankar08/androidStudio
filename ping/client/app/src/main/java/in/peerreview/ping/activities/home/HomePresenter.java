package in.peerreview.ping.activities.home;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.setApplicationId;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.facebook.common.Common;
import com.google.gson.Gson;

import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;
import in.peerreview.ping.activities.Utils.CommonIntent;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.activities.bell.BellInfo;
import in.peerreview.ping.common.model.IContactManager;
import in.peerreview.ping.common.signaling.IDataMessage;
import in.peerreview.ping.contracts.Configuration;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.ICallSignalingApi;
import in.peerreview.ping.contracts.IRtcUser;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class HomePresenter implements IHome.Presenter {

  private IHome.View mView;
  @Nullable private ICallSignalingApi mCallSignalingApi;
  @Nullable private IContactManager mContactManager;
  private String mPendingCallId = null;

  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  HomePresenter(IHome.View view) {
    mView = view;
    initOnUIThread();
    initOnBackgroudThread();
  }

  private void initOnUIThread() {}

  private void initOnBackgroudThread() {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            // Init Model and Restore
            mContactManager = PingApplication.Get().getUserManager();
            mContactManager.addCallback(mContactManagerCallback);
            PingApplication.Get().getUserManager().restore();

            // Init Signals
            mCallSignalingApi = PingApplication.Get().getCallSignalingApi();
            mCallSignalingApi.addCallback(mCallSignalingCallback);
            mCallSignalingApi.connect();
            updateToken();
          }
        });
  }

  private void updateToken() {
    SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String token = preferences.getString("FIREBASE_TOKEN", "");
    if (token.length() > 0 && PingApplication.Get().getMe() != null) {
      HashMap<String, String> data =
          new HashMap<String, String>() {
            {
              put("user_id", PingApplication.Get().getMe().getUserId());
              put("token", token);
            }
          };

      PingApplication.Get()
          .getNetwork()
          .send(
              Configuration.SERVER_ENDPOINT + "/add_token",
              data,
              new Network.Callback() {
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

  private ICallSignalingApi.ICallSignalingCallback mCallSignalingCallback =
      new ICallSignalingApi.ICallSignalingCallback() {
        @Override
        public void onTryConnecting() {
          mView.showNetworkNotification("progress", "Connecting ...");
        }

        @Override
        public void onConnected() {
          mView.showNetworkNotification("success", "You are connected ...");
          PingApplication.Get().setNetworkConn(true);
          invokePendingCall();
        }

        @Override
        public void onDisconnected() {
          PingApplication.Get().setNetworkConn(false);
          mView.showNetworkNotification("error", "Not able to connect to internet.");
        }

        @Override
        public void onPresenceChange(IRtcUser user, ICallSignalingApi.PresenceType type) {
          boolean isOnline = type == ICallSignalingApi.PresenceType.ONLINE ? true : false;
          mContactManager.changeOnlineState(user, isOnline);
        }

        @Override
        public void onWelcome(List<IRtcUser> liveUserList) {
          mContactManager.changeOnlineState(liveUserList, true);
        }

          @Override
          public void onDataMessage(IDataMessage dataMessage) {
              switch (dataMessage.getMessageType()){
                  case ACK:
                      break;
                  case BELL:
                      mView.startBellActivity("incoming",dataMessage.getRawData());
                      break;
                  case BELL_ACK:
                      mView.startBellActivity("outgoing",dataMessage.getRawData());
                      break;

              }
          }

          // RTC - Nothing to do here.
        @Override
        public void onReceivedOffer(
            String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled) {
          if (PingApplication.Get().getCurrentCallInfo() != null) {
            // TODO : Handling Conflicting calls. We recv a call when I am in another call.
          } else {
            mView.navigateToInComingCallView(callId, sdp, rtcUser, isVideoEnabled);
          }
        }

        @Override
        public void onReceivedAnswer(String callId, SessionDescription sdp) {}

        @Override
        public void onReceivedCandidate(String callId, IceCandidate ice) {}

        @Override
        public void onReceivedEndCall(
            String callID, ICallSignalingApi.EndCallType type, String reason) {}
      };

  IContactManager.Callback mContactManagerCallback =
      new IContactManager.Callback() {
        @Override
        public void onContactListChange(List<IRtcUser> userList) {
          mView.updateQuickUserView(userList);
        }

        @Override
        public void onSingleContactChange(IRtcUser user) {}

        @Override
        public void onPresenceChange(IRtcUser user, boolean isOnline) {}

        @Override
        public void onCallListChange(List<ICallInfo> mCallInfo) {
          mView.updateRecentCallView(mCallInfo);
        }
      };

  @Override
  public void finish() {
    mCallSignalingApi.removeCallback(mCallSignalingCallback);
    mContactManager.removeCallback(mContactManagerCallback);
    mCallSignalingApi.disconnect();
    mContactManager = null;
    mCallSignalingApi = null;
  }

  @Override
  public void requestSdp(String pending_call_id) {
    mPendingCallId = pending_call_id;
  }

    @Override
    public void sendBellInfo(BellInfo bellInfo) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                sendBellInfoInternal(bellInfo);
            }
        });
    }

    private void sendBellInfoInternal(BellInfo bellInfo) {
      if(mCallSignalingApi != null){
          mCallSignalingApi.sendMessage(bellInfo);
      }
    }

    public void invokePendingCall() {
    if (mPendingCallId != null) {
      mCallSignalingApi.resendOffer(PingApplication.Get().getMe().getUserId(), mPendingCallId);
      mPendingCallId = null;
    }
  }
}
