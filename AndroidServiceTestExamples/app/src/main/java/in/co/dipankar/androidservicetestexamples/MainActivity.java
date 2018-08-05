package in.co.dipankar.androidservicetestexamples;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

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

    public void startForeGroudService(View view) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Dipankar");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopForeGroudService(View view) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    public void startMusicFGService(View view) {
        Intent mService = new Intent(this, ForegroundPlayerService.class);
        mService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(mService);
    }

    public void stopMusicFGService(View view) {
        Intent mService = new Intent(this, ForegroundPlayerService.class);
        mService.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(mService);
    }

    public void playMusicActivity(View view) {
      playMusic();
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


  private void playMusic(){
      final MediaPlayer mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      try {
          mediaPlayer.setDataSource("http://www.largesound.com/ashborytour/sound/brobob.mp3");
      } catch (IOException e) {
          e.printStackTrace();
      }
          mediaPlayer.prepareAsync();
      mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
              mp.start();
              Log.e("DIPANKAR", "onPrepared called");
          }
      });

      mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
          @Override
          public boolean onError(MediaPlayer mp, int what, int extra) {
              Log.e("DIPANKAR", "onError called");
              return false;
          }
      });
      mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
          @Override
          public boolean onInfo(MediaPlayer mp, int what, int extra) {
              Log.e("DIPANKAR", "onInfo called");
              return false;
          }
      });
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
