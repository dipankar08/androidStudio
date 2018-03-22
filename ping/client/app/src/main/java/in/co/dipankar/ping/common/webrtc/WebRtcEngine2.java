package in.co.dipankar.ping.common.webrtc;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioTrack;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcEngine;
import in.co.dipankar.ping.contracts.IRtcUser;

public class WebRtcEngine2 implements IRtcEngine {

    //flag to check if sdp is offer or answer
    private boolean createOffer = false;
    //context
    Context mContext;
    IRtcUser mRtcUser;

    // CallBacks
    private ICallSignalingApi mCallSingleingApi;
    private IRtcEngine.Callback mCallback;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String TAG = "MainActivity";

    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";


    // Call information
   String mCallID = "0"; //defulat

    //Renderer
    private SurfaceViewRenderer mSelfRenderer;
    private SurfaceViewRenderer mPeerRenderer;
    private EglBase rootEglBase;

    //video and audio source
    private VideoSource mLocalVideoSource;
    private AudioSource mLocalAudioSource;

    //video and audio tracks
    private VideoTrack mLocalVideoTrack;
    private AudioTrack mLocalAudioTrack;

    //peerconnection and mPeerConnectionFactory objects
    private PeerConnectionFactory mPeerConnectionFactory;
    private PeerConnection mPeerConnection;
    private PeerConnection.RTCConfiguration mRTCConfiguration;

    //media constraints
    private MediaConstraints mPeerVideoMediaConstraints;
    private MediaConstraints mPeerAudioMediaConstraints;
    private MediaConstraints mSdpMediaConstraints;

    //Audio Settings
    private boolean DTLS = true;
    private boolean disableAudioProcessing = true;
    private boolean enableLevelControl = false;

    //Other Info
    private String mPeerID;
    private String mSelfId;


    public WebRtcEngine2(Context context,
                         IRtcUser rtcUser,
                         ICallSignalingApi callSingleingApi,
                         SurfaceViewRenderer selfView ,
                         SurfaceViewRenderer peerView ){
        mContext = context;
        mCallSingleingApi = callSingleingApi;
        mSelfRenderer = selfView;
        mPeerRenderer = peerView;
        mSelfId = rtcUser.getUserId();
        init();
    }
    private void init(){
        //initialize audio source and audio error callbacks to log errors
        initAudioErrorCallbacks();
        initializedRenderer();
        initilizeRTCConfig();
        reInit();
    }

    // this should be create and exposed everytime.
    private void reInit(){
        createMediaConstraints();
        initializePeerConnection();
        initilizeLocalAudioVideoSource();
    }

    private void initilizeLocalAudioVideoSource() {

        // 1. Create Audio track
        mLocalAudioSource = mPeerConnectionFactory.createAudioSource(new MediaConstraints());
        mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack("LocalAudio", mLocalAudioSource);
        mLocalAudioTrack.setEnabled(true);

        // 2 A. create video capturer
        VideoCapturer videoCapturer = createCameraCapturer(new Camera2Enumerator(mContext));

        // 2.B create Video Track
        mLocalVideoSource = mPeerConnectionFactory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(240, 320, 30);

        ProxyRenderer proxyRenderer = new ProxyRenderer();
        proxyRenderer.setTarget(mSelfRenderer);

        ProxyVideoSink sink = new ProxyVideoSink();
        sink.setTarget(mSelfRenderer);

        VideoRenderer renderer = new VideoRenderer(proxyRenderer);

        mLocalVideoTrack = mPeerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", mLocalVideoSource);
        mLocalVideoTrack.setEnabled(true);
        mLocalVideoTrack.addRenderer(renderer);
        mLocalVideoTrack.addSink(sink);

        // 3. create media stream and add Tracks created earlier
        final MediaStream stream = mPeerConnectionFactory.createLocalMediaStream("ARDAMS");
        stream.addTrack(mLocalAudioTrack);
        stream.addTrack(mLocalVideoTrack);

        // 4. add Stream to peer connection
        mPeerConnection.addStream(stream);
    }

    private void initilizeRTCConfig() {
        //1. define a list of ICE server
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("turn:numb.viagenie.ca").setUsername("webrtc@live.co").setPassword("muazkh").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("turn:192.158.29.39:3478?transport=udp").setUsername("28224511:1379330808").setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA=").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("turn:turn.bistri.com:80").setUsername("homeo").setPassword("homeo").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("turn:turn.anyfirewall.com:443?transport=tcp").setUsername("webrtc").setPassword("webrtc").createIceServer());

        mRTCConfiguration = new PeerConnection.RTCConfiguration(iceServers);
        //2. TCP candidates are only useful when connecting to a server that supportsICE-TCP.
        mRTCConfiguration.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
        mRTCConfiguration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        mRTCConfiguration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        mRTCConfiguration.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        //3. Use ECDSA encryption.
        mRTCConfiguration.keyType = PeerConnection.KeyType.ECDSA;
    }

    private void initializePeerConnection() {
        //initilize factory
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(mContext)
                        .setEnableVideoHwAcceleration(true)
                        .createInitializationOptions());

        //if loopback is true ignore network mask
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.networkIgnoreMask = 0;

        //create peer connection mPeerConnectionFactory object
        mPeerConnectionFactory = new PeerConnectionFactory(options);
        mPeerConnectionFactory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());

        mPeerConnection = mPeerConnectionFactory.createPeerConnection(
                mRTCConfiguration,
                mPeerVideoMediaConstraints,
                mPeerConnectionObserver);

    }

    private void initializedRenderer() {
        // Intialize UI .
        rootEglBase = EglBase.create();

        // 1. initialize video renderers
        mPeerRenderer.init(rootEglBase.getEglBaseContext(), null);
        mPeerRenderer.setMirror(true);
        mPeerRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mPeerRenderer.setEnableHardwareScaler(true);

        // 2. initialize video renderers
        mSelfRenderer.init(rootEglBase.getEglBaseContext(), null);
        mSelfRenderer.setMirror(true);
        mSelfRenderer.setZOrderMediaOverlay(true);
        mSelfRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mSelfRenderer.setEnableHardwareScaler(false);
    }


    private void createMediaConstraints() {
        // 1. Create peer connection constraints.
        mPeerVideoMediaConstraints = new MediaConstraints();
        // Enable DTLS for normal calls and disable for loopback calls.
        if (DTLS) {
            mPeerVideoMediaConstraints.optional.add(
                    new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "false"));
        } else {
            mPeerVideoMediaConstraints.optional.add(
                    new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
        }
        mPeerVideoMediaConstraints.optional.add(
                new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));

        // 2. Create Peer audio constraints.
        mPeerAudioMediaConstraints = new MediaConstraints();
        // added for audio performance measurements
        if (disableAudioProcessing) {
            mPeerAudioMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            mPeerAudioMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            mPeerAudioMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            mPeerAudioMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        if (enableLevelControl) {
            mPeerAudioMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true"));
        }

        // 3. Create SDP constraints.
        mSdpMediaConstraints = new MediaConstraints();
        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

    }


    SdpObserver sdpObserver = new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            mPeerConnection.setLocalDescription(sdpObserver, sessionDescription);
            if (createOffer) {
                mCallSingleingApi.sendOffer(mPeerID, mCallID, sessionDescription.description);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onSendOffer();
                    }
                });

            } else {
                mCallSingleingApi.sendAnswer(mCallID, sessionDescription.description);
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

    //create camera capturer
    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }



    public void initAudioErrorCallbacks() {
        // Set audio WebRtcAudioRecord error callbacks.
        WebRtcAudioRecord.setErrorCallback(new WebRtcAudioRecord.WebRtcAudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordStartError(
                    WebRtcAudioRecord.AudioRecordStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordError: " + errorMessage);
            }
        });

        // Set audio WebRtcAudioTrack error callbacks.
        WebRtcAudioTrack.setErrorCallback(new WebRtcAudioTrack.WebRtcAudioTrackErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackStartError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }
        });
    }


    //define ProxyRenderer
    class ProxyRenderer implements VideoRenderer.Callbacks {
        private VideoRenderer.Callbacks target;
        @Override
        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
            if (target == null) {
                Log.d(TAG, "Dropping frame in proxy because target is null.");
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }
        synchronized public void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }
    }

    // define ProxyVideoSink
    class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                return;
            }
            target.onFrame(frame);
        }
        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }



    PeerConnection.Observer mPeerConnectionObserver = new PeerConnection.Observer() {
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
            mCallSingleingApi.sendCandidate(mCallID, iceCandidate);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
           //If video track is available then show only first
            if (mediaStream.videoTracks.size() > 0) {
                ProxyRenderer proxyRenderer = new ProxyRenderer();
                ProxyVideoSink sink = new ProxyVideoSink();
                sink.setTarget(mPeerRenderer);
                proxyRenderer.setTarget(mPeerRenderer);
                VideoTrack track = mediaStream.videoTracks.get(0);
                track.setEnabled(true);
                track.addRenderer(new VideoRenderer(proxyRenderer));
                track.addSink(sink);
            }

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

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

        }
    };






    @Override
    public void init(Context context, ICallSignalingApi callSingleingApi, GLSurfaceView selfView, GLSurfaceView peerView) {
        //We did the stuff in cons
    }

    @Override
    public void addIceCandidateToPeerConnection(IceCandidate ice) {
        mPeerConnection.addIceCandidate(ice);
    }

    @Override
    public void setRemoteDescriptionToPeerConnection(SessionDescription sdp) {
        mPeerConnection.setRemoteDescription(sdpObserver,sdp);
    }

    @Override
    public void setLocalDescriptionToPeerConnection(SessionDescription sdp) {
        mPeerConnection.setLocalDescription(sdpObserver,sdp);
    }

    @Override
    public void startAudioCall(String callID, String userid) {
        assert(userid != null);
        mCallID = callID;
        if(mPeerConnection == null){
            reInit();
        }
        createOffer = true;
        mPeerID = userid;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mPeerConnection.createOffer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void startVideoCall(String callId, String userid) {
        assert(userid != null);
        mCallID = callId;
        if(mPeerConnection == null){
            reInit();
        }
        createOffer = true;
        mPeerID = userid;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mPeerConnection.createOffer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void acceptCall(String callId) {
        if(mPeerConnection == null){
            reInit();
        }
        mCallID = callId;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mPeerConnection.createAnswer(sdpObserver, new MediaConstraints());
            }
        });
    }

    @Override
    public void rejectCall(String callId) {
        mCallID = callId;
    }

    @Override
    public void toggleVideo(final boolean isOn) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mLocalVideoTrack.setEnabled(isOn);
            }
        });
    }

    @Override
    public void toggleAudio(final boolean isOn) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mLocalAudioTrack.setEnabled(isOn);
            }
        });
    }

    @Override
    public void toggleCamera(boolean isOn) {

    }


    @Override
    public void endCall() {
        mCallSingleingApi.sendEndCall(mCallID, ICallSignalingApi.EndCallType.NORMAL_END,"Call ended");
        cleanup();

    }

    private void cleanup(){
        if(mPeerConnection != null){
            mPeerConnection.dispose();
            mPeerConnection = null;
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
