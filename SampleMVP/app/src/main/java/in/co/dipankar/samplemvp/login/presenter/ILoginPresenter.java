package in.co.dipankar.samplemvp.login.presenter;

import in.co.dipankar.samplemvp.login.view.ILoginView;

/**
 * Created by dip on 1/24/18.
 */

public interface ILoginPresenter {
    void tryLogin(String username, String password);
    void attach(ILoginView v);
    void dettach();
}
