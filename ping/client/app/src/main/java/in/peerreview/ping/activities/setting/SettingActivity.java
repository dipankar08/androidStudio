package in.peerreview.ping.activities.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.bumptech.glide.Glide;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.CircleImageView;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.contracts.IRtcUser;

public class SettingActivity extends AppCompatActivity {

  private Switch mThemeSwitch;
  private CircleImageView selfImg;
  private CustomFontTextView selfName;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
      setTheme(R.style.ActivityThemeDark);
      DLog.e("Now Dark theme");
    } else {
      setTheme(R.style.ActivityThemeLight);
      DLog.e("Now Light theme");
    }
    super.onCreate(savedInstanceState);
    RuntimePermissionUtils.getInstance().init(this);
    setContentView(in.peerreview.ping.R.layout.activity_setting);
    init();
  }

  private void init() {
    selfImg = findViewById(R.id.self_img);
    selfName = findViewById(R.id.self_name);
    IRtcUser user = PingApplication.Get().getMe();
    selfName.setText(user.getUserName());
    Glide.with(getApplicationContext()).load(user.getProfilePictureUrl()).into(selfImg);

    mThemeSwitch = (Switch) findViewById(R.id.theme_switch);
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
      mThemeSwitch.setChecked(true);
    } else {
      mThemeSwitch.setChecked(false);
    }
    mThemeSwitch.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
              restartApp();
            } else {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
              restartApp();
            }
          }
        });
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    RuntimePermissionUtils.getInstance()
        .onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  public void restartApp() {
    Intent i = new Intent(getApplicationContext(), SettingActivity.class);
    startActivity(i);
    // overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    finish();
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
  }
}
