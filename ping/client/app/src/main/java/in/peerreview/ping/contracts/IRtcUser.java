package in.peerreview.ping.contracts;

import java.io.Serializable;

public interface IRtcUser extends Serializable {
  String getProfilePictureUrl();

  String getCoverPictureUrl();

  String getUserId();

  String getUserName();

  boolean isOnline();

  void setOnline(boolean b);
}
