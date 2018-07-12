package in.co.dipankar.androidservicetestexamples;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  private Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void startStartedService(View view) {
    Intent intent = new Intent(MainActivity.this, MyStartedService.class);
    startService(intent);
  }

  public void startStartedServiceHang(View c) {
    Intent intent = new Intent(MainActivity.this, MyStartedService.class);
    intent.putExtra("type", "hang");
    startService(intent);
  }

  public void stopStartedService(View view) {
    Intent intent = new Intent(MainActivity.this, MyStartedService.class);
    stopService(intent);
  }

  public void startIntentService(View view) {
    Intent intent = new Intent(this, MyIntentService.class);
    startService(intent);
  }

  public void startIntentServiceForResult1(View view) {
    ResultReceiver myResultReceiver = new MyResultReceiver(null);
    Intent intent = new Intent(this, MyStartedService.class);
    intent.putExtra("type", "ResultReceiver");
    intent.putExtra("receiver", myResultReceiver);
    startService(intent);
  }

  public void startIntentServiceForResult2(View view) {
    ResultReceiver myResultReceiver = new MyResultReceiver(null);
    Intent intent = new Intent(this, MyStartedService.class);
    intent.putExtra("type", "BroadcastReceiver");
    startService(intent);
  }

  public void testBoundedSerivce(View view) {
    if (isBound) {
      int res = myBoundService.add(10, 20);
      log("result from bounded service:" + res);
    }
  }

    public void startMusicActivity(View view) {
      startActivity(new Intent(this, MusicActivity.class));
    }

    private class MyResultReceiver extends ResultReceiver {
    public MyResultReceiver(Handler handler) {
      super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
      super.onReceiveResult(resultCode, resultData);
      log("onReceiveResult called");
      if (resultCode == 18 && resultData != null) {
        final String result = resultData.getString("resultIntentService");
        handler.post(
            new Runnable() {
              @Override
              public void run() {
                log("onReceiveResult on UI thread:" + result);
              }
            });
      }
    }
  }

  private BroadcastReceiver myStartedServiceReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          String result = intent.getStringExtra("startServiceResult");
          log("BroadcastReceiver::onReceive called: " + result);
        }
      };

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("action.service.to.activity");
    registerReceiver(myStartedServiceReceiver, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(myStartedServiceReceiver);
  }

  // Bunded serevivce
  boolean isBound = false;
  private MyBoundService myBoundService;

  private ServiceConnection mConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
          MyBoundService.MyLocalBinder myLocalBinder = (MyBoundService.MyLocalBinder) iBinder;
          myBoundService = myLocalBinder.getService();
          isBound = true;
          log("Connected to bounded service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          isBound = false;
          log("Disconnected to bounded service");
        }
      };

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, MyBoundService.class);
    bindService(intent, mConnection, BIND_AUTO_CREATE);
    log("caling bindservice on start");
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (isBound) {
      unbindService(mConnection);
      isBound = false;
      log("un bindservice on stop");
    }
  }

  // helper
  private void log(String msg) {
    Log.d(
        "DIPANKAR",
        "[Thread:"
            + Thread.currentThread().getName()
            + "]["
            + this.getClass().getSimpleName()
            + ":"
            + this.hashCode()
            + "] "
            + msg
            + "\n");
  }
}
