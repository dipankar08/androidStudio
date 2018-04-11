package in.peerreview.ping.common.webrtc;

import static in.peerreview.ping.common.webrtc.Constant.AUDIO_CODEC_OPUS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import in.peerreview.ping.common.webrtc.fork.effect.EffectCamera1Enumerator;
import in.peerreview.ping.common.webrtc.fork.effect.RTCVideoEffector;
import in.peerreview.ping.common.webrtc.fork.filecapture.FileVideoCapturer;
import in.peerreview.ping.common.webrtc.fork.screencapture.ScreenCapturerAndroid;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.ICallSignalingApi;
import in.peerreview.ping.contracts.IRtcEngine;
import in.co.dipankar.quickandorid.utils.DLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
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
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class WebRtcEngine2 implements IRtcEngine {

  private static final ExecutorService executor = Executors.newSingleThreadExecutor();
  private static final String TAG = "MainActivity";

  private boolean createOffer = false;
  Context mContext;
  RtcConfiguration mRtcConfiguration;

  // CallBacks
  private ICallSignalingApi mCallSingleingApi;
  private List<Callback> mCallbackList;

  // Call information
  String mCallID = "0"; // defulat
  private String mPeerID;

  // Renderer
  private SurfaceViewRenderer mSelfRenderer;
  private SurfaceViewRenderer mPeerRenderer;
  private EglBase rootEglBase;

  // video and audio source
  private VideoSource mLocalVideoSource;
  private AudioSource mLocalAudioSource;

  // video and audio tracks
  private VideoTrack mLocalVideoTrack;
  private AudioTrack mLocalAudioTrack;
  VideoCapturer mVideoCapturer;
  private MediaStream mLocalMediaStream;

  // peerconnection and mPeerConnectionFactory objects
  private PeerConnectionFactory mPeerConnectionFactory;
  private PeerConnection mPeerConnection;
  private PeerConnection.RTCConfiguration mRTCConfiguration;

  // media constraints
  private MediaConstraints mPeerVideoMediaConstraints;
  private MediaConstraints mPeerAudioMediaConstraints;
  private MediaConstraints mSdpMediaConstraints;

  // Audio Settings
  private boolean DTLS = true;
  private boolean disableAudioProcessing = true;
  private boolean enableLevelControl = false;

  private boolean mSafeInit = false;
  private ICallInfo mCallInfo;
  private String mVideoFileAsCamera;

  public WebRtcEngine2(
      Context context,
      RtcConfiguration rtcConfiguration,
      ICallSignalingApi callSingleingApi,
      SurfaceViewRenderer selfView,
      SurfaceViewRenderer peerView) {
    mContext = context;
    mRtcConfiguration = rtcConfiguration;
    mCallSingleingApi = callSingleingApi;
    mSelfRenderer = selfView;
    mPeerRenderer = peerView;
    init();
  }

  private synchronized void init() {
    // initialize audio source and audio error callbacks to log errors
    if (!mSafeInit) {
      initAudioErrorCallbacks();
      initializedRenderer();
      initilizeRTCConfig();
      mSafeInit = true;
    }
    mCallbackList = new ArrayList<>();
  }

  // this should be create and exposed everytime.
  private void reInit() {
    cangeMediaConfig();
    initializeParams();
    createMediaConstraints();
    initializePeerConnection();
    initilizeLocalAudioVideoSource();
    // DONOT make a call here as This method also called in case of Incmmong call and
    // we should not call start call from here.
  }

  private void cangeMediaConfig() {
    if (mCallInfo.getShareType() == ICallInfo.ShareType.SCREEN_SHARE) {
      mPeerRenderer.setMirror(false);
      mSelfRenderer.setMirror(false);
      mRtcConfiguration.videoDimention = Dimension.Dimension_720P;
      mRtcConfiguration.videoFps = 5;
    } else {
      mPeerRenderer.setMirror(true);
      mSelfRenderer.setMirror(true);
    }
  }

  private void initializeParams() {
    if (mCallInfo != null) {
      mPeerID = mCallInfo.getTo();
      mCallID = mCallInfo.getId();
      mRtcConfiguration.videoCallEnabled =
          mCallInfo.getShareType() == ICallInfo.ShareType.SCREEN_SHARE
              || mCallInfo.getShareType() == ICallInfo.ShareType.VIDEO_SHARE
              || mCallInfo.getShareType() == ICallInfo.ShareType.VIDEO_CALL;
      if (mCallInfo.getShareType() == ICallInfo.ShareType.VIDEO_SHARE) {
        mVideoFileAsCamera = mCallInfo.getExtra("file");
        mVideoFileAsCamera = "/sdcard/videoplayback.mp4";
      }
    }
  }

  public void startGenericCall(ICallInfo callInfo) {
    mCallInfo = callInfo;
    mPeerID = callInfo.getTo();
    mCallID = callInfo.getId();
    createOffer = true;
    if (mPeerConnection == null) {
      switch (mCallInfo.getShareType()) {
        case SCREEN_SHARE:
          startScreenCapture();
          break;
        default:
          reInit();
          startCallInternal();
      }
    }
  }

  private void startCallInternal() {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            mPeerConnection.createOffer(sdpObserver, new MediaConstraints());
          }
        });
  }

  @Override
  public void acceptCall(String callId) {
    if (mPeerConnection == null) {
      reInit();
    }
    createOffer = false;
    mCallID = callId;
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            mPeerConnection.createAnswer(sdpObserver, new MediaConstraints());
          }
        });
  }

  @Override
  public void rejectCall(String callId) {
    createOffer = false;
    mCallID = callId;
  }

  @Override
  public void endCall() {
    mCallSingleingApi.sendEndCall(mCallID, ICallSignalingApi.EndCallType.NORMAL_END, "Call ended");
    cleanup();
  }

  @Override
  public void toggleVideo(final boolean isOn) {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            mLocalVideoTrack.setEnabled(isOn);
          }
        });
  }

  @Override
  public void toggleAudio(final boolean isOn) {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            mLocalAudioTrack.setEnabled(isOn);
          }
        });
  }

  @Override
  public void toggleCamera(boolean isOn) {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            if (mVideoCapturer instanceof CameraVideoCapturer) {
              if (!mRtcConfiguration.videoCallEnabled) {
                Log.e(TAG, "Failed to switch camera as Video is not eanbled!");
                return;
              }
              Log.d(TAG, "Switch camera");
              CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) mVideoCapturer;
              cameraVideoCapturer.switchCamera(null);
            } else {
              Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
            }
          }
        });
  }

  @Override
  public void switchVideoScaling(RendererCommon.ScalingType scalingType) {
    // todo
  }

  // All private functions
  private void initilizeLocalAudioVideoSource() {

    // 1. Local Audio Stream
    if (true) {
      mLocalAudioSource = mPeerConnectionFactory.createAudioSource(new MediaConstraints());
      mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack("LocalAudio", mLocalAudioSource);
      mLocalAudioTrack.setEnabled(true);
    }

    // 2. Local Video Stream
    if (mRtcConfiguration.videoCallEnabled) {
      mVideoCapturer = createVideoCapturer(mContext);

      mLocalVideoSource = mPeerConnectionFactory.createVideoSource(mVideoCapturer);
      mLocalVideoTrack =
          mPeerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", mLocalVideoSource);
      mLocalVideoTrack.setEnabled(true);

      mVideoCapturer.startCapture(
          mRtcConfiguration.videoDimention.width,
          mRtcConfiguration.videoDimention.height,
          mRtcConfiguration.videoFps);
      for (Callback cb : mCallbackList) {
        cb.onCameraOpen();
      }
    }

    // 3. Build Stream and attach to Peer Connection.
    mLocalMediaStream = mPeerConnectionFactory.createLocalMediaStream("localstream");
    if (mLocalAudioTrack != null) {
      mLocalMediaStream.addTrack(mLocalAudioTrack);
    }
    if (mLocalVideoTrack != null) {
      mLocalMediaStream.addTrack(mLocalVideoTrack);
    }
    mPeerConnection.addStream(mLocalMediaStream);

    // 4. Configure Video Output.
    if (mRtcConfiguration.videoCallEnabled) {
      ProxyRenderer proxyRenderer = new ProxyRenderer();
      proxyRenderer.setTarget(mSelfRenderer);

      ProxyVideoSink sink = new ProxyVideoSink();
      sink.setTarget(mSelfRenderer);

      VideoRenderer renderer = new VideoRenderer(proxyRenderer);
      mLocalVideoTrack.setEnabled(true);
      mLocalVideoTrack.addRenderer(renderer);
      mLocalVideoTrack.addSink(sink);
    }
  }

  private VideoCapturer createVideoCapturer(Context context) {
    switch (mCallInfo.getShareType()) {
      case VIDEO_CALL:
        return getCamera1Capture(context);
        // return getEffectCapture(context);
        // return getCameraCapture(context);
      case SCREEN_SHARE:
        return getScreenCapture(context);
      case VIDEO_SHARE:
      case AUDIO_SHARE:
        return getVideoFileCapture(mVideoFileAsCamera);
    }
    return null;
  }

  private CameraVideoCapturer getEffectCapture(Context context) {
    RTCVideoEffector rtcEffect = new RTCVideoEffector();
    EffectCamera1Enumerator enumerator = new EffectCamera1Enumerator(rtcEffect);
    final String[] deviceNames = enumerator.getDeviceNames();

    for (String deviceName : deviceNames) {
      if (enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    for (String deviceName : deviceNames) {
      if (!enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    return null;
  }

  private CameraVideoCapturer getCamera1Capture(Context context) {
    RTCVideoEffector rtcEffect = new RTCVideoEffector();
    in.peerreview.ping.common.webrtc.fork.cameraCapture.Camera1Enumerator enumerator =
        new in.peerreview.ping.common.webrtc.fork.cameraCapture.Camera1Enumerator();
    final String[] deviceNames = enumerator.getDeviceNames();

    for (String deviceName : deviceNames) {
      if (enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    for (String deviceName : deviceNames) {
      if (!enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    return null;
  }

  private CameraVideoCapturer getCameraCapture(Context context) {
    CameraEnumerator enumerator;
    if (Camera2Enumerator.isSupported(context)) {
      enumerator = new Camera2Enumerator(context);
    } else {
      enumerator = new Camera1Enumerator(true);
    }

    final String[] deviceNames = enumerator.getDeviceNames();

    for (String deviceName : deviceNames) {
      if (enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    for (String deviceName : deviceNames) {
      if (!enumerator.isFrontFacing(deviceName)) {
        CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }
    return null;
  }

  // TODO - THIS CODE NEED TO MOVED OUT.
  int CAPTURE_PERMISSION_REQUEST_CODE = 111;
  private static Intent mediaProjectionPermissionResultData;
  private static int mediaProjectionPermissionResultCode;

  private void startScreenCapture() {
    MediaProjectionManager mediaProjectionManager =
        (MediaProjectionManager)
            ((Activity) mContext)
                .getApplication()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    ((Activity) mContext)
        .startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE) return;
    mediaProjectionPermissionResultCode = resultCode;
    mediaProjectionPermissionResultData = data;
    reInit();
    startCallInternal();
  }

  @Override
  public void setIncomingCallInfo(ICallInfo callInfo) {
    // Says that we are getting an incomming call.
    createOffer = false;
    mCallInfo = callInfo;
  }

  @Override
  public void setRtcConfiguration(RtcConfiguration rtcConfiguration) {
    mRtcConfiguration = rtcConfiguration;
  }

  private VideoCapturer getScreenCapture(Context context) {
    return new ScreenCapturerAndroid(
        mediaProjectionPermissionResultData,
        new MediaProjection.Callback() {
          @Override
          public void onStop() {
            reportError("User revoked permission to capture the screen.");
          }
        });
  }

  // Video FIle Capture
  private VideoCapturer getVideoFileCapture(String videoFileAsCamera) {
    final VideoCapturer videoCapturer;
    if (videoFileAsCamera != null) {
      try {
        videoCapturer = new FileVideoCapturer(videoFileAsCamera);
        return videoCapturer;
      } catch (IOException e) {
        reportError("Failed to open video file for emulated camera");
        return null;
      }
    }
    return null;
  }

  private void reportError(String s) {
    // TODO
    /*
    private void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }*/
  }

  private void initilizeRTCConfig() {
    // 1. define a list of ICE server
    ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
    iceServers.add(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
    iceServers.add(
        PeerConnection.IceServer.builder("turn:numb.viagenie.ca")
            .setUsername("webrtc@live.co")
            .setPassword("muazkh")
            .createIceServer());
    iceServers.add(
        PeerConnection.IceServer.builder("turn:192.158.29.39:3478?transport=udp")
            .setUsername("28224511:1379330808")
            .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA=")
            .createIceServer());
    iceServers.add(
        PeerConnection.IceServer.builder("turn:turn.bistri.com:80")
            .setUsername("homeo")
            .setPassword("homeo")
            .createIceServer());
    iceServers.add(
        PeerConnection.IceServer.builder("turn:turn.anyfirewall.com:443?transport=tcp")
            .setUsername("webrtc")
            .setPassword("webrtc")
            .createIceServer());

    mRTCConfiguration = new PeerConnection.RTCConfiguration(iceServers);
    // 2. TCP candidates are only useful when connecting to a server that supportsICE-TCP.
    mRTCConfiguration.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
    mRTCConfiguration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
    mRTCConfiguration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
    mRTCConfiguration.continualGatheringPolicy =
        PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
    // 3. Use ECDSA encryption.
    mRTCConfiguration.keyType = PeerConnection.KeyType.ECDSA;
  }

  private void initializePeerConnection() {
    // initilize factory
    PeerConnectionFactory.initialize(
        PeerConnectionFactory.InitializationOptions.builder(mContext)
            .setEnableVideoHwAcceleration(true)
            .createInitializationOptions());

    // if loopback is true ignore network mask
    PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
    options.networkIgnoreMask = 0;

    // create peer connection mPeerConnectionFactory object
    mPeerConnectionFactory = new PeerConnectionFactory(options);
    mPeerConnectionFactory.setVideoHwAccelerationOptions(
        rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());

    mPeerConnection =
        mPeerConnectionFactory.createPeerConnection(
            mRTCConfiguration, mPeerVideoMediaConstraints, mPeerConnectionObserver);
    // mPeerConnection.setConfiguration(mRTCConfiguration);
  }

  private void initializedRenderer() {
    // Intialize UI .
    rootEglBase = EglBase.create();

    // 1. initialize video renderers
    mPeerRenderer.init(rootEglBase.getEglBaseContext(), null);
    mPeerRenderer.setZOrderMediaOverlay(true);
    mPeerRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    mPeerRenderer.setEnableHardwareScaler(true);

    // 2. initialize video renderers
    mSelfRenderer.init(rootEglBase.getEglBaseContext(), null);
    mSelfRenderer.setZOrderMediaOverlay(true);
    mSelfRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    mSelfRenderer.setEnableHardwareScaler(true);

    mPeerRenderer.setMirror(true);
    mSelfRenderer.setMirror(true);
  }

  private void createMediaConstraints() {
    // 1. Create peer connection constraints.
    mPeerVideoMediaConstraints = new MediaConstraints();
    // Enable DTLS for normal calls and disable for loopback calls.
    if (DTLS) {
      mPeerVideoMediaConstraints.optional.add(
          new MediaConstraints.KeyValuePair(Constant.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "false"));
    } else {
      mPeerVideoMediaConstraints.optional.add(
          new MediaConstraints.KeyValuePair(Constant.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
    }
    mPeerVideoMediaConstraints.optional.add(
        new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));

    // 2. Create Peer audio constraints.
    mPeerAudioMediaConstraints = new MediaConstraints();
    // added for audio performance measurements
    if (disableAudioProcessing) {
      mPeerAudioMediaConstraints.mandatory.add(
          new MediaConstraints.KeyValuePair(Constant.AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
      mPeerAudioMediaConstraints.mandatory.add(
          new MediaConstraints.KeyValuePair(Constant.AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
      mPeerAudioMediaConstraints.mandatory.add(
          new MediaConstraints.KeyValuePair(Constant.AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
      mPeerAudioMediaConstraints.mandatory.add(
          new MediaConstraints.KeyValuePair(Constant.AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
    }
    if (enableLevelControl) {
      mPeerAudioMediaConstraints.mandatory.add(
          new MediaConstraints.KeyValuePair(Constant.AUDIO_LEVEL_CONTROL_CONSTRAINT, "true"));
    }

    // 3. Create SDP constraints.
    mSdpMediaConstraints = new MediaConstraints();
    mSdpMediaConstraints.mandatory.add(
        new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
    mSdpMediaConstraints.mandatory.add(
        new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
  }

  SdpObserver sdpObserver =
      new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
          mPeerConnection.setLocalDescription(sdpObserver, sessionDescription);
          if (createOffer) {
            mCallSingleingApi.sendOffer(
                mPeerID,
                mCallID,
                sessionDescription.description,
                mRtcConfiguration.videoCallEnabled);
            runOnUIThread(
                new Runnable() {
                  @Override
                  public void run() {
                    for (Callback cb : mCallbackList) {
                      cb.onSendOffer();
                    }
                  }
                });

          } else {
            mCallSingleingApi.sendAnswer(mCallID, sessionDescription.description);
            runOnUIThread(
                new Runnable() {
                  @Override
                  public void run() {
                    for (Callback cb : mCallbackList) {
                      cb.onSendAns();
                    }
                  }
                });
          }
        }

        @Override
        public void onSetSuccess() {
          Log.d("DIP111", " onSetSuccess Success");
        }

        @Override
        public void onCreateFailure(String s) {
          Log.d("DIP111", " onCreateFailure failed");
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (Callback cb : mCallbackList) {
                    cb.onFailure("SDP Creation failed");
                  }
                }
              });
        }

        @Override
        public void onSetFailure(String s) {
          Log.d("DIP111", " onSetFailure failed...");
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (Callback cb : mCallbackList) {
                    cb.onFailure("set SDP failed");
                  }
                }
              });
        }
      };

  // create camera capturer
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
    WebRtcAudioRecord.setErrorCallback(
        new WebRtcAudioRecord.WebRtcAudioRecordErrorCallback() {
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
    WebRtcAudioTrack.setErrorCallback(
        new WebRtcAudioTrack.WebRtcAudioTrackErrorCallback() {
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

  // define ProxyRenderer
  class ProxyRenderer implements VideoRenderer.Callbacks {
    private VideoRenderer.Callbacks target;

    @Override
    public synchronized void renderFrame(VideoRenderer.I420Frame frame) {
      if (target == null) {
        Log.d(TAG, "Dropping frame in proxy because target is null.");
        VideoRenderer.renderFrameDone(frame);
        return;
      }

      target.renderFrame(frame);
    }

    public synchronized void setTarget(VideoRenderer.Callbacks target) {
      this.target = target;
    }
  }

  // define ProxyVideoSink
  class ProxyVideoSink implements VideoSink {
    private VideoSink target;

    @Override
    public synchronized void onFrame(VideoFrame frame) {
      if (target == null) {
        return;
      }
      target.onFrame(frame);
    }

    public synchronized void setTarget(VideoSink target) {
      this.target = target;
    }
  }

  PeerConnection.Observer mPeerConnectionObserver =
      new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
          Log.d("RTCAPP", "onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
          Log.d("RTCAPP", "onIceConnectionChange:" + iceConnectionState.toString());
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {}

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
          mCallSingleingApi.sendCandidate(mCallID, iceCandidate);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}

        @Override
        public void onAddStream(MediaStream mediaStream) {
          // If video track is available then show only first
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
        public void onRemoveStream(MediaStream mediaStream) {}

        @Override
        public void onDataChannel(DataChannel dataChannel) {}

        @Override
        public void onRenegotiationNeeded() {}

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}
      };

  @Override
  public void addIceCandidateToPeerConnection(IceCandidate ice) {
    if (mPeerConnection == null) {
      reInit();
    }
    mPeerConnection.addIceCandidate(ice);
    DLog.e("WebRtcEngine: addIceCandidateToPeerConnection");
  }

  @Override
  public void setRemoteDescriptionToPeerConnection(SessionDescription sdp) {
    if (mPeerConnection == null) {
      reInit();
    }
    String sdpDescription = sdp.description;

    sdpDescription = WebRtcUtils.preferCodec(sdpDescription, mRtcConfiguration.videoCodec, true);
    sdpDescription = WebRtcUtils.preferCodec(sdpDescription, mRtcConfiguration.audioCodec, false);
    if (mRtcConfiguration.audioCodec.equals(AUDIO_CODEC_OPUS)) {
      sdpDescription =
          WebRtcUtils.setStartBitrate(
              AUDIO_CODEC_OPUS, false, sdpDescription, mRtcConfiguration.audioStartBitrate);
    }
    SessionDescription sdpRemote = new SessionDescription(sdp.type, sdpDescription);
    mPeerConnection.setRemoteDescription(sdpObserver, sdpRemote);
    DLog.e("WebRtcEngine: setRemoteDescriptionToPeerConnection");
  }

  @Override
  public void setLocalDescriptionToPeerConnection(SessionDescription sdp) {
    if (mPeerConnection == null) {
      reInit();
    }
    mPeerConnection.setLocalDescription(sdpObserver, sdp);
  }

  private synchronized void cleanup() {

    // >>> clean peer

    // >>> clean video
    try {
      if (mVideoCapturer != null) {
        mVideoCapturer.stopCapture();
        mVideoCapturer.dispose();
        mVideoCapturer = null;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (mLocalVideoSource != null) {
      mLocalVideoSource.dispose();
      mLocalVideoSource = null;
    }
    if (mLocalVideoTrack != null) {
      mLocalVideoTrack.dispose();
    }

    // >>> clean audio
    if (mLocalAudioSource != null) {
      mLocalAudioSource.dispose();
      mLocalAudioSource = null;
    }
    if (mLocalAudioTrack != null) {
      mLocalAudioTrack.dispose();
    }

    if (mPeerConnection != null) {
      mPeerConnection.removeStream(mLocalMediaStream);
      mPeerConnection.close();
      mPeerConnection.dispose();
      mPeerConnection = null;
    }
    if (mLocalMediaStream != null) {
      // mLocalMediaStream.dispose();
      mLocalMediaStream = null;
    }
    for (Callback cb : mCallbackList) {
      cb.onCameraClose();
    }
  }

  @Override
  public void addCallback(Callback callback) {
    mCallbackList.add(callback);
  }

  @Override
  public void removeCallback(Callback callback) {
    mCallbackList.remove(callback);
  }

  private void runOnUIThread(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  /* Statistic */
  private Timer statsTimer = new Timer(true);

  @Override
  public void getStats() {
    if (mPeerConnection == null) {
      return;
    }
    boolean success =
        mPeerConnection.getStats(
            new StatsObserver() {
              @Override
              public void onComplete(final StatsReport[] reports) {
                runOnUIThread(
                    new Runnable() {
                      @Override
                      public void run() {
                        for (Callback cb : mCallbackList) {
                          cb.onStat(WebRtcUtils.parseStatistics(reports));
                        }
                      }
                    });
              }
            },
            null);
    if (!success) {
      Log.e(TAG, "getStats() returns false!");
    }
  }

  @Override
  public void enableStatsEvents(boolean enable, int periodMs) {
    if (enable) {
      try {
        statsTimer.scheduleAtFixedRate(
            new TimerTask() {
              @Override
              public void run() {
                executor.execute(
                    new Runnable() {
                      @Override
                      public void run() {
                        getStats();
                      }
                    });
              }
            },
            0,
            periodMs);
      } catch (Exception e) {
        Log.e(TAG, "Can not schedule statistics timer", e);
      }
    } else {
      statsTimer.cancel();
    }
  }
}
