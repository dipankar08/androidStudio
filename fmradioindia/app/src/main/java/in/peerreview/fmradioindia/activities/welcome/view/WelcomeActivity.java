package in.peerreview.fmradioindia.activities.welcome.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.radio.view.RadioActivity;
import in.peerreview.fmradioindia.activities.welcome.IWelcomeContract;
import in.peerreview.fmradioindia.activities.welcome.presenter.WelcomePresenter;

public class WelcomeActivity extends AppCompatActivity implements IWelcomeContract.View {

  private static final String TAG = "MainActivity";
  ImageView radio;
  private IWelcomeContract.Presenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new WelcomePresenter(this);
    setContentView(R.layout.activity_welcome);
    initViews();
    presenter.loadData();
  }

  @Override
  public void exit() {
    this.finish();
  }

  @Override
  public void gotoHome() {
    Intent intent = new Intent(WelcomeActivity.this, RadioActivity.class);
    startActivity(intent);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    finish();
  }

  private void initViews() {
    // update versions.
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

    // animation image view
    radio = (ImageView) findViewById(R.id.logo);
    Animation rotation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.bounce);
    rotation.setFillAfter(true);
    radio.startAnimation(rotation);
  }
}
