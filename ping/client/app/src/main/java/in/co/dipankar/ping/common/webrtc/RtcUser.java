package in.co.dipankar.ping.common.webrtc;

import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/11/18.
 */

public class RtcUser implements IRtcUser {

    String name, id, deviceID,profilePictureUrl;

    @Override
    public String getDeviceID() {
        return deviceID;
    }

    public RtcUser(String name, String id, String deviceID, String profilePictureUrl) {
        this.name = name;
        this.id = id;
        this.deviceID = deviceID;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public String getUserName() {
        return name;
    }
}
