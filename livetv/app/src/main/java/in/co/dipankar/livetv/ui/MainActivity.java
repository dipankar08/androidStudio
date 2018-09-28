package in.co.dipankar.livetv.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseNavigationActivity;
import in.co.dipankar.livetv.base.Screen;
import in.co.dipankar.livetv.data.ChannelManager;
import in.co.dipankar.livetv.ui.home.HomeFragment;
import in.co.dipankar.livetv.ui.player.PlayerFragment;
import in.co.dipankar.livetv.ui.splash.SplashFragment;

public class MainActivity extends BaseNavigationActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ChannelManager.Get().init(this);
    getNavigation().navigate(Screen.SPLASH, null);

    fullscreen();
  }

  private void fullscreen() {
    int mUIFlag =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
  }

  @Override
  public Screen getHomeScreen() {
    return Screen.HOME;
  }

  @Override
  public Screen getSplashScreen() {
    return Screen.SPLASH;
  }

  @Override
  public int getContainerId() {
    return R.id.fragment_container;
  }

  @Override
  public FragmentAnimation getFragmentAnimation(Screen screen) {
    return null;
  }

  @Override
  public Fragment getFragmentForScreen(Screen screen, Bundle args) {
    switch (screen) {
      case HOME:
        return HomeFragment.getNewFragment(args);
      case SPLASH:
        return SplashFragment.getNewFragment(args);
      case PLAYER:
        return PlayerFragment.getNewFragment(args);
      default:
        return null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
