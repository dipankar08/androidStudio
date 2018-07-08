package in.co.dipankar.samplemvp.login.presenter;

import in.co.dipankar.samplemvp.login.model.ILoginInteractor;
import in.co.dipankar.samplemvp.login.model.LoginInteractor;
import in.co.dipankar.samplemvp.login.view.ILoginView;

/** Created by dip on 1/24/18. */
public class LoginPresenter implements ILoginPresenter, ILoginInteractor.OnLoginFinishedListener {

  private ILoginView loginView;
  private ILoginInteractor loginInteractor;

  public LoginPresenter(ILoginView loginView, LoginInteractor loginInteractor) {
    this.loginView = loginView;
    this.loginInteractor = loginInteractor;
  }

  @Override
  public void tryLogin(String username, String password) {
    if (loginView != null) {
      loginView.showProgress();
    }

    loginInteractor.login(username, password, this);
  }

  @Override
  public void onViewAttached(ILoginView view) {
    this.loginView = loginView;
  }

  @Override
  public void onViewDetached() {
    loginView = null;
  }

  @Override
  public void onDestroyed() {
    loginView = null;
  }

  @Override
  public void onUsernameError(String msg) {
    if (loginView != null) {
      loginView.setUsernameError(msg);
      loginView.hideProgress();
    }
  }

  @Override
  public void onPasswordError(String msg) {
    if (loginView != null) {
      loginView.setPasswordError(msg);
      loginView.hideProgress();
    }
  }

  @Override
  public void onSuccess() {
    if (loginView != null) {
      loginView.navigateToHome();
    }
  }
}
