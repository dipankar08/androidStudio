package in.peerreview.ping.activities.setting;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.CompoundButton;
import android.widget.Switch;

import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.peerreview.ping.R;

public class SettingActivity extends AppCompatActivity {

    private Switch mThemeSwitch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.ActivityThemeDark);
            DLog.e("Now Dark theme");
        } else{
            setTheme(R.style.ActivityThemeLight);
            DLog.e("Now Light theme");
        }
        super.onCreate(savedInstanceState);
        RuntimePermissionUtils.getInstance().init(this);
        setContentView(in.peerreview.ping.R.layout.activity_setting);
        init();
    }

    private void init(){
        mThemeSwitch = (Switch) findViewById(R.id.theme_switch);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            mThemeSwitch.setChecked(true);
        } else{
            mThemeSwitch.setChecked(false);
        }
        mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();
                } else{
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
    public void restartApp(){
        Intent i = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
    }
}
