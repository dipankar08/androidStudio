package in.co.dipankar.samplemvp.login.model;

import android.os.Handler;
import android.text.TextUtils;

/**
 * Created by dip on 1/24/18.
 */

public class LoginInteractor implements ILoginInteractor {

    @Override
    public void login(final String username, final String password, final OnLoginFinishedListener listener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                if (TextUtils.isEmpty(username)) {
                    listener.onUsernameError("User name is empty!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    listener.onPasswordError("Password is empty!");
                    return;
                }
                listener.onSuccess();
            }
        }, 2000);
    }
}