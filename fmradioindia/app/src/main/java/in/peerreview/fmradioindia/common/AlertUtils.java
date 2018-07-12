package in.peerreview.fmradioindia.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import in.co.dipankar.quickandorid.utils.AndroidUtils;
import in.co.dipankar.quickandorid.utils.TelemetryUtils;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;

public class AlertUtils {
    Context mContext;
    public AlertUtils(Context context){
        mContext = context;
    }

    public void showRateAlert(){
        new AlertDialog.Builder(mContext)
            .setTitle("Rate this app!")
            .setMessage("Hope you are enjoying this app ! Will appreciate if you could tell us your feedback and give 5 start rating.")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AndroidUtils.RateIt(mContext);
                    FMRadioIndiaApplication.Get().getTelemetry().markHit("rate_dialog_on_click_ok");
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FMRadioIndiaApplication.Get().getTelemetry().markHit("rate_dialog_on_click_cancel");

                }
            }).show();
    }

    public void showUpgradeAlert(){
        new AlertDialog.Builder(mContext)
                .setTitle("Upgrade the app!")
                .setMessage("We are consistently working on this app by fixing issues or adding new feature. Recently, I have upgraded this app, so please consider upgrading it. ")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AndroidUtils.RateIt(mContext);
                        FMRadioIndiaApplication.Get().getTelemetry().markHit("upgrade_dialog_on_click_ok");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FMRadioIndiaApplication.Get().getTelemetry().markHit("upgrade_dialog_on_click_cancel");

                    }
                }).show();
    }

    public void showFeedback(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Tell us how you think about this app?");
        String[] animals = {
                "I am very happy and would love to support the developer",
                "I will not use this app if it shows this adds",
                "I am very happy with this app",
                "I am everyday using this",
                "I am using this on weekends",
                "The app is crashing multiple times!",
                "I am not using this app",
        };
        int checkedItem = 1;
        builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FMRadioIndiaApplication.Get().getTelemetry().markHit("show_feedback_dialog_on_click_ok");
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

