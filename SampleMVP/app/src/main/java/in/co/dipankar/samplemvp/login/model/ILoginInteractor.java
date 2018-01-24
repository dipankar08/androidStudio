package in.co.dipankar.samplemvp.login.model;

/**
 * We have a network call here .. So let's tell as Integrator rather than a model!
 */

public interface ILoginInteractor {
    interface OnLoginFinishedListener {
        void onUsernameError(String msg);
        void onPasswordError(String msg);
        void onSuccess();
    }
    void login(String username, String password, OnLoginFinishedListener listener);
}