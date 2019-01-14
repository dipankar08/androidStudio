package in.peerreview.fmradioindia.applogic;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.concurrent.TimeUnit;

public class StatManager {
  private long mPlayTime = 0;
  private long mPlayCount = 0;
  private long mStartTime = 0;

  public StatManager() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Get());
    mPlayTime = sp.getLong("TimeSpent", mPlayTime);
  }

  private void storePlayTime(long time) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Get());
    SharedPreferences.Editor editor = sp.edit();
    mPlayTime += time;
    editor.putLong("TimeSpent", mPlayTime);
    editor.apply();
  }

  public String getPlayTime() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Get());
    long time_ns = sp.getLong("TimeSpent", mPlayTime);
    StringBuilder stringBuilder = new StringBuilder();
    long time_min = TimeUnit.NANOSECONDS.toMinutes(time_ns);
    long time_hour = TimeUnit.NANOSECONDS.toHours(time_ns);
    if (time_hour > 0) stringBuilder.append(time_hour + " Hours ");
    if (time_min > 0) stringBuilder.append(time_min % 60 + " Minutes");
    return stringBuilder.toString().length() == 0 ? "0" : stringBuilder.toString();
  }

  public void storePlayCount(long time) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Get());
    SharedPreferences.Editor editor = sp.edit();
    mPlayCount += time;
    editor.putLong("PlayCount", mPlayCount);
    editor.apply();
  }

  public long getPlayCount() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Get());
    return sp.getLong("PlayCount", mPlayCount);
  }

  public void onStartPlaying() {
    if (mStartTime != 0) {
      onStopPlaying();
    }
    mStartTime = System.nanoTime();
  }

  public void onStopPlaying() {
    storePlayTime(System.nanoTime() - mStartTime);
    mStartTime = 0;
  }
}
