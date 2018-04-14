package in.peerreview.ping.activities.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;

public class TestActivity extends FragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RuntimePermissionUtils.getInstance().init(this);
    setContentView(in.peerreview.ping.R.layout.activity_test);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    RuntimePermissionUtils.getInstance()
        .onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
