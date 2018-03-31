package in.co.dipankar.ping.contracts;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.List;

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
        TOPIC_IN_PRESENCE("presence"),
        TOPIC_IN_WELCOME ("welcome"),
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

    public enum PresenceType{
        ONLINE("online"),
        OFFLINE("offline");
        public final String type;
        PresenceType(String type) {
            this.type = type;
        }
    }


    public enum EndCallType {
        NORMAL_END("normal_end"),
         SELF_OFFLINE("self_offline"),
         SELF_REJECT("self_reject"),
         SELF_PICKUP("self_pickup"),
         SELF_BUSY("self_busy"),
         SELF_NOTPICKUP("self_notpickup"),
         PEER_OFFLINE("peer_offline"),
         PEER_REJECT("peer_reject"),
         PEER_PICKUP("peer_pickup"),
         PEER_BUSY("peer_busy"),
         PEER_NOTPICKUP("peer_notpickup"),
        ;

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
    void sendOffer(String peerId, String callID, Object description, boolean isVideoEnabled);
    void sendAnswer(String callID, Object description);
    void sendCandidate(String callId, IceCandidate iceCandidate);
    void sendEndCall(String callID, EndCallType type, String reason);

    public interface ICallSignalingCallback{
        // connetion
        void onTryConnecting();
        void onConnected();
        void onDisconnected();
        //RTC
        void onReceivedOffer(String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled);
        void onReceivedAnswer(String callId, SessionDescription sdp);
        void onReceivedCandidate(String callId, IceCandidate ice);
        void onReceivedEndCall(String callID, EndCallType type, String reason);
        void onPresenceChange(IRtcUser user, PresenceType type);
        void onWelcome(List<IRtcUser> liveUserList);
    }
    void addCallback(ICallSignalingApi.ICallSignalingCallback callback);

}
