package in.peerreview.fmradioindia.ui;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.di.DaggerAppComponent;

public class MyApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingInjector;
  public static final String CHANNEL_ID = "MUSIC_NOTIFICATION_CHANNEL";

  @Override
  public void onCreate() {
    super.onCreate();
    createNotificationChannel();
    initializeComponent();
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
    private void initializeComponent() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingInjector;
    }
}
