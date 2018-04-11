package in.peerreview.ping.activities.home;

import android.annotation.SuppressLint;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;

import java.util.List;
import org.webrtc.SessionDescription;

/** Created by dip on 3/30/18. */
public class IHome {
  public interface View {
    @SuppressLint("ResourceAsColor")
    void showNetworkNotification(String type, String s);

    void updateQuickUserView(List<IRtcUser> userList);

    void updateRecentCallView(List<ICallInfo> mCallInfo);

    void navigateToInComingCallView(
        String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled);
  }

  public interface Presenter {

    void finish();

    void requestSdp(String pending_call_id);
  }
}
