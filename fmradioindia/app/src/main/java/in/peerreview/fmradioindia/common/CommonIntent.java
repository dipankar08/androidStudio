package in.peerreview.fmradioindia.common;

import android.app.Activity;
import android.content.Intent;
import in.peerreview.fmradioindia.activities.radio.RadioActivity;
import in.peerreview.fmradioindia.activities.tutorial.TutorialActivity;
import in.peerreview.fmradioindia.activities.welcome.WelcomeActivity;

/** Created by dip on 5/8/18. */
public class CommonIntent {
  public static void startRadioActivity(Activity sender, String start_with) {
    Intent intent = new Intent(sender, RadioActivity.class);
    intent.putExtra("START_WITH", start_with);
    sender.startActivity(intent);
    sender.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }

  public static void startTutorialActivity(Activity sender, String start_with) {
    Intent intent = new Intent(sender, TutorialActivity.class);
    intent.putExtra("START_WITH", start_with);
    sender.startActivity(intent);
    sender.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }

}
