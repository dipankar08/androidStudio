package in.co.dipankar.ping.common.webrtc;

import android.app.AlertDialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import in.co.dipankar.ping.R;

/** Created by dip on 3/24/18. */
public class RtcStatView extends RelativeLayout {

  public interface Callback {}

  private RtcStatView.Callback mCallback;
  LayoutInflater mInflater;

  TextView total, now, mTimeView;
  private Handler mHandler = new Handler();
  private int mTime = 0;
  private long mStartRX, mStartTX, mPrevRX, mPrevTX;

  public void setCallback(RtcStatView.Callback callback) {
    mCallback = callback;
  }

  public RtcStatView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public RtcStatView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public RtcStatView(Context context) {
    super(context);
    initView(context);
  }

  public void reset() {
    mTime = 0;
    mStartRX = 0;
    mStartTX = 0;
    mPrevRX = 0;
    mPrevTX = 0;
  }

  private void initView(Context context) {
    mInflater = LayoutInflater.from(context);
    View v = mInflater.inflate(R.layout.view_rtc_stat, this, true);
    mTimeView = findViewById(R.id.time);
    now = findViewById(R.id.now);
    total = findViewById(R.id.total);

    mStartRX = mPrevRX = TrafficStats.getTotalRxBytes();
    mStartTX = mPrevTX = TrafficStats.getTotalTxBytes();
    if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
      AlertDialog.Builder alert = new AlertDialog.Builder(context);
      alert.setTitle("Uh Oh!");
      alert.setMessage("Your device does not support traffic stat monitoring.");
      alert.show();
    } else {
      mHandler.postDelayed(mRunnable, 1000);
    }
  }

  private final Runnable mRunnable =
      new Runnable() {
        public void run() {
          mTime++;

          long rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX) / 1024;
          long txBytes = (TrafficStats.getTotalTxBytes() - mStartTX) / 1024;

          mTimeView.setText(
              "Time/Rate: " + Long.toString(mTime) + "/" + (rxBytes + txBytes) / mTime);
          now.setText("Now Tx/Rx: " + (txBytes - mPrevTX) + "/" + (rxBytes - mPrevRX) + " KB");
          total.setText("Total Tx/Rx: " + txBytes + "/" + rxBytes + " KB");
          mPrevRX = rxBytes;
          mPrevTX = txBytes;
          mHandler.postDelayed(mRunnable, 1000);
        }
      };
}
