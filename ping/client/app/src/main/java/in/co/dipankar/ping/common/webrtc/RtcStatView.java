package in.co.dipankar.ping.common.webrtc;

import android.app.AlertDialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.callscreen.subviews.CallOngoingPageView;
import in.co.dipankar.quickandorid.views.MultiStateImageButton;
import in.co.dipankar.quickandorid.views.StateImageButton;

/**
 * Created by dip on 3/24/18.
 */

public class RtcStatView extends RelativeLayout {

    public interface Callback {
    }

    private RtcStatView.Callback mCallback;
    LayoutInflater mInflater;

    TextView tx, rx, mTimeView;
    private Handler mHandler = new Handler();
    private int mTime  =0;
    private long mStartRX = 0;
    private long mStartTX = 0;

    public void setCallback(RtcStatView.Callback callback){
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

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_rtc_stat, this, true);
        mTimeView = findViewById(R.id.time);
        tx = findViewById(R.id.tx);
        rx = findViewById(R.id.rx);

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        }
        else {
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            mTime++;
            mTimeView.setText("Time: "+Long.toString(mTime)+" sec");
            long rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX)/1024;
            rx.setText("Byte sent: "+Long.toString(rxBytes)+" KB");
            long txBytes = (TrafficStats.getTotalTxBytes() - mStartTX)/1024;
            tx.setText("Byte received: "+Long.toString(txBytes) +" KB");
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

}