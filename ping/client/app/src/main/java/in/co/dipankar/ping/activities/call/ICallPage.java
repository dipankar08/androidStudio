package in.co.dipankar.ping.activities.call;

import android.content.Intent;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
import java.util.Map;
import org.webrtc.SessionDescription;

public interface ICallPage {

  public enum PageViewType {
    LANDING,
    INCOMMING,
    OUTGOING,
    ONGOING,
    ENDED
  }

  public interface IView {

    void switchToView(PageViewType type);

    void showNetworkNotification(String process, String s);

    void onCameraOff();

    void onCameraOn();

    void onRtcStat(Map<String, String> reports);

    void toggleViewBasedOnVideoEnabled(boolean isVideoEnabled);

    void prepareCallUI(IRtcUser peer, ICallInfo callinfo);

    void updateOutgoingView(String title, String subtitle);

    void updateIncomingView(String title, String subtitle);

    void updateEndView(String title, String subtitle);

    void updateOngoingView(String title, String subtitle);
  }

  public interface IPresenter {

    void startOutgoingCall();

    void startIncomingCall(String callId, SessionDescription sdp);

    void endCall();

    void acceptCall();

    void rejectCall();

    void toggleVideo(boolean isOn);

    void toggleCamera(boolean isOn);

    void toggleAudio(boolean isOn);

    void toggleSpeaker(boolean isOn);

    void finish();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void changeAudioBitrate(int i);

    void reset(IRtcUser peer, ICallInfo.ShareType shareType);
  }
}
