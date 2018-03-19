package in.co.dipankar.ping.contracts;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by dip on 3/9/18.
 */

public interface ICallSignalingApi {

    public enum SignalType {
         TOPIC_OUT_CONNECTION("connection"),
         TOPIC_OUT_REGISTER("register"),
         TOPIC_OUT_DISCONNECT("disconnect"),
         TOPIC_OUT_OFFER("offer"),
         TOPIC_OUT_CANDIDATE ("candidate"),
         TOPIC_OUT_ANSWER ("answer"),
         TOPIC_OUT_ENDCALL("endcall"),
         TOPIC_OUT_TEST("test"),
         TOPIC_OUT_NOTI("notification"),

         TOPIC_IN_TEST("test"),
         TOPIC_IN_OFFER("offer"),
         TOPIC_IN_CANDIDATE("candidate"),
         TOPIC_IN_ANSWER("answer"),
         TOPIC_IN_ENDCALL("endcall"),
         TOPIC_IN_INVALID_PAYLOAD("invalid_playload"),
         TOPIC_IN_NOTI("notification"),
        ;

        public final String type;
        SignalType(String type) {
            this.type = type;
        }
    };
            
    public enum NotificationType{
        CONNECTED("connected");
        public final String type;
        NotificationType(String type) {
            this.type = type;
        }
    }

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



    public interface ICallSignalingCallback{
        // connetion
        void onTryConnecting();
        void onConnected();
        void onDisconnected();
        //RTC
        void onReceivedOffer(String callId, SessionDescription sdp, IRtcUser rtcUser);
        void onReceivedAnswer(String callId, SessionDescription sdp);
        void onReceivedCandidate(String callId, IceCandidate ice);
        void onReceivedEndCall(String callID, EndCallType type, String reason);
    }
    void addCallback(ICallSignalingApi.ICallSignalingCallback callback);

}
