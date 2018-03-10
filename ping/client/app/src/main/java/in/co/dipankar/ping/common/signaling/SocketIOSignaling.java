package in.co.dipankar.ping.common.signaling;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;

import in.co.dipankar.ping.contracts.ICallSignalingApi;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOSignaling implements ICallSignalingApi {

    private static final String SDP = "sdp";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";
    private static final String SIGNALING_URI = "http://192.168.1.106:7000";

    private ICallSignalingApi.ICallSignalingCallback mCallback;

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
    public void sendOffer(Object description) {
        Log.e("DIP111", "Send Offer");
        try {
            JSONObject obj = new JSONObject();
            obj.put(SDP,description);
            socket.emit(OFFER, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswer(Object description) {
        Log.e("DIP111", "Send Answer");
        try {
            JSONObject obj = new JSONObject();
            obj.put(SDP, description);
            socket.emit(ANSWER, obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCandidate(IceCandidate iceCandidate) {
        Log.e("DIP111", "Send Caldidate");
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
    public void ping(String userid, String username, String deviceid) {
        Log.e("DIP111", "Send Ping");
        try {
            JSONObject obj = new JSONObject();
            obj.put("user_name", username);
            obj.put("user_id", userid);
            obj.put("device_id", deviceid);
            socket.emit(OUTGOING_PING, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCallback(ICallSignalingApi.ICallSignalingCallback callback){
        mCallback = callback;
    }

    private void handleIncomingPing(Object ... args){
        Log.e("DIP111", "Recv Ping");
        try {
            JSONObject obj = new JSONObject(args[0].toString());
            Log.e("DIP111", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onRecvAns(Object ...args ){
        Log.e("DIP111","Receved Ans  Call");
        try {
            JSONObject obj =  new JSONObject(args[0].toString());
            final SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                    obj.getString(SDP));

            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedAnswer(sdp);
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
            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedOffer(sdp);
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
            if(mCallback != null){
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onReceivedCandidate(ice);
                    }
                });
            }
            //peerConnection.setRemoteDescription(sdpObserver, sdp);
            // DONOT accept ATOTOCALTICAALLY...
            //peerConnection.createAnswer(sdpObserver, new MediaConstraints());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void init(){
            socket.on(INCOMMING_PING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handleIncomingPing(args);
                }
            }).on(OFFER, new Emitter.Listener() {
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
