package in.co.dipankar.ping.activities.application;

import android.app.Application;
import android.content.res.Configuration;

import in.co.dipankar.ping.common.model.ContactManger;
import in.co.dipankar.ping.common.signaling.SocketIOSignaling;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;

public class PingApplication extends Application {

    private ContactManger mContactManger;
    private IRtcUser mSelfUser;
    private IRtcUser mPeerUser;
    private IRtcDeviceInfo mSelfDevice;

    private ICallSignalingApi mCallSignalingApi;


    private static PingApplication sPingApplication;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        mContactManger = new ContactManger();
        SharedPrefsUtil.getInstance().init(this);
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

    public ContactManger getUserManager(){
        return mContactManger;
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

    boolean mNetworkConnection  = false;
    public boolean hasNetworkConn() {
        return mNetworkConnection;
    }
    public void setNetworkConn(boolean networkConn){
        mNetworkConnection = networkConn;
    }

    public void setCallSignalingApi(ICallSignalingApi api) {
        mCallSignalingApi = api;
    }
    public ICallSignalingApi getCallSignalingApi() {
        if(mCallSignalingApi == null){
            mCallSignalingApi = new SocketIOSignaling(mSelfUser,mSelfDevice);
        }
        return mCallSignalingApi;
    }

    public boolean isOnCall(){
        return getPeer() != null;
    }
}