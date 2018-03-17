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


    public enum EndCallType {
        OFFLINE("offline"),
        BUSY ("busy"),
        USER_REJECT ("user_reject"),
        OTHER ("other"),
        SERVER_ERROR("server_error"),
        RECEIVED_BY_OTHER_ENDPOINT("received_by_other_endpoint"),
        REJECT_BY_OTHER_ENDPOINT("reject_by_other_endpoint"),
        NORMAL("normal");


        private final String name;

        EndCallType(String s) {
            name = s;
        }
        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }

        public static EndCallType getByString(String name){
            for(EndCallType prop : values()){
                if(prop.toString().equals(name)){
                    return prop;
                }
            }
            throw new IllegalArgumentException(name + " is not a valid PropName");
        }
    }

    //basic
    void connect();
    void disconnect();

    //RTC API
    void sendOffer(String peerId, String callID, Object description);
    void sendAnswer(String callID, Object description);
    void sendCandidate(String callId, IceCandidate iceCandidate);
    void sendEndCall(String callID, EndCallType type, String reason);
    void sendRegister(IRtcUser user, IRtcDeviceInfo info);



    public interface ICallSignalingCallback{
        //RTC
        void onReceivedOffer(String callId, SessionDescription sdp, IRtcUser rtcUser);
        void onReceivedAnswer(String callId, SessionDescription sdp);
        void onReceivedCandidate(String callId, IceCandidate ice);
        void onReceivedEndCall(String callID, EndCallType type, String reason);
    }
    void addCallback(ICallSignalingApi.ICallSignalingCallback callback);

}
