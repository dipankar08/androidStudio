package in.co.dipankar.tvremote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import in.co.dipankar.quickandorid.utils.SimplePubSub;

public class MainActivity extends AppCompatActivity {

  private SimplePubSub mSimplePubSub =
      new SimplePubSub(
          new SimplePubSub.Config() {
            @Override
            public String getURL() {
              return "ws://simplestore.dipankar.co.in:8081";
            }
          });

  private Button mNext, mPrev, mVolP, mVolN, mPlay;

  @Override
  protected void onPause() {
    mSimplePubSub.disconnect();
    super.onPause();
  }

  @Override
  protected void onResume() {
    mSimplePubSub.connect();
    super.onResume();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mSimplePubSub.addCallback(
        new SimplePubSub.Callback() {
          @Override
          public void onConnect() {
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
                  }
                });
          }

          @Override
          public void onDisconnect() {
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getBaseContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                  }
                });
          }

          @Override
          public void onError(String err) {
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_SHORT).show();
                  }
                });
          }

          @Override
          public void onMessage(String topic, String data) {
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getBaseContext(), "onMessage ", Toast.LENGTH_SHORT).show();
                  }
                });
          }

          @Override
          public void onSignal(String topic, String data) {
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getBaseContext(), "Signal ", Toast.LENGTH_SHORT).show();
                  }
                });
          }
        });

    mSimplePubSub.subscribe("live_tv");

    mNext = findViewById(R.id.next);
    mNext.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSimplePubSub.publish("live_tv", "next");
          }
        });

    mPrev = findViewById(R.id.prev);
    mPrev.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSimplePubSub.publish("live_tv", "prev");
          }
        });

    mVolP = findViewById(R.id.volp);
    mVolP.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSimplePubSub.publish("live_tv", "volp");
          }
        });

    mVolN = findViewById(R.id.voln);
    mVolN.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSimplePubSub.publish("live_tv", "voln");
          }
        });

    mPlay = findViewById(R.id.play);
    mPlay.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mSimplePubSub.publish("live_tv", "play");
          }
        });
  }
}
