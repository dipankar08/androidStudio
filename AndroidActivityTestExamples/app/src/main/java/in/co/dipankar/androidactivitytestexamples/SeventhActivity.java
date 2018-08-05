package in.co.dipankar.androidactivitytestexamples;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class SeventhActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {
  private static final String TAG_TASK_FRAGMENT = "task_fragment";
  private TaskFragment mTaskFragment;
  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    log("onCreate called");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seven);

    FragmentManager fm = getSupportFragmentManager();
    mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
    // If the Fragment is non-null, then it is currently being
    // retained across a configuration change.
    if (mTaskFragment == null) {
      mTaskFragment = new TaskFragment();
      fm.beginTransaction().add(R.id.holder, mTaskFragment, TAG_TASK_FRAGMENT).commit();
    }
    mTextView = findViewById(R.id.result);
  }

  @Override
  public void onPreExecute() {}

  @Override
  public void onProgressUpdate(int percent) {
    mTextView.setText("Update on " + percent + "%");
  }

  @Override
  public void onCancelled() {}

  @Override
  public void onPostExecute() {}

  // All Override function.
  private void log(String msg) {
    Log.d(
        "DIPANKAR",
        "[Thread:"
            + Thread.currentThread().getName()
            + " Obj:"
            + this.hashCode()
            + "]"
            + this.getClass().getSimpleName()
            + "::"
            + msg);
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    log("onPostCreate called");
    super.onPostCreate(savedInstanceState);
  }

  @Override
  public void onBackPressed() {
    log("onBackPressed called");
    super.onBackPressed();
  }

  @Override
  protected void onPause() {
    log("onPause called");
    super.onPause();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    log("onNewIntent called");
    super.onNewIntent(intent);
  }

  @Override
  protected void onResume() {
    log("onResume called");
    super.onResume();
  }

  @Override
  protected void onRestart() {
    log("onRestart called");
    super.onRestart();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    log("onConfigurationChanged called");
    super.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onDestroy() {
    log("onDestroy called");
    super.onDestroy();
  }

  @Override
  protected void onPostResume() {
    log("onPostResume called");
    super.onPostResume();
  }

  @Override
  protected void onStart() {
    log("onStart called");
    super.onStart();
  }

  @Override
  protected void onStop() {
    log("onStop called");
    super.onStop();
  }
}
