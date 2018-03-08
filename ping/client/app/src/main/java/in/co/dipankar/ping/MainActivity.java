package in.co.dipankar.ping;


        import android.content.Context;
        import android.media.AudioManager;
        import android.opengl.GLSurfaceView;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;

        import org.json.JSONException;
        import org.json.JSONObject;
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

        import java.net.URISyntaxException;
        import java.util.ArrayList;

        import io.socket.client.IO;
        import io.socket.client.Socket;
        import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private static final String SIGNALING_URI = "http://192.168.1.106:7000";
    private static final String VIDEO_TRACK_ID = "video1";
    private static final String AUDIO_TRACK_ID = "audio1";
    private static final String LOCAL_STREAM_ID = "stream1";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";

    private static final String SDP = "sdp";
    private static final String CREATEOFFER = "createoffer";

    private static final String OFFER = "offer";
    private static final String ANSWER = "answer";
    private static final String CANDIDATE = "candidate";
    private static final String OUTGOING_PING = "pingsss";
    private static final String INCOMMING_PING= "onlineusers";


    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource localVideoSource;
    private PeerConnection peerConnection;
    private MediaStream localMediaStream;
    private VideoRenderer otherPeerRenderer;
    private Socket socket;
    private boolean createOffer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);

        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        peerConnectionFactory = new PeerConnectionFactory();

        VideoCapturerAndroid vc = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice(), null);

        localVideoSource = peerConnectionFactory.createVideoSource(vc, new MediaConstraints());
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
        localVideoTrack.setEnabled(true);

        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);

        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);

        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);

        VideoRendererGui.setView(videoView, null);
        try {
            otherPeerRenderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            VideoRenderer renderer = VideoRendererGui.createGui(50, 50, 50, 50, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }

    public void init() {
       // if (peerConnection != null)
        //    return;



        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                new MediaConstraints(),
                peerConnectionObserver);

        peerConnection.addStream(localMediaStream);

        try {
            socket = IO.socket(SIGNALING_URI);
            socket.on(INCOMMING_PING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handleIncomingPing(args);
                }
            }).on(OFFER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handleIncomingCall(args);
                }
            }).on(ANSWER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handleAcceptedCall(args);
                }

            }).on(CANDIDATE, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        peerConnection.addIceCandidate(new IceCandidate(obj.getString(SDP_MID),
                                obj.getInt(SDP_M_LINE_INDEX),
                                obj.getString(SDP)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });

            socket.connect();
            sendPing();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Pings
    void sendPing(){
        Log.e("DIP111", "Send Ping");
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_name", "DIP111");
            obj.put("user_id", "DIP111");
            socket.emit(OUTGOING_PING, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void handleIncomingPing(Object ... args){
        Log.e("DIP111", "Recv Ping");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            Log.e("DIP111", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void handleAcceptedCall(Object ...args ){
        try {
            JSONObject obj =  new JSONObject(args[0].toString());
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                    obj.getString(SDP));
            peerConnection.setRemoteDescription(sdpObserver, sdp);
            Log.e("DIP111","Receved Ans  Call");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //handing incoming call
    // I am getting a incomming call
    // ring the UI and set the desp. as remote.
    void handleIncomingCall(Object ... args){
        init();
        Log.d("DIP111"," SHOW INCOMMING RING UI");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER,
                    obj.getString(SDP));
            peerConnection.setRemoteDescription(sdpObserver, sdp);
            // DONOT accept ATOTOCALTICAALLY...
            //peerConnection.createAnswer(sdpObserver, new MediaConstraints());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Call Request
    public void sendCallRequest(View button){
        createOffer = true;
        peerConnection.createOffer(sdpObserver, new MediaConstraints());
    }

    // WE
    public void acceptCall(View button){
        peerConnection.createAnswer(sdpObserver, new MediaConstraints());
    }

    public void rejectCall(View button){
        Log.d("DIP111"," SHOW REJECTION UI");
    }

    // Note that this must be shared.
    SdpObserver sdpObserver = new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP, sessionDescription.description);
                if (createOffer) {
                    socket.emit(OFFER, obj);
                } else {
                    socket.emit(ANSWER, obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {
            Log.d("DIP111"," onSetSuccess Success");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d("DIP111"," onCreateFailure failed");
        }

        @Override
        public void onSetFailure(String s) {
            Log.d("DIP111"," onSetFailure failed...");
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
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP_MID, iceCandidate.sdpMid);
                obj.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
                obj.put(SDP, iceCandidate.sdp);
                socket.emit(CANDIDATE, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
}
