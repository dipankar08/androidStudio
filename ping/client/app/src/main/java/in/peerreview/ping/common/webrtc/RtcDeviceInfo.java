package in.peerreview.ping.common.webrtc;

import in.peerreview.ping.contracts.IRtcDeviceInfo;

/** Created by dip on 3/11/18. */
public class RtcDeviceInfo implements IRtcDeviceInfo {

  String id, name, location;

  public RtcDeviceInfo(String id, String name, String location) {
    this.id = id;
    this.name = name;
    this.location = location;
  }

  @Override
  public String getDeviceId() {
    return id;
  }

  @Override
  public String getDeviceLocation() {
    return location;
  }

  @Override
  public String getDeviceName() {
    return name;
  }
}
