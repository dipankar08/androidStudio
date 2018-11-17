package in.co.dipankar.livetv.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import in.co.dipankar.livetv.R;
import in.co.dipankar.quickandorid.utils.DLog;

public abstract class BaseNavigationActivity extends AppCompatActivity implements Navigation {

  public abstract Fragment getFragmentForScreen(Screen screen, Bundle args);

  // You must define this
  public abstract Screen getHomeScreen();

  public abstract Screen getSplashScreen();

  public abstract int getContainerId();

  public abstract FragmentAnimation getFragmentAnimation(Screen screen);

  public Navigation getNavigation() {
    return this;
  }

  public enum FragmentAnimation {
    DEFAULT,
    SLIDE_LEFT_RIGHT,
    SLIDE_UP_DOWN
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Hide the bar
    supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    getSupportActionBar().hide();

    /* Uncommenting this it will Make No trsparnet color in battery title bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    */
  }

  @Override
  public void goBack() {
    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
      getSupportFragmentManager().popBackStack();
    } else {
      finish();
    }
  }

  @Override
  public void navigate(
      Screen screen,
      Bundle bundle,
      boolean isReplace /*is replace or add */,
      boolean shouldBackStack) {
    String tag = screen.name();
    Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
    if (fragment == null) {
      fragment = getFragmentIfNotExist(screen, bundle);
    }
    FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
    setAnimation(screen, fts);
    if (isReplace) {
      fts.replace(getContainerId(), fragment, tag);
    } else {
      fts.add(getContainerId(), fragment, tag);
    }
    // only add to backtrca
    if (shouldBackStack) {
      fts.addToBackStack(tag);
    }
    fts.commit();
    DLog.e("Count::::=>" + getSupportFragmentManager().getBackStackEntryCount());
  }

  @Override
  public void navigate(Screen screen, Bundle bundle) {
    navigate(screen, bundle, true, true);
  }

  private void setAnimation(Screen screen, FragmentTransaction fts) {
    if (getFragmentAnimation(screen) == null) {
      fts.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
      return;
    }
    switch (getFragmentAnimation(screen)) {
      case SLIDE_UP_DOWN:
        fts.setCustomAnimations(
            R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        break;
      case SLIDE_LEFT_RIGHT:
        fts.setCustomAnimations(
            R.anim.slide_left_in_fast,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_right_out_fast);
        break;
      case DEFAULT:
        fts.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
    }
  }

  private Fragment getFragmentIfNotExist(Screen screen, Bundle bundle) {
    return getFragmentForScreen(screen, bundle);
  }

  @Override
  public void onBackPressed() {
    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
      Log.d("DIPANKAR", "OnBackPressed");
      new AlertDialog.Builder(this)
          .setMessage("Are you sure you want to exit?")
          .setCancelable(false)
          .setPositiveButton(
              "Yes",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  BaseNavigationActivity.this.finish();
                }
              })
          .setNegativeButton("No", null)
          .show();
    } else {
      Log.d("DIPANKAR", "popBackStack");
      getSupportFragmentManager().popBackStack();
    }
  }

  private static boolean isConfigChange(Bundle savedInstanceState) {
    return savedInstanceState != null;
  }

  private void popAllFragments() {
    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
      String bottomTag = getSupportFragmentManager().getBackStackEntryAt(0).getName();
      getSupportFragmentManager().popBackStack(bottomTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
  }

  @Override
  public void gotoHome() {
    popAllFragments();
    navigate(getHomeScreen(), null, true, false);
  }

    @Override public void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override public void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
