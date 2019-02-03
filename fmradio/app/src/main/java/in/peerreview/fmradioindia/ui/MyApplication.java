package in.peerreview.fmradioindia.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.applogic.StorageManager;

public class MyApplication extends Application {

  private static MyApplication mMyApplication;

  public static final String CHANNEL_ID = "MUSIC_NOTIFICATION_CHANNEL";

  public static Application Get() {
    return mMyApplication;
  }

  @Override
  public void onCreate() {
    mMyApplication = this;
    super.onCreate();
    createNotificationChannel();
    StorageManager.Get().init(this);
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
}
