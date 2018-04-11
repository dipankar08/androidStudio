package in.peerreview.ping.activities.pushnotification;

/** Created by dip on 4/4/18. */
public interface IPushNotification {
  public enum FcmType {
    ONLINE("online"),
    CALL("call"),
    MESSEGE("messege");

    public String mType;

    FcmType(String type) {
      mType = type;
    }
  };
}
