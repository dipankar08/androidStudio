package in.co.dipankar.androidservicetestexamples;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MyIntentService extends IntentService {

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
            + msg);
  }

  public MyIntentService() {
    super("MyWorkerThread"); // Give the name to the worker thread
    log("MyIntentService constrictor called");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    log("onCreate called");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log("onStartCommand called");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    log("onHandleIntent called");
    for (int i = 0; i < 5; i++) {
      log("Counter is now " + i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    log("onDestroy called");
  }
}
