package in.co.dipankar.androidservicetestexamples;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import static in.co.dipankar.androidservicetestexamples.TestApplication.CHANNEL_ID;
public class ForegroundPlayerService  extends Service{
    private static final String LOG_TAG = "DIPANKAR08";
    private MusicPlayerUtils mMusicPlayerUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicPlayerUtils = new MusicPlayerUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction() == null){
            return START_STICKY;
        }
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            Log.i(LOG_TAG, "Clicked Previous");
            mMusicPlayerUtils.play("0","http://www.largesound.com/ashborytour/sound/brobob.mp3");
        }
        else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION))
        {
            Log.i(LOG_TAG, "Clicked Previous");
            mMusicPlayerUtils.play("0","http://www.largesound.com/ashborytour/sound/brobob.mp3");
        }

        else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION))
        {
            Log.i(LOG_TAG, "Clicked Play");
            if(mMusicPlayerUtils.isPlaying()){
                mMusicPlayerUtils.pause();
            }
            if(mMusicPlayerUtils.isPause()){
                mMusicPlayerUtils.resume();
            }
        }

        else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION))
        {
            Log.i(LOG_TAG, "Clicked Next");
            mMusicPlayerUtils.play("0","http://www.largesound.com/ashborytour/sound/brobob.mp3");
        }

        else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION) ||
                intent.getAction().equals(Constants.ACTION.STOP_ACTION))
        {
            mMusicPlayerUtils.stop();
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, getNotification(intent.getAction()));
        return START_STICKY;
    }

    private Notification getNotification(String type) {
        Log.i(LOG_TAG, "Received Start Foreground Intent ");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, ForegroundPlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, ForegroundPlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, ForegroundPlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent stopIntent = new Intent(this, ForegroundPlayerService.class);
        nextIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent pstopIntent = PendingIntent.getService(this, 0,
                stopIntent, 0);


        Intent pauseIntent = new Intent(this, ForegroundPlayerService.class);
        nextIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);


        Notification.Builder notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notification = new Notification.Builder(this);
        }
        notification
                .setContentTitle("DAM Music Player")
            .setTicker("DAM Music Player")
            .setContentText("My Music")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
            .addAction(android.R.drawable.ic_media_rew, "Stop", pstopIntent);
        return notification.build();
    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // Used only in case of bound services.
        return null;
    }



}