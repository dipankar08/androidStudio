package in.co.dipankar.ping.common.model;

import in.co.dipankar.ping.contracts.ICallInfo;

public class CallInfo implements ICallInfo {

    private String mId;
    private CallType mType;
    private String mFrom;
    private String mTo;
    private String mDuration;
    private String mStartTime;
    private boolean mIsVideo;

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void setStartTime(String data) {

    }

    public void setDataUses(String mDataUses) {
        this.mDataUses = mDataUses;
    }

    public CallInfo(String mId, CallType mType, boolean mIsVideo, String mFrom, String mTo, String mDuration, String mStartTime, String mDataUses) {
        this.mId = mId;
        this.mType = mType;
        this.mFrom = mFrom;
        this.mTo = mTo;
        this.mDuration = mDuration;
        this.mStartTime = mStartTime;
        this.mDataUses = mDataUses;
        this.mIsVideo = mIsVideo;
    }

    private String mDataUses;

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public CallType getType() {
        return mType;
    }

    @Override
    public void setType(CallType calltype) {
        mType = calltype;
    }

    @Override
    public String getFrom() {
        return mFrom;
    }

    @Override
    public String getTo() {
        return mTo;
    }

    @Override
    public String getDuration() {
        return mDuration;
    }

    @Override
    public String getStartTime() {
        return mStartTime;
    }

    @Override
    public String getDataUses() {
        return mDataUses;
    }

    @Override
    public boolean getIsVideo() {
        return mIsVideo;
    }
}
