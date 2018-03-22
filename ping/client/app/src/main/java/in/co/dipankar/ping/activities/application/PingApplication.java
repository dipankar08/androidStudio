package in.co.dipankar.ping.activities.application;

import android.app.Application;
import android.content.res.Configuration;

import in.co.dipankar.ping.common.model.UserManager;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/16/18.
 */

public class PingApplication extends Application {

    private UserManager mUserManager;
    private IRtcUser mSelfUser;
    private IRtcUser mPeerUser;
    private IRtcDeviceInfo mSelfDevice;

    private static PingApplication sPingApplication;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        mUserManager = new UserManager();
        sPingApplication = this;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public UserManager getUserManager(){
        return mUserManager;
    }

    public static PingApplication Get(){
        return sPingApplication;
    }

    public void setMe(IRtcUser user) {
        mSelfUser = user;
    }
    public IRtcUser getMe() {
        return mSelfUser;
    }

    public void setDevice(IRtcDeviceInfo device) {
        this.mSelfDevice = device;
    }

    public IRtcDeviceInfo getDevice() {
        return mSelfDevice;
    }

    public IRtcUser getPeer() {
        return mPeerUser;
    }
    public void setPeer(IRtcUser  user) {
         mPeerUser = user;
    }
}