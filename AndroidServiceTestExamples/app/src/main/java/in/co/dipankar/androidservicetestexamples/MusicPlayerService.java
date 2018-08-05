package in.co.dipankar.androidservicetestexamples;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener {

  MediaPlayer mediaPlayer;
  WifiManager.WifiLock wifiLock;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {

    mediaPlayer = null;
    String url = "http://www.myserver.com/mystream.mp3";
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnPreparedListener(this);
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

    wifiLock =
        ((WifiManager) this.getSystemService(Context.WIFI_SERVICE))
            .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

    wifiLock.acquire();

    try {
      mediaPlayer.setDataSource(url);
      mediaPlayer.prepareAsync();
      // it takes so long, about a minute...

    } catch (IllegalArgumentException
        | SecurityException
        | IllegalStateException
        | IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  /** Called when MediaPlayer is ready */
  @Override
  public void onPrepared(MediaPlayer player) {
    mediaPlayer.start();
    // radio0.tabBridge.setStopView();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  public void onDestroy() {
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      mediaPlayer.release();
    }
    wifiLock.release();
  }

  public void onCompletion(MediaPlayer _mediaPlayer) {
    stopSelf();
  }
}
