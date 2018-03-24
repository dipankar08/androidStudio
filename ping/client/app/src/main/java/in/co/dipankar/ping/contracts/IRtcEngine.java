package in.co.dipankar.ping.contracts;

import android.content.Context;
import android.opengl.GLSurfaceView;

import org.webrtc.IceCandidate;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;

import in.co.dipankar.ping.common.webrtc.LocalStreamOptions;

public interface IRtcEngine {
    // Signling API
    void addIceCandidateToPeerConnection(IceCandidate ice);
    void setRemoteDescriptionToPeerConnection(SessionDescription sdp);
    void setLocalDescriptionToPeerConnection(SessionDescription sdp);

    //user API
    void startAudioCall(String callId, String userid);
    void startVideoCall(String callId, String userid);
    void acceptCall(String callId);
    void rejectCall(String callId);
    void endCall();

    // Config API
    void setLocalVideoOption(LocalStreamOptions opt);
    void toggleVideo(boolean isOn);
    void toggleAudio(boolean isOn);
    void toggleCamera(boolean isOn);
    void switchVideoScaling(RendererCommon.ScalingType scalingType);


    interface Callback{
        void onSendOffer();
        void onSendAns();
        void onFailure(String s);

        // UI APIs
        void onCameraClose();

        void onCameraOpen();
    }

    void setCallback(Callback callback);
}
