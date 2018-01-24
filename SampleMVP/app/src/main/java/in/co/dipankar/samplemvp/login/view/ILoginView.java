package in.co.dipankar.samplemvp.login.view;

/**
 * Created by dip on 1/24/18.
 */

public interface ILoginView {
    void showProgress();
    void hideProgress();
    void setUsernameError(String msg);
    void setPasswordError(String msg);
    void navigateToHome();
}
