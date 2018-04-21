package in.peerreview.ping.activities.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.Timer;
import java.util.TimerTask;

import in.peerreview.ping.R;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.activities.bell.BellActivity;
import in.peerreview.ping.activities.home.HomeActivity;

/**
 * Created by dip on 4/20/18.
 */

public class CommonIntent {
    private static Ringtone alarmRingtone;
    public static void startHomeActivity(Context context,String type, String call_id){
        Intent myIntent = new Intent(context, HomeActivity.class);
        myIntent.putExtra("type", type);
        myIntent.putExtra("call_id", call_id);
        myIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(myIntent);
    }

    public static void startBellActivity(Context context, String incoming, String data) {
        Intent myIntent = new Intent(context, BellActivity.class);
        myIntent.putExtra("type", incoming);
        myIntent.putExtra("bell_info", data);
        myIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(myIntent);
    }

    public static void playTone(Context context) {
        stopTone();
        long ringDelay = 3500;
        Uri notification = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmRingtone = RingtoneManager
                .getRingtone(context, notification);
        alarmRingtone.play();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                alarmRingtone.stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, ringDelay);
    }
    public static void stopTone() {
        if(alarmRingtone != null){
            alarmRingtone.stop();
        }
    }
}
