package in.peerreview.ping.activities.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.INetwork;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.ping.common.model.ContactManger;
import in.peerreview.ping.common.signaling.SocketIOSignaling;
import in.peerreview.ping.common.subview.NotificationBuilder;
import in.peerreview.ping.common.webrtc.RtcDeviceInfo;
import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.ICallSignalingApi;
import in.peerreview.ping.contracts.IRtcDeviceInfo;
import in.peerreview.ping.contracts.IRtcUser;
import io.paperdb.Paper;

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
  private NotificationBuilder mNotificationBuilder;
  // Called when the application is starting, before any other application objects have been
  // created.
  // Overriding this method is totally optional!
  @Override
  public void onCreate() {
    super.onCreate();
    sPingApplication = this;
  }

  private boolean isInit = false;

  public void init() {
    if (isInit) {
      return;
    }

    SharedPrefsUtil.getInstance().init(this);
    ActiveAndroid.initialize(this);
    Paper.init(this);

    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    this.isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false);

    isInit = true;
    DLog.e("App Initilization successfully ");
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
    if (mContactManger == null) {
      mContactManger = new ContactManger();
    }
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
    if (mSelfUser == null) {
      Gson gson = new Gson();
      String json = SharedPrefsUtil.getInstance().getString("self_user_info", null);
      if (json == null) {
        return null;
      }
      try {
        mSelfUser = gson.fromJson(json, RtcUser.class);
      } catch (Exception e) {
      }
    }
    return mSelfUser;
  }

  public IRtcDeviceInfo getDevice() {
    if (mSelfDevice == null) {
      String deviceid =
          Settings.Secure.getString(
              getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
      mSelfDevice = new RtcDeviceInfo(deviceid, android.os.Build.MODEL, "10");
    }
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

  public ICallSignalingApi getCallSignalingApi() {
    if (mCallSignalingApi == null) {
      mCallSignalingApi = new SocketIOSignaling(getMe(), getDevice());
    }
    return mCallSignalingApi;
  }

  public boolean isOnCall() {
    return getPeer() != null;
  }

  public INetwork getNetwork() {
    if (mNetwork == null) {
      mNetwork = new Network();
    }
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

  public NotificationBuilder getNotificationBuilder() {
    if (mNotificationBuilder == null) {
      mNotificationBuilder = new NotificationBuilder(this);
    }
    return mNotificationBuilder;
  }
}
