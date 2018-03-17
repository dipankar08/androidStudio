package in.co.dipankar.ping.common.webrtc;

import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/11/18.
 */

public class RtcUser implements IRtcUser {

    String name, id,profilePictureUrl, mCoverPrctureUrl;

    public RtcUser(String name, String id, String profilePictureUrl,String coverPictureUrl) {
        this.name = name;
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
        this.mCoverPrctureUrl = coverPictureUrl;
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
}
