package in.co.dipankar.ping.activities.call;

import static in.co.dipankar.ping.common.webrtc.Constant.AUDIO_CODEC_OPUS;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.model.CallInfo;
import in.co.dipankar.ping.common.model.IContactManager;
import in.co.dipankar.ping.common.utils.DataUsesReporter;
import in.co.dipankar.ping.common.webrtc.WebRtcEngine2;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IMultiVideoPane;
import in.co.dipankar.ping.contracts.IRtcEngine;
import in.co.dipankar.ping.contracts.IRtcUser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class CallPresenter implements ICallPage.IPresenter {
  private ICallPage.IView mView;

  // Utils
  String mCallId;
  IRtcUser mPeerRtcUser;
  ICallInfo mCallInfo;
  IMultiVideoPane mMultiVideoPane;

  // rtc engine
  IRtcEngine mWebRtcEngine;
  // singnaling api
  ICallSignalingApi mSignalingApi;

  // Incoming Calls
  SessionDescription mPeerSdp;

  DataUsesReporter mDataUsesReporter;

  public CallPresenter(
      ICallPage.IView view,
      IRtcUser peer,
      ICallInfo.ShareType shareType,
      IMultiVideoPane multiVideoPane) {
    mView = view;
    mPeerRtcUser = peer;
    mMultiVideoPane = multiVideoPane;
    init();
    this.mCallInfo =
        new CallInfo(
            getRandomCallId(),
            shareType,
            ICallInfo.CallType.OUTGOING_CALL,
            PingApplication.Get().getMe().getUserId(),
            peer.getUserId(),
            "0",
            getTimeNow(),
            "0 Kb");
  }

  // this is a function which allows to have a call when other call ends with same presenter
  @Override
  public void reset(IRtcUser peer, ICallInfo.ShareType shareType) {
    mPeerRtcUser = peer;
    this.mCallInfo =
        new CallInfo(
            getRandomCallId(),
            shareType,
            ICallInfo.CallType.OUTGOING_CALL,
            PingApplication.Get().getMe().getUserId(),
            peer.getUserId(),
            "0",
            getTimeNow(),
            "0 Kb");
  }

  private String getTimeNow() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    String currentDateandTime = sdf.format(new Date());
    return currentDateandTime;
    //  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    // LocalDateTime now = LocalDateTime.now();
    // return dtf.format(now).toString(); //2016/11/16 12:08:43
  }

  private void init() {
    IRtcEngine.RtcConfiguration rtcConfiguration = new IRtcEngine.RtcConfiguration();
    rtcConfiguration.audioCodec = AUDIO_CODEC_OPUS;
    rtcConfiguration.audioStartBitrate = 6;
    if (mSignalingApi == null) {
      mSignalingApi = PingApplication.Get().getCallSignalingApi();
      mSignalingApi.addCallback(mSignalingCallback);
    }
    if (mWebRtcEngine == null) {
      mWebRtcEngine =
          new WebRtcEngine2(
              (Context) mView,
              rtcConfiguration,
              mSignalingApi,
              mMultiVideoPane.getSelfView(),
              mMultiVideoPane.getPeerView());
      mWebRtcEngine.addCallback(mWebrtcEngineCallback);
      mWebRtcEngine.enableStatsEvents(true, 1000);
    } else {
      mWebRtcEngine.setRtcConfiguration(rtcConfiguration);
    }
    if (mDataUsesReporter == null) {
      mDataUsesReporter = new DataUsesReporter();
      mDataUsesReporter.addCallback(mDataUsesReporterCallback);
    }
  }

  private ICallSignalingApi.ICallSignalingCallback mSignalingCallback =
      new ICallSignalingApi.ICallSignalingCallback() {
        @Override
        public void onTryConnecting() {}

        @Override
        public void onConnected() {}

        @Override
        public void onDisconnected() {}

        @Override
        public void onPresenceChange(IRtcUser user, ICallSignalingApi.PresenceType type) {}

        @Override
        public void onWelcome(List<IRtcUser> liveUserList) {}

        @Override
        public void onReceivedOffer(
            String callid, SessionDescription sdp, IRtcUser user, boolean isVideoEnabled) {
          // This will taken care bt HomePresenter
          // TODO Incase of Conflict Call
        }

        @Override
        public void onReceivedAnswer(String callid, SessionDescription sdp) {
          if (mWebRtcEngine != null) {
            mWebRtcEngine.setRemoteDescriptionToPeerConnection(sdp);
          }
          mCallInfo.setType(ICallInfo.CallType.OUTGOING_CALL);
          mView.switchToView(ICallPage.PageViewType.ONGOING);
          startTimerInternal();
        }

        @Override
        public void onReceivedCandidate(String callid, IceCandidate ice) {
          if (mWebRtcEngine != null) {
            mWebRtcEngine.addIceCandidateToPeerConnection(ice);
          }
        }

        @Override
        public void onReceivedEndCall(
            String callid, ICallSignalingApi.EndCallType type, String reason) {
          handleEndCallInternal(type, reason);
        }
      };

  private IRtcEngine.Callback mWebrtcEngineCallback =
      new IRtcEngine.Callback() {
        @Override
        public void onSendOffer() {
          mView.switchToView(ICallPage.PageViewType.OUTGOING);
          mCallInfo.setType(ICallInfo.CallType.MISS_CALL_OUTGOING);
        }

        @Override
        public void onSendAns() {
          mView.switchToView(ICallPage.PageViewType.ONGOING);
          mCallInfo.setType(ICallInfo.CallType.INCOMMING_CALL);
        }

        @Override
        public void onFailure(String s) {
          Log.d("DIPANKAR", "Some Error:" + s);
        }

        @Override
        public void onCameraClose() {
          mView.onCameraOff();
        }

        @Override
        public void onCameraOpen() {
          mView.onCameraOn();
        }

        @Override
        public void onStat(Map<String, String> reports) {
          mView.onRtcStat(reports);
        }
      };

  @Override
  public void startOutgoingCall() {
    if (mCallInfo == null) {
      return;
    }
    if (!PingApplication.Get().hasNetworkConn()) {
      mView.showNetworkNotification("error", "No network");
      finish();
      return;
    }
    if (mWebRtcEngine == null) {
      return;
    }
    PingApplication.Get().setCurrentCallInfo(mCallInfo);
    mWebRtcEngine.startGenericCall(mCallInfo);
    mView.prepareCallUI(mPeerRtcUser, mCallInfo);
    mView.updateOutgoingView("Calling " + mPeerRtcUser.getUserName() + "...", "Ringing...");
    mView.switchToView(ICallPage.PageViewType.OUTGOING);
  }

  @Override
  public void startIncomingCall(String callId, SessionDescription sdp) {
    if (mCallInfo == null) {
      return;
    }
    if (!PingApplication.Get().hasNetworkConn()) {
      mView.showNetworkNotification("error", "No network");
      finish();
      return;
    }
    if (mWebRtcEngine == null) {
      return;
    }

    mCallInfo.setType(ICallInfo.CallType.MISS_CALL_INCOMMING);
    mCallId = callId;
    mPeerSdp = sdp;
    // This is the first talk to RTC - So first set the callInfo
    PingApplication.Get().setCurrentCallInfo(mCallInfo);
    mWebRtcEngine.setIncomingCallInfo(mCallInfo);

    mView.prepareCallUI(mPeerRtcUser, mCallInfo);
    mView.toggleViewBasedOnVideoEnabled(mCallInfo.getIsVideo());
    mView.updateIncomingView(
        mPeerRtcUser.getUserName() + " calling...", mPeerRtcUser.getUserName() + " calling you..");
    mView.switchToView(ICallPage.PageViewType.INCOMMING);
  }

  @Override
  public void endCall() {
    handleEndCallInternal(ICallSignalingApi.EndCallType.NORMAL_END, "You ended the call");
  }

  @Override
  public void acceptCall() {
    assert (mWebRtcEngine != null);
    mWebRtcEngine.setRemoteDescriptionToPeerConnection(mPeerSdp);
    mWebRtcEngine.acceptCall(mCallId);
    mCallInfo.setType(ICallInfo.CallType.INCOMMING_CALL);
    mView.switchToView(ICallPage.PageViewType.ONGOING);
    startTimerInternal();
  }

  @Override
  public void rejectCall() {
    assert (mWebRtcEngine != null);
    mSignalingApi.sendEndCall(
        mCallId, ICallSignalingApi.EndCallType.PEER_REJECT, " You have rejected this call");
    mWebRtcEngine.rejectCall(mCallId);
    handleEndCallInternal(
        ICallSignalingApi.EndCallType.PEER_REJECT, " You have rejected this call");
  }

  @Override
  public void toggleVideo(boolean isOn) {
    if (mWebRtcEngine != null) {
      mWebRtcEngine.toggleVideo(isOn);
    }
  }

  @Override
  public void toggleCamera(boolean isOn) {
    if (mWebRtcEngine != null) {
      mWebRtcEngine.toggleCamera(isOn);
    }
  }

  @Override
  public void toggleAudio(boolean isOn) {
    if (mWebRtcEngine != null) {
      mWebRtcEngine.toggleAudio(isOn);
    }
  }

  @Override
  public void toggleSpeaker(boolean isOn) {
    // Note to do.
  }

  @Override
  public void finish() {
    // Please cleanup Eveeything.
    mWebRtcEngine.removeCallback(mWebrtcEngineCallback);
    mSignalingApi.removeCallback(mSignalingCallback);
    mDataUsesReporter.removeCallback(mDataUsesReporterCallback);
    mWebRtcEngine = null;
    mSignalingApi = null;
    mView = null;
    // This is the last thing indicate I am not in call.
    PingApplication.Get().setPeer(null);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mWebRtcEngine.onActivityResult(requestCode, requestCode, data);
  }

  @Override
  public void changeAudioBitrate(int i) {
    // TODO mWebRtcEngine
    // How to chnage Audio BitRtae at runtime.
  }

  private void handleEndCallInternal(ICallSignalingApi.EndCallType type, String reason) {
    mDataUsesReporter.stop();
    Map<String, Long> info = mDataUsesReporter.getInfo();
    reason =
        "This call is ended because of "
            + type.toString()
            + "("
            + reason
            + ")"
            + ". The Duration of the call is:"
            + info.get("time")
            + "sec and the total data uses is:"
            + info.get("data")
            + " kb";
    mView.updateEndView(type.toString().toUpperCase(), reason);
    mView.switchToView(ICallPage.PageViewType.ENDED);
    if (mWebRtcEngine != null) {
      mWebRtcEngine.endCall();
    }
    IContactManager contactManager = PingApplication.Get().getUserManager();
    mCallInfo.setDataUses(info.get("data") + "");
    mCallInfo.setDuration(info.get("time") + "");
    contactManager.addCallInfo(mCallInfo);
    // Notify application that call is now done
    PingApplication.Get().setCurrentCallInfo(null);
  }

  private String getRandomCallId() {
    return UUID.randomUUID().toString();
  }

  private void startTimerInternal() {
    mDataUsesReporter.start();
    /*
    mView.updateOngoingView(mPeerRtcUser.getUserName(),"00:00");
    mDuration = 0;
    mCallTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            mDuration++;
            String min = mDuration/60 < 10 ? "0"+mDuration/60+"": mDuration/60+"";
            String sec = mDuration%60 < 10 ? "0"+mDuration%60+"": mDuration%60+"";
            String status = min+":"+sec;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mView.updateOngoingView(null,status);
                }
            });
        }
    }, 0, 1000);
    */
  }

  private void runOnUIThread(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  DataUsesReporter.Callback mDataUsesReporterCallback =
      new DataUsesReporter.Callback() {
        @Override
        public void onUpdate(int time, long tx, long rx) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  String status =
                      fix(time / 60)
                          + ":"
                          + fix(time % 60)
                          + "s  *  "
                          + tx
                          + "kbps  *  "
                          + rx
                          + "kbps";
                  mView.updateOngoingView(null, status);
                }
              });
        }

        @Override
        public void onFinish(int time, long TxTotal, long RxTotal) {}
      };

  private String fix(int i) {
    return (i < 10 ? "0" + i : i + "");
  }
}
