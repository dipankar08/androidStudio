package in.co.dipankar.fmradio.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import in.co.dipankar.fmradio.R;

public abstract class BaseNavigationActivity extends AppCompatActivity implements Navigation {

    public abstract Fragment getFragmentForScreen(Screen screen, Bundle args);

    public Navigation getNavigation(){
        return this;
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
    public void goToStart(Bundle savedInstanceState, Bundle args){
        if (!isConfigChange(savedInstanceState)) {
            popAllFragments();
        }
        navigate(Screen.HOME_SCREEN, args);
    }

    @Override
    public void goBack(){
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void navigate(Screen screen, Bundle bundle){
        String tag = screen.name();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null) {
            return;
        }

        fragment = getFragmentIfNotExist(screen, bundle);
        if(fragment != null) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.fragment_container, fragment, tag );
            fts.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_out, R.anim.fade_in);
            fts.addToBackStack(tag);
            Log.d("DIPANKAR", "adding to backtrack");
            fts.commit();
        }
    }

    @Override
    public void navigateWithReplace(Screen screen, Bundle bundle){
        getFragmentManager().popBackStack();
        navigate(screen,bundle);
    }

    private Fragment getFragmentIfNotExist(Screen screen, Bundle bundle){
        return getFragmentForScreen(screen, bundle);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Log.d("DIPANKAR", "OnBackPressed");
            super.onBackPressed();
        } else {
            Log.d("DIPANKAR", "popBackStack");
            getFragmentManager().popBackStack();
        }
    }
    private static boolean isConfigChange(Bundle savedInstanceState) {
        return savedInstanceState != null;
    }

    private void popAllFragments() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            String bottomTag = getFragmentManager().getBackStackEntryAt(0).getName();
            getFragmentManager().popBackStack(bottomTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
