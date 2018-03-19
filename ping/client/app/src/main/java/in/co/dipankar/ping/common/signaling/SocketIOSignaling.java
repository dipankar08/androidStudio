package in.co.dipankar.ping.common.signaling;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;

import in.co.dipankar.ping.common.webrtc.RtcDeviceInfo;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.Configuration;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.DLog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOSignaling implements ICallSignalingApi {

    private static final String SDP = "sdp";
    private static final String CALLID = "call_id";
    private static final String CALLER_ID = "user_id";
    private static final String PEER_ID = "peer_id";
    private static final String ENDCALL = "endcall";
    private static final String REGISTER = "register";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";


    private ICallSignalingApi.ICallSignalingCallback mCallback;

    private IRtcUser mRtcUser;
    private IRtcDeviceInfo mRtcDeviceInfo;
    Handler mainUIHandler = new Handler(Looper.getMainLooper());
    private static  Socket socket;

    public SocketIOSignaling(IRtcUser user, IRtcDeviceInfo device, ICallSignalingCallback callback) {
        mRtcUser = user;
        mRtcDeviceInfo = device;
        mCallback = callback;
        if(socket == null){
            try {
                socket = IO.socket(Configuration.SIGNALING_ENDPOINT);
                init();
                connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    void init(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvConnect(args);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvDisconnect(args);
            }
        }).on(SignalType.TOPIC_IN_OFFER.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvOffer(args);
            }
        }).on(SignalType.TOPIC_IN_ANSWER.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvAns(args);
            }

        }).on(SignalType.TOPIC_IN_CANDIDATE.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvIce(args);
            }
        }).on(SignalType.TOPIC_IN_ENDCALL.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvEndCall(args);
            }
        }).on(SignalType.TOPIC_IN_TEST.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvTest(args);
            }
        }).on(SignalType.TOPIC_IN_INVALID_PAYLOAD.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvInvalidPayload(args);
            }
        }).on(SignalType.TOPIC_IN_NOTI.type, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onRecvNoti(args);
            }
        });
    }

    @Override
    public void connect() {
        mCallback.onTryConnecting();
        socket.connect();
        DLog.e("Send Connecting");
    }

    @Override
    public void disconnect() {
        socket.disconnect();
        mCallback.onDisconnected();
    }

    @Override
    public void sendOffer(String peerID, String callId, Object description) {
        DLog.e("Send Offer");
        try {
            JSONObject obj = new JSONObject();
            obj.put(PEER_ID,peerID);
            obj.put(CALLER_ID,mRtcUser.getUserId());
            obj.put(SDP,description);
            obj.put(CALLID,callId);

            if(socket.connected()) {
                socket.emit(SignalType.TOPIC_OUT_OFFER.type, obj);
            } else{
                mCallback.onDisconnected();
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
            obj.put(CALLER_ID,mRtcUser.getUserId());
            obj.put(SDP, description);
            obj.put(CALLID,callId);

            if(socket.connected()) {
                socket.emit(SignalType.TOPIC_OUT_ANSWER.type, obj);
            } else{
                mCallback.onDisconnected();
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
            obj.put(CALLID,callId);
            obj.put(CALLER_ID,mRtcUser.getUserId());
            obj.put(SDP_MID, iceCandidate.sdpMid);
            obj.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
            obj.put(SDP, iceCandidate.sdp);

            if(socket.connected()) {
                socket.emit(SignalType.TOPIC_OUT_CANDIDATE.type, obj);
            } else{
                mCallback.onDisconnected();
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
            obj.put(CALLID,callId);
            obj.put(CALLER_ID,mRtcUser.getUserId());
            obj.put("type", type.toString());
            obj.put("reason", reason);

            if(socket.connected()) {
            socket.emit(SignalType.TOPIC_OUT_ENDCALL.type, obj);
            } else{
                mCallback.onDisconnected();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendRegister() {
        DLog.e("Send Register");
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_id", mRtcUser.getUserId());
            obj.put("user_name", mRtcUser.getUserName());
            obj.put("user_details", mRtcUser.toString());
            obj.put("device_id",mRtcDeviceInfo.getDeviceId());
            obj.put("device_loc", mRtcDeviceInfo.getDeviceLocation());
            obj.put("device_name", mRtcDeviceInfo.getDeviceName());
            if(socket.connected()) {
                socket.emit(SignalType.TOPIC_OUT_REGISTER.type, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCallback(ICallSignalingApi.ICallSignalingCallback callback){
        mCallback = callback;
    }
    private void runOnUIThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }


    // Incomming call handler...
    private void onRecvConnect(Object ...args ){
        DLog.e("Received onRecvConnect");
        sendRegister();
    }
    private void onRecvDisconnect(Object ...args ){
        DLog.e("Received onRecvDisconnect");
        if(mCallback != null){
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDisconnected();
                }
            });
        }
    }

    private void onRecvAns(Object ...args ){
        DLog.e("Received Answer");
        try {
            JSONObject obj =  new JSONObject(args[0].toString());
            final SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                    obj.getString(SDP));
            final String callId = obj.getString(CALLID);

            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedAnswer(callId, sdp);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void onRecvOffer(Object ... args){
        DLog.e("Received Offer");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER,
                    obj.getString(SDP));
            final String callId = obj.getString(CALLID);
            final String userinfo = obj.getString("userinfo");
            //TODO -- DIPANKAR
            final IRtcUser user = null;
            if(mCallback != null){
                mainUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedOffer(callId, sdp, user);
                    }
                });
                /*
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void onRecvIce(Object ... args){
        DLog.e("Received Ice");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final IceCandidate ice = new IceCandidate(obj.getString(SDP_MID),
                    obj.getInt(SDP_M_LINE_INDEX),
                    obj.getString(SDP));
            final String callId = obj.getString(CALLID);
            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedCandidate(callId, ice);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void onRecvEndCall(Object ... args) {
        DLog.e("Received EndCall");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final String type = obj.getString("type");
            final String reason = obj.getString("reason");
            final String callId = obj.getString(CALLID);
            if (mCallback != null) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedEndCall(callId, EndCallType.valueOf(type.toUpperCase()), reason);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void onRecvTest(Object ... args) {
        DLog.e("Received Test");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final String type = obj.getString("type");
            final String reason = obj.getString("reason");
            final String callId = obj.getString(CALLID);
            if (mCallback != null) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedEndCall(callId, EndCallType.valueOf(type.toUpperCase()), reason);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void onRecvInvalidPayload(Object ... args) {
        DLog.e("Received onRecvInvalidPayload");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final String type = obj.getString("type");
            final String reason = obj.getString("reason");
            final String callId = obj.getString(CALLID);
            if (mCallback != null) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedEndCall(callId, EndCallType.valueOf(type.toUpperCase()), reason);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void onRecvNoti(Object ... args) {
        DLog.e("Received onRecvInvalidPayload");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            NotificationType type = NotificationType.valueOf(obj.getString("type").toUpperCase());
            final String msg = obj.getString("msg");
            switch (type){
                case CONNECTED:
                    if(mCallback != null){
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onConnected();
                            }
                        });
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
