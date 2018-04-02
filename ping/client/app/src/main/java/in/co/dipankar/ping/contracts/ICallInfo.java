package in.co.dipankar.ping.contracts;


import java.util.Map;

public interface ICallInfo {
    String getId();
    CallType getType();
    ShareType getShareType();
    void setType(CallType calltype);
    String getFrom();
    String getTo();

    String getDuration();
    String getStartTime();
    String getDataUses();

    void setDuration(String data);
    void setStartTime(String data);
    void setDataUses(String data);
    String getExtra(String key);

    boolean getIsVideo();

    public enum CallType{
        MISS_CALL_INCOMMING("miss_call"),
        MISS_CALL_OUTGOING("miss_call"),
        OUTGOING_CALL("miss_call"),
        INCOMMING_CALL("miss_call"),
        ;

        public String mType;
        CallType(String type) {
            mType = type;
        }
    }

    public enum ShareType{
        AUDIO_CALL("audio_call"),
        VIDEO_CALL("video_call"),
        SCREEN_SHARE("screen_share"),
        AUDIO_SHARE("audio_share"),
        VIDEO_SHARE("video_share"),
        ;

        public String mType;
        ShareType(String type) {
            mType = type;
        }
    }
}
