package in.peerreview.ping.common.subview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.home.HomeActivity;
import java.util.Random;

/** Created by dip on 4/20/18. */
public class NotificationBuilder {
  private static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL_ID";
  NotificationManager mNotificationManager;
  Context mContext;

  public NotificationBuilder(Context context) {
    mContext = context;
    mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    // Setting up Notification channels for android O and above
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      setupChannels();
    }
  }

  public void showIncommingCallNotification(String msg, String call_id) {
    RemoteViews remoteViews =
        new RemoteViews(mContext.getPackageName(), R.layout.notification_accept_reject);

    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    int notificationId = new Random().nextInt(60000);

    Uri defaultSoundUri =
        Uri.parse(
            "android.resource://"
                + mContext.getPackageName()
                + "/"
                + R.raw
                    .whatsapp_whistle); // Here is FILE_NAME is the name of file that you want to
                                        // play
    long[] vibrate = {0, 100, 200, 300};
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(mContext, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContent(remoteViews)
            .setSound(soundUri)
            .setVibrate(vibrate)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_VIBRATE);

    mNotificationManager =
        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(notificationId, notificationBuilder.build());

    Intent intent = new Intent(mContext, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra("type", "accept_call");
    intent.putExtra("call_id", call_id);
    PendingIntent pIntent =
        PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.accept, pIntent);

    Intent intent2 = new Intent(mContext, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra("type", "reject_call");
    intent.putExtra("call_id", call_id);
    PendingIntent pIntent2 =
        PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.reject, pIntent2);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void setupChannels() {
    CharSequence adminChannelName = "notifications_admin_channel_name";
    String adminChannelDescription = "notifications_admin_channel_description";
    NotificationChannel adminChannel;
    adminChannel =
        new NotificationChannel(
            ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
    adminChannel.setDescription(adminChannelDescription);
    adminChannel.enableLights(true);
    adminChannel.setLightColor(Color.RED);
    adminChannel.enableVibration(true);
    if (mNotificationManager != null) {
      mNotificationManager.createNotificationChannel(adminChannel);
    }
  }
}
