package in.co.dipankar.fmradio.ui.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.DataManager;
import in.co.dipankar.fmradio.ui.base.BaseNavigationActivity;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.fragments.ftux.FtuxFragment;
import in.co.dipankar.fmradio.ui.fragments.home.HomeFragment;
import in.co.dipankar.fmradio.ui.fragments.login.LoginFragment;
import in.co.dipankar.fmradio.ui.fragments.player.PlayerFragment;
import in.co.dipankar.fmradio.ui.fragments.search.SearchFragment;
import in.co.dipankar.fmradio.ui.fragments.setting.SettingFragment;
import in.co.dipankar.fmradio.ui.fragments.splash.SplashFragment;
import in.co.dipankar.fmradio.utils.Configuration;

public class MainActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if(Configuration.shouldShowFTUX()){
            navigate(Screen.FTUX_SCREEN, null);
        } else {
            navigate(Screen.SPLASH_SCREEN, null);
        }
    }

    private void init() {
        DataManager.Get().init(this);
    }

    @Override
    public Fragment getFragmentForScreen(Screen screen, Bundle args){
        switch (screen){
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
            default:
                return null;
        }
    }

    @Override
    protected void onDestroy() {
        DataManager.Get().destroy();
        super.onDestroy();
    }
}
