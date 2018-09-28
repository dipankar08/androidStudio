package in.co.dipankar.fmradio.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseNavigationActivity;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.fragments.CategoriesFragment;
import in.co.dipankar.fmradio.ui.fragments.FtuxFragment;
import in.co.dipankar.fmradio.ui.fragments.HomeFragment;
import in.co.dipankar.fmradio.ui.fragments.LoginFragment;
import in.co.dipankar.fmradio.ui.fragments.PlayerFragment;
import in.co.dipankar.fmradio.ui.fragments.SearchFragment;
import in.co.dipankar.fmradio.ui.fragments.SettingFragment;
import in.co.dipankar.fmradio.ui.fragments.SplashFragment;
import in.co.dipankar.fmradio.ui.fragments.VideoPlayerFragment;
import in.co.dipankar.fmradio.utils.Configuration;

public class MainActivity extends BaseNavigationActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
    /*
    // test if it comes from Notification or Icon tap
    RadioManager.STATE state = FmRadioApplication.Get().getRadioManager().getState();
    if(state == RadioManager.STATE.RESUME || state == RadioManager.STATE.SUCCESS){
        Bundle bundle = new Bundle();
        String id=  FmRadioApplication.Get().getRadioManager().getCurrentID();
        Radio r = FmRadioApplication.Get().getRadioManager().getRadioForId(id);
        if(r != null){
            bundle.putString("ID",id);
            if(r.isVideo()){
                navigate(Screen.VIDEO_PLAER_SCREEN, bundle);
            } else{
                navigate(Screen.PLAYER_SCREEN, bundle);
            }
        }

    } else {
    /*/
    if (Configuration.shouldShowFTUX()) {
      navigate(Screen.FTUX_SCREEN, null, true, false);
    } else {
      navigate(Screen.SPLASH_SCREEN, null, true, false);
    }
  }

  @Override
  public Screen getHomeScreen() {
    return Screen.HOME_SCREEN;
  }

  @Override
  public Screen getSplashScreen() {
    return Screen.SPLASH_SCREEN;
  }

  private void init() {}

  @Override
  public Fragment getFragmentForScreen(Screen screen, Bundle args) {
    switch (screen) {
      case FTUX_SCREEN:
        return FtuxFragment.getNewFragment(args);
      case HOME_SCREEN:
        return HomeFragment.getNewFragment(args);
      case SEARCH_SCREEN:
        return SearchFragment.getNewFragment(args);
      case SETTING_SCREEN:
        return SettingFragment.getNewFragment(args);
      case SPLASH_SCREEN:
        return SplashFragment.getNewFragment(args);
      case LOGIN_SCREEN:
        return LoginFragment.getNewFragment(args);
      case PLAYER_SCREEN:
        return PlayerFragment.getNewFragment(args);
      case VIDEO_PLAER_SCREEN:
        return VideoPlayerFragment.getNewFragment(args);
      case CATEGORIES_SCREEN:
        return CategoriesFragment.getNewFragment(args);
      default:
        return null;
    }
  }

  @Override
  public FragmentAnimation getFragmentAnimation(Screen screen) {
    switch (screen) {
      case PLAYER_SCREEN:
      case VIDEO_PLAER_SCREEN:
        return FragmentAnimation.SLIDE_UP_DOWN;
      default:
        return FragmentAnimation.SLIDE_LEFT_RIGHT;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
