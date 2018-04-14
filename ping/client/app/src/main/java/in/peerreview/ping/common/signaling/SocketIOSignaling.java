package in.peerreview.ping.common.signaling;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.Configuration;
import in.peerreview.ping.contracts.ICallSignalingApi;
import in.peerreview.ping.contracts.IRtcDeviceInfo;
import in.peerreview.ping.contracts.IRtcUser;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class SocketIOSignaling implements ICallSignalingApi {

  private static final String SDP = "sdp";
  private static final String CALLID = "call_id";
  private static final String CALLER_INFO = "user_info";
  private static final String PEER_ID = "peer_id";
  private static final String SDP_MID = "sdpMid";
  private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";

  private List<ICallSignalingApi.ICallSignalingCallback> mCallbackList;

  private IRtcUser mRtcUser;
  private IRtcDeviceInfo mRtcDeviceInfo;
  private Handler mainUIHandler = new Handler(Looper.getMainLooper());
  private Socket mSocket;

  public SocketIOSignaling(IRtcUser user, IRtcDeviceInfo device) {
    mCallbackList = new ArrayList<>();
    mRtcUser = user;
    mRtcDeviceInfo = device;
    if (mSocket == null) {
      try {
        mSocket = IO.socket(Configuration.SIGNALING_ENDPOINT);
        init();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void addCallback(ICallSignalingCallback callback) {
    mCallbackList.add(callback);
  }

  @Override
  public void removeCallback(ICallSignalingCallback callback) {
    for (ICallSignalingCallback callback1 : mCallbackList) {
      if (callback == callback1) {
        DLog.e("We remove this callback");
        mCallbackList.remove(callback1);
      }
    }
  }

  private void init() {
    mSocket
        .on(
            Socket.EVENT_CONNECT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvConnect(args);
              }
            })
        .on(
            Socket.EVENT_DISCONNECT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvDisconnect(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_OFFER.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvOffer(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_ANSWER.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvAns(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_CANDIDATE.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvIce(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_ENDCALL.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvEndCall(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_TEST.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvTest(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_INVALID_PAYLOAD.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvInvalidPayload(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_NOTI.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvNoti(args);
              }
            })
        .on(
            "presence",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvPresence(args);
              }
            })
        .on(
            SignalType.TOPIC_IN_WELCOME.type,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                onRecvWelcome(args);
              }
            });
  }

  @Override
  public void connect() {
    if (!mSocket.connected()) {
      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onTryConnecting();
                }
              }
            });
      }
      mSocket.connect();
      DLog.e("Send Connecting");
    } else {
      DLog.e("Already Connected");
    }
  }

  @Override
  public void disconnect() {
    mSocket.disconnect();
    if (mCallbackList != null) {
      runOnUIThread(
          new Runnable() {
            @Override
            public void run() {
              for (ICallSignalingCallback callback : mCallbackList) {
                callback.onDisconnected();
              }
            }
          });
    }
  }

  @Override
  public void sendOffer(String peerID, String callId, Object description, boolean isVideoEnabled) {
    DLog.e("Send Offer");
    try {
      JSONObject obj = new JSONObject();
      obj.put(PEER_ID, peerID);
      obj.put(SDP, description);
      obj.put(CALLID, callId);
      obj.put("is_video_enabled", isVideoEnabled);

      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_OFFER.type, obj);
      } else {
        if (mCallbackList != null) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (ICallSignalingCallback callback : mCallbackList) {
                    callback.onDisconnected();
                  }
                }
              });
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void resendOffer(String userId, String callId) {
    DLog.e("Send Offer");
    try {
      JSONObject obj = new JSONObject();
      obj.put("user_id", userId);
      obj.put("call_id", callId);
      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_RESEND_OFFER.type, obj);
      } else {
        if (mCallbackList != null) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (ICallSignalingCallback callback : mCallbackList) {
                    callback.onDisconnected();
                  }
                }
              });
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendAnswer(String callId, Object description) {
    DLog.e("Send Answer");
    try {
      JSONObject obj = new JSONObject();
      obj.put(SDP, description);
      obj.put(CALLID, callId);

      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_ANSWER.type, obj);
      } else {
        if (mCallbackList != null) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (ICallSignalingCallback callback : mCallbackList) {
                    callback.onDisconnected();
                  }
                }
              });
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendCandidate(String callId, IceCandidate iceCandidate) {
    DLog.e("Send Ice");
    try {
      JSONObject obj = new JSONObject();
      obj.put(CALLID, callId);
      obj.put(SDP_MID, iceCandidate.sdpMid);
      obj.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
      obj.put(SDP, iceCandidate.sdp);

      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_CANDIDATE.type, obj);
      } else {
        if (mCallbackList != null) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (ICallSignalingCallback callback : mCallbackList) {
                    callback.onDisconnected();
                  }
                }
              });
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendEndCall(String callId, EndCallType type, String reason) {
    DLog.e("Send EndCall");
    try {
      JSONObject obj = new JSONObject();
      obj.put(CALLID, callId);
      obj.put("type", type.toString());
      obj.put("reason", reason);

      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_ENDCALL.type, obj);
      } else {
        if (mCallbackList != null) {
          runOnUIThread(
              new Runnable() {
                @Override
                public void run() {
                  for (ICallSignalingCallback callback : mCallbackList) {
                    callback.onDisconnected();
                  }
                }
              });
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void sendRegister() {
    DLog.e("Send Register");
    try {
      JSONObject obj = new JSONObject();
      obj.put("user_id", mRtcUser.getUserId());
      obj.put("user_name", mRtcUser.getUserName());
      obj.put("user_details", mRtcUser.toString());
      obj.put("device_id", mRtcDeviceInfo.getDeviceId());
      obj.put("device_loc", mRtcDeviceInfo.getDeviceLocation());
      obj.put("device_name", mRtcDeviceInfo.getDeviceName());
      try {
        obj.put(CALLER_INFO, Base64Coder.toString(mRtcUser));
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (mSocket.connected()) {
        mSocket.emit(SignalType.TOPIC_OUT_REGISTER.type, obj);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void runOnUIThread(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  // Incomming call handler...
  private void onRecvConnect(Object... args) {
    DLog.e("Received onRecvConnect");
    sendRegister();
  }

  private void onRecvDisconnect(Object... args) {
    DLog.e("Received onRecvDisconnect");
    if (mCallbackList != null) {
      runOnUIThread(
          new Runnable() {
            @Override
            public void run() {
              for (ICallSignalingCallback callback : mCallbackList) {
                callback.onDisconnected();
              }
            }
          });
    }
  }

  private void onRecvAns(Object... args) {
    DLog.e("Received Answer");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final SessionDescription sdp =
          new SessionDescription(SessionDescription.Type.ANSWER, obj.getString(SDP));
      final String callId = obj.getString(CALLID);

      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedAnswer(callId, sdp);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvOffer(Object... args) {
    DLog.e("Received Offer");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final SessionDescription sdp =
          new SessionDescription(SessionDescription.Type.OFFER, obj.getString(SDP));
      final String callId = obj.getString(CALLID);
      final String peer_info = obj.getString("peer_info");
      final boolean isVideoEnabled = obj.getBoolean("is_video_enabled");
      IRtcUser user = null;
      try {
        user = (IRtcUser) Base64Coder.fromString(peer_info);
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
      if (mCallbackList != null) {
        IRtcUser finalUser = user;
        mainUIHandler.post(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedOffer(callId, sdp, finalUser, isVideoEnabled);
                }
              }
            });
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvIce(Object... args) {
    DLog.e("Received Ice");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final IceCandidate ice =
          new IceCandidate(
              obj.getString(SDP_MID), obj.getInt(SDP_M_LINE_INDEX), obj.getString(SDP));
      final String callId = obj.getString(CALLID);
      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedCandidate(callId, ice);
                }
              }
            });
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvEndCall(Object... args) {
    DLog.e("Received EndCall");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final String type = obj.getString("type");
      final String reason = obj.getString("reason");
      final String callId = obj.getString(CALLID);
      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedEndCall(
                      callId, EndCallType.valueOf(type.toUpperCase()), reason);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvTest(Object... args) {
    DLog.e("Received Test");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final String type = obj.getString("type");
      final String reason = obj.getString("reason");
      final String callId = obj.getString(CALLID);
      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedEndCall(
                      callId, EndCallType.valueOf(type.toUpperCase()), reason);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvInvalidPayload(Object... args) {
    DLog.e("Received onRecvInvalidPayload");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      final String type = obj.getString("type");
      final String reason = obj.getString("reason");
      final String callId = obj.getString(CALLID);
      if (mCallbackList != null) {
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onReceivedEndCall(
                      callId, EndCallType.valueOf(type.toUpperCase()), reason);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvNoti(Object... args) {
    DLog.e("Received onRecvInvalidPayload");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      NotificationType type = NotificationType.valueOf(obj.getString("type").toUpperCase());
      final String msg = obj.getString("msg");
      switch (type) {
        case CONNECTED:
          if (mCallbackList != null) {
            runOnUIThread(
                new Runnable() {
                  @Override
                  public void run() {
                    for (ICallSignalingCallback callback : mCallbackList) {
                      callback.onConnected();
                    }
                  }
                });
          }
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvPresence(Object... args) {
    DLog.e("Received onRecvPresence");
    try {
      JSONObject obj = new JSONObject(args[0].toString());
      PresenceType type = PresenceType.valueOf(obj.getString("type").toUpperCase());

      final String user_info = obj.getString("user_info");
      IRtcUser user = null;
      try {
        user = (IRtcUser) Base64Coder.fromString(user_info);
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

      if (mCallbackList != null) {
        IRtcUser finalUser = user;
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onPresenceChange(finalUser, type);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void onRecvWelcome(Object... args) {
    DLog.e("Received onRecvWelcome");
    try {
      JSONObject obj = new JSONObject(args[0].toString());

      final JSONArray live_users = obj.getJSONArray("live_users");
      List<IRtcUser> userList = new ArrayList<>();

      for (int i = 0; i < live_users.length(); i++) {
        String userStr = live_users.getString(i);
        try {
          IRtcUser userObj = (RtcUser) Base64Coder.fromString(userStr);
          userList.add(userObj);
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
      if (mCallbackList != null) {
        List<IRtcUser> finalUser = userList;
        runOnUIThread(
            new Runnable() {
              @Override
              public void run() {
                for (ICallSignalingCallback callback : mCallbackList) {
                  callback.onWelcome(finalUser);
                }
              }
            });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public static class Base64Coder {
    public static byte[] fromBase64(String s) {
      return Base64.decode(s, Base64.DEFAULT);
    }

    public static String toBase64(byte[] bytes) {
      return new String(Base64.encode(bytes, Base64.DEFAULT));
    }

    public static Object fromString(String s) throws IOException, ClassNotFoundException {
      byte[] data = fromBase64(s);
      ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
      Object o = ois.readObject();
      ois.close();
      return o;
    }

    public static String toString(Serializable o) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      oos.close();
      return toBase64(baos.toByteArray());
    }
  }

  @Override
  public void onResume() {
    sendRegister();
  }

  @Override
  public void onPause() {
    disconnect();
  }
}
