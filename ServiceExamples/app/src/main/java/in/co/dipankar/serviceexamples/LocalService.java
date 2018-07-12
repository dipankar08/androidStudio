package in.co.dipankar.serviceexamples;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.Random;

public class LocalService extends Service {
  private final IBinder mBinder = new LocalBinder();
  private final Random mGenerator = new Random();

  public class LocalBinder extends Binder {
    LocalService getService() {
      // Return this instance of LocalService so clients can call public methods
      return LocalService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }
  // clinet methods.
  public int getRandomNumber() {
    return mGenerator.nextInt(100);
  }
}
