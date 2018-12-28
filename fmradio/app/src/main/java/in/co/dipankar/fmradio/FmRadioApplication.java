package in.co.dipankar.fmradio;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.facebook.FacebookSdk;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.service.MusicControllerUtils;
import in.co.dipankar.fmradio.ui.viewpresenter.adds.GoogleAdsUtils;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;

public class FmRadioApplication extends Application {

  public static final String CHANNEL_ID = "MUSIC_NOTIFICATION_CHANNEL";
  private static FmRadioApplication sApplication;
  private RadioManager radioManager;
  private ConfigurationManager configurationManager;
  private MusicControllerUtils musicControllerUtils;
  private GoogleAdsUtils mGoogleAdsUtils;

  @Override
  public void onCreate() {
    super.onCreate();
    sApplication = this;
    FacebookSdk.setApplicationId("123");
    FacebookSdk.sdkInitialize(getApplicationContext());
    createNotificationChannel();
    SharedPrefsUtil.getInstance().init(this);
  }

  public static FmRadioApplication Get() {
    return sApplication;
  }

  public synchronized RadioManager getRadioManager() {
    if (radioManager == null) {
      radioManager = new RadioManager(getApplicationContext());
    }
    return radioManager;
  }

  private void createNotificationChannel() {
    DLog.d("Crearing Notification Channel");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel =
          new NotificationChannel(
              CHANNEL_ID, "Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);
    }
  }

  public MusicControllerUtils getMusicController() {
    if (musicControllerUtils == null) {
      musicControllerUtils = new MusicControllerUtils(this);
    }
    return musicControllerUtils;
  }

  public ConfigurationManager getConfigurationManager() {
    if (configurationManager == null) {
      configurationManager = new ConfigurationManager();
    }
    return configurationManager;
  }
}