package in.co.dipankar.ping.contracts;

import java.io.Serializable;

public interface IRtcUser extends Serializable {
    String getDeviceID();
    String getProfilePictureUrl();
    String getUserId();
    String getUserName();
}
