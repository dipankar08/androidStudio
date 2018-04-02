package in.co.dipankar.ping.contracts;

import android.content.Intent;

import org.webrtc.IceCandidate;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.Map;

import in.co.dipankar.ping.common.model.CallInfo;

import static in.co.dipankar.ping.common.webrtc.Constant.AUDIO_CODEC_ISAC;
import static in.co.dipankar.ping.common.webrtc.Constant.VIDEO_CODEC_VP8;

public interface IRtcEngine {
    // Signling API
    void addIceCandidateToPeerConnection(IceCandidate ice);
    void setRemoteDescriptionToPeerConnection(SessionDescription sdp);
    void setLocalDescriptionToPeerConnection(SessionDescription sdp);

    //user API
    //void startAudioCall(String callId, String peerid);
    //void startVideoCall(String callId, String userid);

    // This is a New API which support mutiple type like screen shreing , local video sharing.
    void startGenericCall(ICallInfo callInfo);


    void acceptCall(String callId);
    void rejectCall(String callId);
    void endCall();

    // Config API
    void toggleVideo(boolean isOn);
    void toggleAudio(boolean isOn);
    void toggleCamera(boolean isOn);
    void switchVideoScaling(RendererCommon.ScalingType scalingType);

    void addCallback(Callback callback);
    void removeCallback(Callback callback);

    void getStats();

    void enableStatsEvents(boolean enable, int periodMs);

    // TODO: This should be the busicess of RTC Engine.
    void onActivityResult(int requestCode, int resultcode, Intent data);

    void setIncomingCallInfo(ICallInfo mCallInfo);

    interface Callback{
        void onSendOffer();
        void onSendAns();
        void onFailure(String s);

        // UI APIs
        void onCameraClose();

        void onCameraOpen();
        void onStat(Map<String, String> reports);
    }

    public class RtcConfiguration{
        public RtcConfiguration(boolean videoCallEnabled,
                                RendererCommon.ScalingType scaleType,
                                Dimension videoDimension,
                                int videoFps,
                                int videoMaxBitrate,
                                int audioStartBitrate,
                                String videoCodec,
                                String audioCodec) {
            mScaleType = scaleType;
            this.videoCallEnabled = videoCallEnabled;
            this.videoDimention = videoDimension;
            this.videoFps = videoFps;
            this.videoMaxBitrate = videoMaxBitrate;
            this.audioStartBitrate = audioStartBitrate;
            this.videoCodec = videoCodec;
            this.audioCodec = audioCodec;
        }

        public  boolean videoCallEnabled = true;

        // Video Info
        public Dimension videoDimention =  Dimension.Dimension_240P;
        public int videoFps = 30;
        public int videoMaxBitrate;
        public RendererCommon.ScalingType mScaleType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;

        //Audio info
        public int audioStartBitrate;

        //Codec
        public String videoCodec = VIDEO_CODEC_VP8;
        public String audioCodec = AUDIO_CODEC_ISAC;

        public RtcConfiguration() {
        }

       /* Thease will be supported later
        public final boolean loopback;
        public final boolean tracing;
        public final boolean videoCodecHwAcceleration;
        public final boolean videoFlexfecEnabled;
        public final boolean noAudioProcessing;
        public final boolean aecDump;
        public final boolean useOpenSLES;
        public final boolean disableBuiltInAEC;
        public final boolean disableBuiltInAGC;
        public final boolean disableBuiltInNS;
        public final boolean enableLevelControl;
        public final boolean disableWebRtcAGCAndHPF;
        */
    }

    public enum Dimension {
        Dimension_240P(240, 320),
        Dimension_720P(720, 1280),
        Dimension_1080P(1080, 1920);
        public int width;
        public int height;
        Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
