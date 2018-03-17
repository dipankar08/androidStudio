package in.co.dipankar.ping.activities.calltestscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.callscreen.CallActivity;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SharedPrefsUtil.getInstance().init(this);
        Button button = (Button) findViewById(R.id.call);
        final TextView name = (TextView) findViewById(R.id.name);
        final TextView id = (TextView) findViewById(R.id.id);
        final TextView TO = (TextView) findViewById(R.id.to);
        name.setText(SharedPrefsUtil.getInstance().getString("name",""));
        id.setText(SharedPrefsUtil.getInstance().getString("id",""));
        TO.setText(SharedPrefsUtil.getInstance().getString("to",""));
        button.setOnClickListener(v -> {

            String name1 = name.getText().toString();
            String id1 = id.getText().toString();
            String to1 = TO.getText().toString();
            SharedPrefsUtil.getInstance().setString("name",name1);
            SharedPrefsUtil.getInstance().setString("id",id1);
            SharedPrefsUtil.getInstance().setString("to",to1);
            DLog.d(name1+id1);
            Intent myIntent = new Intent(TestActivity.this, CallActivity.class);
            myIntent.putExtra("name", name1); //Optional parameters
            myIntent.putExtra("id", id1); //Optional parameters
            myIntent.putExtra("to", to1); //Optional parameters
            TestActivity.this.startActivity(myIntent);
        });
    }
}
