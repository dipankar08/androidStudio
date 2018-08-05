package in.co.dipankar.fmradio.ui.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseNavigationActivity;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.fragments.home.HomeFragment;
import in.co.dipankar.fmradio.ui.fragments.login.LoginFragment;
import in.co.dipankar.fmradio.ui.fragments.player.PlayerFragment;
import in.co.dipankar.fmradio.ui.fragments.search.SearchFragment;
import in.co.dipankar.fmradio.ui.fragments.setting.SettingFragment;
import in.co.dipankar.fmradio.ui.fragments.splash.SplashFragment;

public class MainActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigate(Screen.HOME_SCREEN, null);
    }

    @Override
    public Fragment getFragmentForScreen(Screen screen){
        switch (screen){
            case HOME_SCREEN:
                return HomeFragment.getNewFragment(null);
            case SEARCH_SCREEN:
                return SearchFragment.getNewFragment(null);
            case SETTING_SCREEN:
                return SettingFragment.getNewFragment(null);
            case SPLASH_SCREEN:
                return SplashFragment.getNewFragment(null);
            case LOGIN_SCREEN:
                return LoginFragment.getNewFragment(null);
            case PLAYER_SCREEN:
                return PlayerFragment.getNewFragment(null);
            default:
                return null;
        }
    }
}
