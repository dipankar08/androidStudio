package in.peerreview.ping.common.utils;

import android.net.TrafficStats;
import android.os.Handler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.dipankar.quickandorid.utils.DLog;

/** Created by dip on 4/1/18. */
public class DataUsesReporter {

  public HashMap<String, String> getInfo() {
    return new HashMap<String, String>() {
      {
        put("time", mTime+"");
        put("data", getFileSize(mPrevRX + mPrevTX - mStartRX - mStartTX));
      }
    };
  }

  public interface Callback {
    void onUpdate(int time, long TxNow, long RxNow);

    void onFinish(int time, long TxTotal, long RxTotal);
  }

  private List<Callback> mCallbackList;
  private Handler mHandler = new Handler();
  private int mTime = 0;
  private long mStartRX, mStartTX, mPrevRX, mPrevTX;
  private boolean mStart;

  public DataUsesReporter() {
    mCallbackList = new ArrayList<>();
    mStartRX = mPrevRX = TrafficStats.getTotalRxBytes();
    mStartTX = mPrevTX = TrafficStats.getTotalTxBytes();
    if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
      return;
    }
  }

  public synchronized void start() {
    mStart = true;
    mStartRX  =mPrevRX = TrafficStats.getTotalRxBytes();
    mStartTX  =mPrevTX=  TrafficStats.getTotalTxBytes();
    mHandler.postDelayed(mRunnable, 1000);
  }

  public void stop() {
    long nowrx = TrafficStats.getTotalRxBytes();
    long nowtx = TrafficStats.getTotalTxBytes();
    for (Callback cb : mCallbackList) {
      cb.onFinish(mTime, (nowrx - mPrevRX)/1024, (nowtx - mPrevTX)/1024);
    }
    mStart = false;
  }

  private final Runnable mRunnable =
      new Runnable() {
        public void run() {
          if (mStart) {
            mTime++;
            long rxNow = TrafficStats.getTotalRxBytes();
            long txNow = TrafficStats.getTotalTxBytes();

            for (Callback cb : mCallbackList) {
              cb.onUpdate(mTime, (txNow - mPrevTX)/1024, (rxNow - mPrevRX)/1024);
            }
            mPrevRX = rxNow;
            mPrevTX = txNow;
            if (mStart) {
              mHandler.postDelayed(mRunnable, 1000);
            }
          }
        }
      };

  public void addCallback(Callback callback) {
    mCallbackList.add(callback);
  }

  public void removeCallback(Callback callback) {
    for (Callback cb : mCallbackList) {
      if (cb == callback) {
        mCallbackList.remove(callback);
      }
    }
  }

  public static String getFileSize(long size) {
    if (size <= 0)
      return "0";
    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }
  public void finalize() {
    DLog.d("delete Report");
  }
}
