package in.co.dipankar.androidservicetestexamples;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class MyStartedService extends Service {

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

  @Override
  public void onCreate() {
    super.onCreate();
    log("onCreate called");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log("onStartCommand called");
    String type = intent.getStringExtra("type");

    if (type == null) {
      new MyAsyncTask().execute(5);
    } else if (type.equals("hang")) {
      takeNap(30);
    } else if (type.equals("test_selfStop")) {
      takeNap(5);
      stopSelf();
    } else if (type.equals("ResultReceiver")) {
      ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
      takeNap(2);
      log("Sending the data using ResultReceiver");
      Bundle bundle = new Bundle();
      bundle.putString("resultIntentService", "This is Result");
      resultReceiver.send(18, bundle);
    } else if (type.equals("BroadcastReceiver")) {
      takeNap(2);
      log("Sending the data using ResultReceiver");
      Intent intent1 = new Intent("action.service.to.activity");
      intent.putExtra("startServiceResult", "This is result");
      sendBroadcast(intent1);
    }

    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    log("onBind called");
    return null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    log("onDestroy called");
  }

  // AsyncTask class declaration
  class MyAsyncTask extends AsyncTask<Integer, String, Void> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      log("onPreExecute called");
    }

    @Override // Perform our Long Running Task
    protected Void doInBackground(Integer... params) {
      log("doInBackground called");

      for (int ctr = 0; ctr < 10; ctr++) {
        publishProgress("Counter is now " + ctr);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
      super.onProgressUpdate(values);
      // log("onProgressUpdate called");
      Toast.makeText(MyStartedService.this, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      log("onPostExecute called");
    }
  }

  private void takeNap(int i) {
    try {
      Thread.sleep(i * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
