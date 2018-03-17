package in.co.dipankar.ping.contracts;

import java.io.Serializable;

public interface IRtcUser extends Serializable {
    String getProfilePictureUrl();
    String getCoverPictureUrl();
    String getUserId();
    String getUserName();
}
