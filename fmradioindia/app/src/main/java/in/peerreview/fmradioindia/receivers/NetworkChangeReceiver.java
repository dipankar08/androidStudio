package in.peerreview.fmradioindia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public interface INetworkChangeCallback{
        void onNetworkGone();
        void onNetworkAvailable();
        void onNetworkAvailableWifi();
        void onNetworkAvailableMobileData();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(mNetworkChangeCallback != null){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            int networkType = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_TYPE);
            boolean isWiFi = networkType == ConnectivityManager.TYPE_WIFI;
            boolean isMobile = networkType == ConnectivityManager.TYPE_MOBILE;
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                mNetworkChangeCallback.onNetworkAvailable();
                if(isMobile){
                    mNetworkChangeCallback.onNetworkAvailableMobileData();
                }
                if(isWiFi){
                    mNetworkChangeCallback.onNetworkAvailableWifi();
                }
            } else {
                mNetworkChangeCallback.onNetworkGone();
            }
        }
    }

    public void addNetworkChangeCallback(INetworkChangeCallback networkChangeCallback){
        mNetworkChangeCallback = networkChangeCallback;
    }
    // private
    private INetworkChangeCallback mNetworkChangeCallback;

}