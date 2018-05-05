package in.peerreview.fmradioindia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.activities.welcome.WelcomeActivity;

public class AlermReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        DLog.e("AlermReceiver Received");
        Intent i=new Intent(context, WelcomeActivity.class);
        i.putExtra("START_WITH", intent.getStringExtra("START_WITH"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}