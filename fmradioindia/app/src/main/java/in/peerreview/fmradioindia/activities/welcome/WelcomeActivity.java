package in.peerreview.fmradioindia.activities.welcome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.activities.radio.RadioActivity;
import in.peerreview.fmradioindia.common.CommonIntent;

public class WelcomeActivity extends AppCompatActivity implements IWelcomeContract.View {

  private ImageView radio;
  private IWelcomeContract.Presenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new WelcomePresenter(this);
    setContentView(R.layout.activity_welcome);
    initViews();
    presenter.loadData();
  }

  private void initViews() {
    TextView versionTextView = (TextView) findViewById(R.id.version);
    try {
      PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
      String version = pInfo.versionName;
      int verCode = pInfo.versionCode;
      versionTextView.setText("V" + version + " (Release - " + verCode + ")");
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      versionTextView.setText("V-0.0");
    }
    radio = (ImageView) findViewById(R.id.logo);
    Animation rotation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.bounce);
    rotation.setFillAfter(true);
    radio.startAnimation(rotation);
  }

  @Override
  public void exit() {
    FMRadioIndiaApplication.Get().getTelemetry().markHit("exit_from_welcome");
    this.finish();
  }

  @Override
  public void showPermissionDialog() {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("You must allow this permission.");
    alertDialog.setMessage(
        "We store the channel info in your mobile to save your data uses. Please restart and allow this permission.");
    alertDialog.setButton(
        AlertDialog.BUTTON_NEUTRAL,
        "OK",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    alertDialog.show();
  }

  @Override
  public void gotoHome() {
    if(SharedPrefsUtil.getInstance().getBoolean("FIRST_BOOT", true)) {
      CommonIntent.startTutorialActivity(this, getIntent().getStringExtra("START_WITH"));
    } else{
      CommonIntent.startRadioActivity(this, getIntent().getStringExtra("START_WITH"));
    }
    finish();
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    RuntimePermissionUtils.getInstance()
        .onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
