package in.peerreview.fmradioindia.common.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class Player implements IPlayer {

  // interface

  public interface IPlayerCallback {
    void onTryPlaying(String msg);

    void onSuccess(String msg);

    void onError(String msg);

    void onComplete(String msg);
  }

  // public functions
  public Player(IPlayerCallback playerCallback) {
    mPlayerCallback = playerCallback;
    init();
  }

  @Override
  public void stop() {
    if (mPlayer != null) {
      mPlayer.stop();
      mPlayer.reset();
      mPlayer = null;
    }
    s_playing = false;
  }

  @Override
  public boolean isPlaying() {
    return s_playing;
  }

  @Override
  public void pause() {
    if (s_playing) {
      mPlayer.stop();
    }
  }

  @Override
  public void resume() {
    if (s_playing) {
      mPlayer.start();
    }
  }

  @Override
  public void restart() {
    if (mUrl != null) {
      play(mTitle, mUrl);
    }
  }

  @Override
  public void play(final String title, final String url) {
    if (url == null) {
      mPlayerCallback.onError("Invalid URL passed");
      return;
    }
    mUrl = url;
    mTitle = title;
    mPlayerCallback.onTryPlaying("Trying to play " + title);

    stop();
    init();

    try {
      mPlayer.setDataSource(url);
      mPlayer.prepareAsync();
    } catch (final Exception e) {
      mPlayerCallback.onError("Not able to play because of:" + e.getMessage());
      stop();
      e.printStackTrace();
    } finally {

    }
    // if we ave an excpetion at this points let's return.
    if (mPlayer == null) return;

    mPlayer.setOnPreparedListener(
        new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer player) {
            mPlayer.start();
            mPlayerCallback.onSuccess("Now Playing " + title);
          }
        });
    mPlayer.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            mPlayerCallback.onComplete("");
          }
        });
    mPlayer.setOnErrorListener(
        new MediaPlayer.OnErrorListener() {
          public boolean onError(MediaPlayer mp, int what, final int extra) {
            mPlayerCallback.onError("MediaPlayer error happened");
            return true;
          }
        });
    s_playing = true;
  }

  // private
  private static MediaPlayer mPlayer;
  private IPlayerCallback mPlayerCallback;
  private static boolean s_playing = false;
  private static final String TAG = "MediaPlayerUtils";
  private String mUrl;
  private String mTitle;

  // private functions.
  private void init() {
    if (mPlayer == null) {
      mPlayer = new MediaPlayer();
      mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
  }
}
