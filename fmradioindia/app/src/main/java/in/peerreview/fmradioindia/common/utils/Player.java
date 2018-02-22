package in.peerreview.fmradioindia.common.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.IOException;
import java.util.HashMap;

public class Player implements IPlayer {

  // interface

  public interface IPlayerCallback {
    void onTryPlaying(String id);

    void onSuccess(String id);

    void onResume(String id);

    void onPause(String id);

    void onMusicInfo(HashMap<String, Object> info);

    void onSeekBarPossionUpdate(int total, int cur);

    void onError(String id);

    void onComplete(String id);
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
    return mPlayer != null && mPlayer.isPlaying();
  }

  @Override
  public void pause() {
    Log.d(TAG, "Pause Called");
    if (mPlayer != null && mPlayer.isPlaying()) {
      mPlayer.pause();
      mPlayerCallback.onPause(mTitle);
    }
  }

  @Override
  public void resume() {
    Log.d(TAG, "Resume Called");
    if (mPlayer != null && mPlayer.isPlaying() == false) {
      mPlayer.start();
      mPlayerCallback.onResume(mTitle);
    }
  }

  @Override
  public void restart() {
    Log.d(TAG, "Restart Called");
    if (mUrl != null) {
      play(mTitle, mUrl);
    }
  }

  @Override
  public void mute() {}

  @Override
  public void unmute() {}

  public void seekTo(int progress) {
    Log.d(TAG, "Pause Called");
    if (s_playing) {
      int msec = (int) (mTotalDuration * (progress / 100.0));
      mPlayer.seekTo(msec);
    }
  }

  @Override
  public void play(final String title, final String url) {
    Log.d(TAG, "Play Called");
    Thread backgroudThread =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  playInternal(title, url);
                } catch (final IOException e) {
                  new Handler(Looper.getMainLooper())
                      .post(
                          new Runnable() {
                            @Override
                            public void run() {
                              onError1(e.getMessage());
                            }
                          });
                }
              }
            });
    backgroudThread.start();
  }

  public void playInternal(final String title, final String url) throws IOException {
    if (url == null) {
      onError1("Invalid URL passed");
      return;
    }
    mUrl = url;
    mTitle = title;
    onTryPlaying(title);

    stop();
    init();

    try {
      mPlayer.setDataSource(url);
      mPlayer.prepareAsync();
    } catch (final Exception e) {
      onError1("Not able to play because of:" + e.getMessage());
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
            mTotalDuration = mPlayer.getDuration();
            onSuccess(title);
            onMusicInfo(
                new HashMap<String, Object>() {
                  {
                    put("CurrentPosition", mPlayer.getCurrentPosition());
                    put("Duration", mPlayer.getDuration());
                    put("count", "1");
                  }
                });
            // schedule the update timer
            mHandler.postDelayed(mUpdateTimeTask, 1000);
          }
        });
    mPlayer.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
            onComplete(title);
          }
        });
    mPlayer.setOnErrorListener(
        new MediaPlayer.OnErrorListener() {
          public boolean onError(MediaPlayer mp, int what, final int extra) {
            onError1("MediaPlayer error happened");
            return true;
          }
        });
    s_playing = true;
  }

  // private
  private static MediaPlayer mPlayer;
  private IPlayerCallback mPlayerCallback;
  private static boolean s_playing = false;
  private static final String TAG = "DIPANKAR :: Player ";
  private String mUrl;
  private String mTitle;
  private int mTotalDuration;
  private Handler mHandler = new Handler();;

  private void onError1(final String msg) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onError(msg);
              }
            });
  }

  private void onTryPlaying(final String msg) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onTryPlaying(msg);
              }
            });
  }

  private void onSuccess(final String msg) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onSuccess(msg);
              }
            });
  }

  private void onComplete(final String msg) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onComplete(msg);
              }
            });
  }

  private void onMusicInfo(final HashMap<String, Object> info) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onMusicInfo(info);
              }
            });
  }

  private void onSeekBarPossionUpdate(final int total, final int cur) {
    new Handler(Looper.getMainLooper())
        .post(
            new Runnable() {
              @Override
              public void run() {
                mPlayerCallback.onSeekBarPossionUpdate(total, cur);
              }
            });
  }

  private Runnable mUpdateTimeTask =
      new Runnable() {
        public void run() {
          if (mPlayer != null && mPlayer.isPlaying()) {
            int totalDuration = mPlayer.getDuration();
            int currentDuration = mPlayer.getCurrentPosition();
            onSeekBarPossionUpdate(totalDuration, currentDuration);
            mHandler.postDelayed(this, 1000);
          }
        }
      };

  // private functions.
  private void init() {
    if (mPlayer == null) {
      Log.d(TAG, "creating new instnace of MediaPlayer ");
      mPlayer = new MediaPlayer();
      mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
  }
}
