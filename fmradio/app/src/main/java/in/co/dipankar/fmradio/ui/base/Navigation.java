package in.co.dipankar.fmradio.ui.base;

import android.app.Activity;
import android.os.Bundle;

public interface Navigation {
    void goToStart(Bundle savedInstanceState, Bundle args);
    void gotoHome();
    void goBack();
    void navigate(Screen screen, Bundle bundle);
    void navigateWithReplace(Screen screen, Bundle bundle);
}
