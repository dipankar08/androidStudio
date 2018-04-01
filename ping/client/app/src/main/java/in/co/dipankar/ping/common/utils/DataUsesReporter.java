package in.co.dipankar.ping.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dip on 4/1/18.
 */

public class DataUsesReporter {
    public interface Callback {
        void onUpdate(int time, long TxNow, long RxNow);
        void onFinish(int time, long TxTotal, long RxTotal);
    }
    private List<Callback> mCallbackList;
    private Handler mHandler = new Handler();
    private int mTime  =0;
    private long mStartRX, mStartTX, mPrevRX, mPrevTX;
    private boolean mStart;

    public DataUsesReporter(){
        mCallbackList = new ArrayList<>();
        mStartRX = mPrevRX = TrafficStats.getTotalRxBytes();
        mStartTX = mPrevTX = TrafficStats.getTotalTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            return;
        }
    }
    public synchronized void start(){
        mStart = true;
        reset();
        mHandler.postDelayed(mRunnable, 1000);
    }
    public void reset(){
        mTime  = 0;
        mStartRX  = 0;
        mStartTX = 0;
        mPrevRX = 0;
        mPrevTX = 0;
    }
    public void stop(){
        long rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX) / 1024;
        long txBytes = (TrafficStats.getTotalTxBytes() - mStartTX) / 1024;

        for (Callback cb : mCallbackList) {
            cb.onFinish(mTime,rxBytes,txBytes);
        }
        mStart = false;
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if(mStart) {
                mTime++;
                long rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX) / 1024;
                long txBytes = (TrafficStats.getTotalTxBytes() - mStartTX) / 1024;

                for (Callback cb : mCallbackList) {
                    cb.onUpdate(mTime, (txBytes - mPrevTX) , (rxBytes - mPrevRX) );
                }
                mPrevRX = rxBytes;
                mPrevTX = txBytes;
                if(mStart) {
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        }
    };

    public void addCallback(Callback callback){
        mCallbackList.add(callback);
    }
    public void removeCallback(Callback callback){
        for(Callback cb : mCallbackList){
            if(cb ==callback){
                mCallbackList.remove(callback);
            }
        }
    }

}
