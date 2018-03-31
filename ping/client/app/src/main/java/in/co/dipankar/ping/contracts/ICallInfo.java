package in.co.dipankar.ping.contracts;



public interface ICallInfo {
    String getId();
    CallType getType();
    void setType(CallType calltype);
    String getFrom();
    String getTo();

    String getDuration();
    String getStartTime();
    String getDataUses();

    void setDuration(String data);
    void setStartTime(String data);
    void setDataUses(String data);

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
}
