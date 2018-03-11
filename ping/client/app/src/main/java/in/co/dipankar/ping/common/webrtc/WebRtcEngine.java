package in.co.dipankar.ping.common.webrtc;

public class WebRtcEngine {

}

/*
import android.content.Context;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcEngine;

public class WebRtcEngine implements IRtcEngine {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    SdpObserver sdpObserver = new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            if (createOffer) {
                mCallSingleingApi.sendOffer(sessionDescription.description);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onSendOffer();
                    }
                });

            } else {
                mCallSingleingApi.sendAnswer(sessionDescription.description);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onSendAns();
                    }
                });
            }
        }
        @Override
        public void onSetSuccess() {
            Log.d("DIP111"," onSetSuccess Success");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d("DIP111"," onCreateFailure failed");
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFailure("SDP Creation failed");
                }
            });
        }

        @Override
        public void onSetFailure(String s) {
            Log.d("DIP111"," onSetFailure failed...");
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFailure("set SDP failed");
                }
            });
        }
    };


    PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("RTCAPP", "onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("RTCAPP", "onIceConnectionChange:" + iceConnectionState.toString());
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            mCallSingleingApi.sendCandidate(iceCandidate);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            mediaStream.videoTracks.getFirst().addRenderer(otherPeerRenderer);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }
    };

    private static final String VIDEO_TRACK_ID = "video1";
    private static final String AUDIO_TRACK_ID = "audio1";
    private static final String LOCAL_STREAM_ID = "stream1";

    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource localVideoSource;
    private PeerConnection peerConnection;
    private MediaStream localMediaStream;
    private VideoRenderer otherPeerRenderer;

    private boolean createOffer = false;
    Context mContext;
    private ICallSignalingApi mCallSingleingApi;
    private IRtcEngine.Callback mCallback;
    private  GLSurfaceView mSelfView,mPeerView;

    public WebRtcEngine(Context context,
                 ICallSignalingApi callSingleingApi,
                 GLSurfaceView selfView ,
                 GLSurfaceView peerView ){
        mContext = context;
        mCallSingleingApi = callSingleingApi;
        mSelfView = selfView;
        mPeerView = peerView;

        //initPeers();
        createNewCall();
    }


    @Override
    public void init(Context context, ICallSignalingApi callSingleingApi, GLSurfaceView selfView, GLSurfaceView peerView) {
        //We did the stuff in cons
    }

    @Override
    public void addIceCandidateToPeerConnection(IceCandidate ice) {
        peerConnection.addIceCandidate(ice);
    }

    @Override
    public void setRemoteDescriptionToPeerConnection(SessionDescription sdp) {
        peerConnection.setRemoteDescription(sdpObserver,sdp);
    }

    @Override
    public void setLocalDescriptionToPeerConnection(SessionDescription sdp) {
        peerConnection.setLocalDescription(sdpObserver,sdp);
    }

    private void initPeers() {

    }

    VideoTrack localVideoTrack;
    AudioTrack localAudioTrack;
    public void createNewCall(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
        PeerConnectionFactory.initializeAndroidGlobals(
                mContext,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        peerConnectionFactory = new PeerConnectionFactory();
        VideoCapturerAndroid vc = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice(), null);

        localVideoSource = peerConnectionFactory.createVideoSource(vc, new MediaConstraints());
         localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
        localVideoTrack.setEnabled(true);

        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);

        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);

        VideoRendererGui.setView(mSelfView, null);
        try {
            otherPeerRenderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            VideoRenderer renderer = VideoRendererGui.createGui(50, 50, 50, 50, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                new MediaConstraints(),
                peerConnectionObserver);

        peerConnection.addStream(localMediaStream);
    }

    @Override
    public void startAudioCall(String userid) {
        createOffer = true;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                peerConnection.createOffer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void startVideoCall(String userid) {
        createOffer = true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                peerConnection.createOffer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void acceptCall() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                peerConnection.createAnswer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void rejectCall() {
        //TODO
    }

    @Override
    public void toggleVideo(final boolean isOn) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                localVideoTrack.setEnabled(isOn);
            }
        });
    }

    @Override
    public void toggleAudio(final boolean isOn) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                localAudioTrack.setEnabled(isOn);
            }
        });
    }

    @Override
    public void toggleCamera(boolean isOn) {

    }


    @Override
    public void endCall() {
       if(peerConnection !=null){
           peerConnection.dispose();
           peerConnection = null;
       }
       if(localVideoTrack != null){
           localVideoTrack.dispose();
           localVideoTrack = null;
       }
       if(localVideoSource != null){
           localVideoSource.dispose();
           localVideoSource = null;
       }
        if(peerConnectionFactory!= null){
           peerConnectionFactory.dispose();
           peerConnectionFactory = null;
        }
        if(otherPeerRenderer != null){
            otherPeerRenderer.dispose();
            otherPeerRenderer.dispose();
        }
    }


    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void runOnUIThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
*/