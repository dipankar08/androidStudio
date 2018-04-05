package in.co.dipankar.ping.activities.home;

import android.annotation.SuppressLint;

import org.webrtc.SessionDescription;

import java.util.List;

import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/30/18.
 */

public class IHome {
    public  interface View{
        @SuppressLint("ResourceAsColor")
        void showNetworkNotification(String type, String s);

        void updateQuickUserView(List<IRtcUser> userList);

        void updateRecentCallView(List<ICallInfo> mCallInfo);

        void navigateToInComingCallView(String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled);
    }
    public  interface Presenter{

        void finish();

        void requestSdp(String pending_call_id);
    }
}
