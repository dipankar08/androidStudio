package in.co.dipankar.samplemvp.login.view;

/**
 * Created by dip on 1/24/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import in.co.dipankar.samplemvp.R;
import in.co.dipankar.samplemvp.login.model.LoginInteractor;
import in.co.dipankar.samplemvp.login.presenter.ILoginPresenter;
import in.co.dipankar.samplemvp.login.presenter.LoginPresenter;
import in.co.dipankar.samplemvp.mainview.view.MainActivity;

public class LoginActivity extends Activity implements ILoginView, View.OnClickListener {

    private ProgressBar progressBar;
    private EditText username;
    private EditText password;

    private ILoginPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        findViewById(R.id.button).setOnClickListener(this);
        presenter = new LoginPresenter(this,new LoginInteractor());
    }

    @Override protected void onDestroy() {
        presenter.dettach();
        super.onDestroy();
    }

    @Override protected void onResume() {
        presenter.attach(this);
        super.onResume();
    }
    @Override protected void onStop() {
        presenter.dettach();
        super.onStop();
    }
    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void setUsernameError(String msg) {
        username.setError(msg);
    }

    @Override public void setPasswordError(String msg) {
        password.setError(msg);
    }

    @Override public void navigateToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override public void onClick(View v) {
        presenter.tryLogin(username.getText().toString(), password.getText().toString());
    }
}
