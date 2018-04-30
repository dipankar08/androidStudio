package in.peerreview.ping.activities.pushnotification;

import static in.peerreview.ping.contracts.Preferences.mForceBellRequest;
import static in.peerreview.ping.contracts.Preferences.mForceIncomingCall;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.ping.activities.Utils.CommonIntent;
import in.peerreview.ping.activities.application.PingApplication;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    DLog.e("Remote message Received" + remoteMessage.toString());
    String type = remoteMessage.getData().get("type");
    switch (type) {
      case "call_request":
        String msg = remoteMessage.getData().get("msg");
        String call_id = remoteMessage.getData().get("call_id");
        handleCallRequest(msg, call_id);
        break;
      case "bell_request":
        String data = remoteMessage.getData().get("data");
        handleBellRequest(data);
        break;
    }
  }

  private void handleBellRequest(String data) {
    if (mForceBellRequest) {
      CommonIntent.startBellActivity(this, "incoming", data);
    } else {
      CommonIntent.startBellActivity(this, "incoming", data);
      // PingApplication.Get().getNotificationBuilder().showIncommingCallNotification( "Incomming
      // Daga", call_id);
    }
  }

  private void handleCallRequest(String msg, String call_id) {
    if (mForceIncomingCall) {
      CommonIntent.startHomeActivity(this, null, call_id);
    } else {
      PingApplication.Get().getNotificationBuilder().showIncommingCallNotification(msg, call_id);
    }
  }
}
