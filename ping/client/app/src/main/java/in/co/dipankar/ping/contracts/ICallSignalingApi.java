package in.co.dipankar.ping.contracts;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by dip on 3/9/18.
 */

public interface ICallSignalingApi {

    // RTC Related
    public static final String OFFER = "offer";
    public static final String ANSWER = "answer";
    public static final String CANDIDATE = "candidate";

    // user API
    public static final String OUTGOING_PING = "pingsss";
    public static final String INCOMMING_PING= "onlineusers";


    //basic
    void connect();
    void disconnect();

    //RTC API
    void sendOffer(Object description);
    void sendAnswer(Object description);
    void sendCandidate(IceCandidate iceCandidate);

    //user API
    void ping(String userid, String username, String deviceid);


    public interface ICallSignalingCallback{
        //RTC
        void onReceivedOffer(SessionDescription sdp);
        void onReceivedAnswer(SessionDescription sdp);
        void onReceivedCandidate(IceCandidate ice);
    }
    void addCallback(ICallSignalingApi.ICallSignalingCallback callback);

}
