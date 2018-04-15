package in.peerreview.ping.activities.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.INetwork;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.ping.common.model.ContactManger;
import in.peerreview.ping.common.signaling.SocketIOSignaling;
import in.peerreview.ping.common.webrtc.RtcDeviceInfo;
import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.ICallSignalingApi;
import in.peerreview.ping.contracts.IRtcDeviceInfo;
import in.peerreview.ping.contracts.IRtcUser;
import java.io.IOException;

public class PingApplication extends Application {

  private ContactManger mContactManger;
  private IRtcUser mSelfUser;
  private IRtcUser mPeerUser;
  private IRtcDeviceInfo mSelfDevice;
  private ICallInfo mCallInfo;

  private ICallSignalingApi mCallSignalingApi;
  private INetwork mNetwork;
  private boolean isNightModeEnabled = false;

  private static PingApplication sPingApplication;
  // Called when the application is starting, before any other application objects have been
  // created.
  // Overriding this method is totally optional!
  @Override
  public void onCreate() {
    super.onCreate();
    // Required initialization logic here!
    mContactManger = new ContactManger();
    SharedPrefsUtil.getInstance().init(this);
    sPingApplication = this;
    mNetwork = new Network();

    String deviceid =
        Settings.Secure.getString(
            getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    mSelfDevice = new RtcDeviceInfo(deviceid, android.os.Build.MODEL, "10");

    // Restoring User..
    Gson gson = new Gson();
    String json = SharedPrefsUtil.getInstance().getString("self_user_info", null);
    if (json == null) {
      mSelfUser = null;
    }
    mSelfUser = gson.fromJson(json, RtcUser.class);

    SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
    this.isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false);

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

  public ContactManger getUserManager() {
    return mContactManger;
  }

  public static PingApplication Get() {
    return sPingApplication;
  }

  public void setMe(IRtcUser user) {
    mSelfUser = user;
    Gson gson = new Gson();
    String json = gson.toJson(user);
    SharedPrefsUtil.getInstance().setString("self_user_info", json);
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

  public void setPeer(IRtcUser user) {
    mPeerUser = user;
  }

  boolean mNetworkConnection = false;

  public boolean hasNetworkConn() {
    return mNetworkConnection;
  }

  public void setNetworkConn(boolean networkConn) {
    mNetworkConnection = networkConn;
  }

  public void setCallSignalingApi(ICallSignalingApi api) {
    mCallSignalingApi = api;
  }

  public ICallSignalingApi getCallSignalingApi() {
    if (mCallSignalingApi == null) {
      mCallSignalingApi = new SocketIOSignaling(mSelfUser, mSelfDevice);
    }
    return mCallSignalingApi;
  }

  public boolean isOnCall() {
    return getPeer() != null;
  }

  public INetwork getNetwork() {
    return mNetwork;
  }

  @Nullable
  public ICallInfo getCurrentCallInfo() {
    return mCallInfo;
  }

  public void setCurrentCallInfo(ICallInfo callInfo) {
    mCallInfo = callInfo;
  }
  public boolean isNightModeEnabled() {
    return isNightModeEnabled;
  }
  public void setIsNightModeEnabled(boolean isNightModeEnabled) {
    this.isNightModeEnabled = isNightModeEnabled;
  }
}
