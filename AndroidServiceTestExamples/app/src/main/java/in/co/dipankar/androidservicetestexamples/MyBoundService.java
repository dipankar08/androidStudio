package in.co.dipankar.androidservicetestexamples;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyBoundService extends Service {

  private MyLocalBinder myLocalBinder = new MyLocalBinder();

  public class MyLocalBinder extends Binder {
    MyBoundService getService() {
      return MyBoundService.this;
    }
  }

  @Override
  public void onDestroy() {
    log("onDestroy called");
    super.onDestroy();
  }

  @Override
  public boolean onUnbind(Intent intent) {
    log("onUnbind called");
    return super.onUnbind(intent);
  }

  @Override
  public void onCreate() {
    log("onCreate called");
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log("onStartCommand called");
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    log("onBind called");
    return myLocalBinder;
  }

  public int add(int a, int b) {
    log("Add called");
    return a + b;
  }

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
}
