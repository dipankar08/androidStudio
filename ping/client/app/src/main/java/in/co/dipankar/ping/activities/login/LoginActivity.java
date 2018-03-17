package in.co.dipankar.ping.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.Utils;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.activities.callscreen.CallActivity;
import in.co.dipankar.ping.activities.calltestscreen.TestActivity;
import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/16/18.
 */

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.makeFullScreen(this);
        setContentView(R.layout.activity_login);
        ImageView usr1 = findViewById(R.id.user1);
        ImageView usr2 = findViewById(R.id.user2);

        usr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCallScreen(1);
            }
        });
        usr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCallScreen(2);
            }
        });

    }

    private void navigateToCallScreen(int i) {
        PingApplication  pingApplication = (PingApplication) this.getApplication();
        IRtcUser user = pingApplication.getUserManager().getLoginUser(i);
        Intent myIntent = new Intent(this, CallActivity.class);
        myIntent.putExtra("RtcUser", user);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
