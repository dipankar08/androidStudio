package in.co.dipankar.ping.contracts;

import android.content.Context;
import android.opengl.GLSurfaceView;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.activities.CallIncommingPageView;

/**
 * Created by dip on 3/10/18.
 */

public interface IRtcEngine {

    void init(Context context,
                 ICallSignalingApi callSingleingApi,
                 GLSurfaceView selfView ,
                 GLSurfaceView peerView );

    // Signling API
    void addIceCandidateToPeerConnection(IceCandidate ice);
    void setRemoteDescriptionToPeerConnection(SessionDescription sdp);
    void setLocalDescriptionToPeerConnection(SessionDescription sdp);

    //user API
    void startAudioCall(String userid);
    void startVideoCall(String userid);
    void acceptCall();
    void rejectCall();

    void toggleVideo(boolean isOn);

    void toggleAudio(boolean isOn);

    void toggleCamera(boolean isOn);

    interface Callback{
        void onSendOffer();
        void onSendAns();
        void onFailure(String s);
    }

    void endCall();

    void setCallback(Callback callback);
}
