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

    @Override
    public void connect() {
        socket.connect();
        DLog.e("Send Connecting");
    }

    @Override
    public void disconnect() {
        socket.disconnect();
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
            socket.emit(OFFER, obj);
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
            socket.emit(ANSWER, obj);

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
            socket.emit(CANDIDATE, obj);
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
            socket.emit(ENDCALL, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRegister(IRtcUser user, IRtcDeviceInfo device) {
        mRtcUser = user;
        mRtcDeviceInfo = device;
        DLog.e("Send Register");
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_id", mRtcUser.getUserId());
            obj.put("user_name", mRtcUser.getUserName());
            obj.put("user_details", mRtcUser.toString());
            obj.put("device_id",mRtcDeviceInfo.getDeviceId());
            obj.put("device_loc", mRtcDeviceInfo.getDeviceLocation());
            obj.put("device_name", mRtcDeviceInfo.getDeviceName());
            socket.emit(REGISTER, obj);
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
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedOffer(callId, sdp, user);
                    }
                });
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

    void onRecvEndCall(Object ... args){
        DLog.e("Received EndCall");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final String type = obj.getString("type");
            final String reason = obj.getString("reason");
            final String callId = obj.getString(CALLID);
            if(mCallback != null){
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

    void init(){
            socket.on(OFFER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onRecvOffer(args);
                }
            }).on(ANSWER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onRecvAns(args);
                }

            }).on(CANDIDATE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onRecvIce(args);
                }
            }).on(ENDCALL, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onRecvEndCall(args);
                }
            });
    }

    public SocketIOSignaling() {
        if(socket == null){
            try {
                socket = IO.socket(Configuration.SIGNALING_ENDPOINT);
                init();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private static  Socket socket;

}
