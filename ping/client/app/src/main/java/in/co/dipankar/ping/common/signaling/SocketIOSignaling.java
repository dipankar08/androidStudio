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
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
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
    private static final String SIGNALING_URI = "http://192.168.1.106:7000";

    private ICallSignalingApi.ICallSignalingCallback mCallback;

    private IRtcUser mRtcUser;
    private IRtcDeviceInfo mRtcDeviceInfo;

    @Override
    public void connect() {
        socket.connect();
        Log.e("DIP111", "Connecting ... ");
    }

    @Override
    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public void sendOffer(String peerID, String callId, Object description) {
        Log.e("DIP111", "Send Offer");
        try {
            JSONObject obj = new JSONObject();
            obj.put(PEER_ID,peerID);
            obj.put(CALLER_ID,mRtcUser.getUserId());
            obj.put(SDP,description);
            obj.put(CALLID,mRtcUser.getUserId());
            socket.emit(OFFER, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswer(String callId, Object description) {
        Log.e("DIP111", "Send Answer");
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
        Log.e("DIP111", "--> sendCandidate");
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
        Log.e("DIP111", "--> SendEndCall");
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
        Log.e("DIP111",  "--> sendRegister");
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_id", mRtcUser.getUserId());
            obj.put("user_name", mRtcUser.getUserName());
            obj.put("user_details", mRtcUser.toString());
            obj.put("devjce_id",mRtcDeviceInfo.getDeviceId());
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


    private void onRecvAns(Object ...args ){
        Log.e("DIP111","Receved Ans  Call");
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

    private void runOnUIThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }


    void onRecvOffer(Object ... args){
        Log.d("DIP111"," SHOW INCOMMING RING UI");
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
        Log.d("DIP111"," SHOW INCOMMING RING UI");
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
        Log.d("DIP111"," SHOW INCOMMING ENd call ");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            final String type = obj.getString("type");
            final String reason = obj.getString("reason");
            final String callId = obj.getString(CALLID);
            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedEndCall(callId, EndCallType.getByString(type), reason);
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
                socket = IO.socket(SIGNALING_URI);
                init();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }



    private static  Socket socket;
    private final String TAG = "DIPANKAR";

}
