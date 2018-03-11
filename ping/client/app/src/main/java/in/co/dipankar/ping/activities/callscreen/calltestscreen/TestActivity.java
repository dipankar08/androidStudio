package in.co.dipankar.ping.activities.callscreen.calltestscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.callscreen.CallActivity;
import in.co.dipankar.quickandorid.utils.DLog;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button button = findViewById(R.id.call);
        final TextView name = findViewById(R.id.name);
        final TextView id = findViewById(R.id.id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1 = name.getText().toString();
                String id1 = id.getText().toString();
                DLog.d(name1+id1);
                Intent myIntent = new Intent(TestActivity.this, CallActivity.class);
                myIntent.putExtra("name", name1); //Optional parameters
                myIntent.putExtra("id", id1); //Optional parameters
                TestActivity.this.startActivity(myIntent);
            }
        });
    }
}
