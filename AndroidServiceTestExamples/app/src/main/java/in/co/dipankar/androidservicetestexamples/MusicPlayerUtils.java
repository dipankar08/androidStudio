package in.co.dipankar.androidservicetestexamples;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.IOException;

public class MusicPlayerUtils {

  private MediaPlayer mediaPlayer;
  private boolean isPause = false;

  public MusicPlayerUtils() {
    reset();
  }

  public boolean isPlaying() {
    return mediaPlayer.isPlaying();
  }

  public boolean isPause() {
    return isPause;
  }

  public boolean play(String Id, String url) {
    isPause = false;
    if (mediaPlayer != null) {
      reset();
    }
    try {
      mediaPlayer.setDataSource(url); // "http://www.largesound.com/ashborytour/sound/brobob.mp3");
    } catch (IOException e) {
      e.printStackTrace();
    }
    mediaPlayer.prepareAsync();
    mediaPlayer.setOnPreparedListener(
        new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
            mp.start();
            Log.e("DIPANKAR", "onPrepared called");
          }
        });
    return true;
  }

  public boolean pause() {
    if (mediaPlayer == null) return false;
    mediaPlayer.pause();
    isPause = true;
    return true;
  }

  public boolean stop() {
    if (mediaPlayer == null) return false;
    mediaPlayer.stop();
    isPause = false;
    return true;
  }

  public boolean resume() {
    if (mediaPlayer == null) return false;
    mediaPlayer.start();
    isPause = false;
    return true;
  }

  private synchronized void reset() {
    isPause = false;
    if (mediaPlayer != null) {
      stop();
      mediaPlayer.release();
      ;
      mediaPlayer = null;
    }
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    mediaPlayer.setOnErrorListener(
        new MediaPlayer.OnErrorListener() {
          @Override
          public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("DIPANKAR", "MusicPlayerUtils::onError called");
            return false;
          }
        });
    mediaPlayer.setOnInfoListener(
        new MediaPlayer.OnInfoListener() {
          @Override
          public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.e("DIPANKAR", "MusicPlayerUtils::onInfo called");
            return false;
          }
        });
  }
}
