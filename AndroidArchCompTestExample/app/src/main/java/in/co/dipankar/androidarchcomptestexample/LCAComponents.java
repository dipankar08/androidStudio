package in.co.dipankar.androidarchcomptestexample;

import static android.arch.lifecycle.Lifecycle.State.STARTED;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.util.Log;

public class LCAComponents implements LifecycleObserver {

  private Context mContext;
  private Lifecycle mLifecycle;

  public LCAComponents(Context context, Lifecycle lifecycle) {
    mContext = context;
    mLifecycle = lifecycle;
    // This is Registration.
    mLifecycle.addObserver(this);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void connectListener() {
    Log.d("DIPANKAR", "LCA ON_RESUME called");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void disconnectListener() {
    Log.d("DIPANKAR", "LCA ON_PAUSE called");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void cleanup() {
    Log.d("DIPANKAR", "LCA ON_DESTROY called");
    mLifecycle.removeObserver(this);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  public void setup() {
    Log.d("DIPANKAR", "LCA ON_CREATE called");
  }

  public void enable() {
    if (mLifecycle.getCurrentState().isAtLeast(STARTED)) {
      // connect if not connected
    }
  }
}
