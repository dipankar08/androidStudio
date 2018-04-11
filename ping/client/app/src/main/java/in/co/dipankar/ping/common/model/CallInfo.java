package in.co.dipankar.ping.common.model;

import in.co.dipankar.ping.contracts.ICallInfo;
import java.util.HashMap;
import java.util.Map;

public class CallInfo implements ICallInfo {

  private String mId;
  private ShareType mShareType;
  private CallType mType;
  private String mFrom;
  private String mTo;
  private String mDuration;
  private String mStartTime;
  private Map<String, String> mExtra;

  public void setDuration(String mDuration) {
    this.mDuration = mDuration;
  }

  @Override
  public void setStartTime(String data) {}

  public void setDataUses(String mDataUses) {
    this.mDataUses = mDataUses;
  }

  @Override
  public String getExtra(String key) {
    return mExtra.get(key);
  }

  @Override
  public boolean getIsVideo() {
    return (mShareType == ShareType.VIDEO_SHARE
        || mShareType == ShareType.VIDEO_CALL
        || mShareType == ShareType.SCREEN_SHARE);
  }

  public CallInfo(
      String mId,
      ShareType shareType,
      CallType mType,
      String mFrom,
      String mTo,
      String mDuration,
      String mStartTime,
      String mDataUses) {
    this.mId = mId;
    this.mShareType = shareType;
    this.mType = mType;
    this.mFrom = mFrom;
    this.mTo = mTo;
    this.mDuration = mDuration;
    this.mStartTime = mStartTime;
    this.mDataUses = mDataUses;
    this.mExtra = new HashMap<>();
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
  public ShareType getShareType() {
    return mShareType;
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

  public void addExtra(String key, String value) {
    mExtra.put(key, value);
  }
}
