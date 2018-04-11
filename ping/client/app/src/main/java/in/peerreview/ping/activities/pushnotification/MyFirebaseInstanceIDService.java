package in.peerreview.ping.activities.pushnotification;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import in.co.dipankar.quickandorid.utils.DLog;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

  @Override
  public void onTokenRefresh() {
    // Get updated InstanceID token.
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    DLog.e("Refreshed token: " + refreshedToken);
    SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.edit().putString("FIREBASE_TOKEN", refreshedToken).apply();
  }
}
