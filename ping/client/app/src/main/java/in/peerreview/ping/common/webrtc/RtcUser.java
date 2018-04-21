package in.peerreview.ping.common.webrtc;

import in.peerreview.ping.contracts.IRtcUser;

/** Created by dip on 3/11/18. */
public class RtcUser implements IRtcUser {

  public String name, id, profilePictureUrl, mCoverPrctureUrl;
  public boolean mOnline;

  public RtcUser(String name, String id, String profilePictureUrl, String coverPictureUrl) {
    this.name = name;
    this.id = id;
    this.profilePictureUrl = profilePictureUrl;
    this.mCoverPrctureUrl = coverPictureUrl;
    this.mOnline = false;
  }

  @Override
  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  @Override
  public String getCoverPictureUrl() {
    return mCoverPrctureUrl;
  }

  @Override
  public String getUserId() {
    return id;
  }

  @Override
  public String getUserName() {
    return name;
  }

  @Override
  public boolean isOnline() {
    return mOnline;
  }

  @Override
  public void setOnline(boolean b) {
    mOnline = b;
  }
}
