package in.peerreview.fmradioindia.ui.mainactivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.MyApplication;
import in.peerreview.fmradioindia.ui.home.HomeScreen;
import in.peerreview.fmradioindia.ui.search.SearchView;
import in.peerreview.fmradioindia.ui.splash.SplashScreen;

public class MainActivity extends DaggerAppCompatActivity implements BaseView<MainState> {
  private SplashScreen mSplashView;
  private SearchView mSearchView;
  private HomeScreen mHomeView;
  private MainPresenter mPresenter;

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null && intent.getExtras() != null) {
      // DLog.d("onNewIntent " + intent.getExtras().toString());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);

    mSplashView = findViewById(R.id.splash);
    mSearchView = findViewById(R.id.search);
    mHomeView = findViewById(R.id.home);

    mSearchView.addCallback(
        new SearchView.Callback() {
          @Override
          public void onClose() {
            AnimationUtil.closeSearchScreen(getApplicationContext(), mSearchView);
          }

          @Override
          public void onOpen() {}
        });
    mSplashView.addCallback(
        new SplashScreen.Callback() {
          @Override
          public void onLoadSuccess() {
              AnimationUtil.switchFromSpashToHomeScreen(getApplicationContext(), mSplashView, mHomeView);
              mPresenter.onLoadSuccess();
          }
        });
    mHomeView.addCallback(
        new HomeScreen.Callback() {
          @Override
          public void onSearchClick() {
              AnimationUtil.openSearchScreen(getApplicationContext(), mSearchView);
          }
        });
    mPresenter = new MainPresenter();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mPresenter.attachView(this);
  }

  @Override
  protected void onPause() {
    mPresenter.detachView();
    super.onPause();
  }

  @Override
  public void render(final MainState state) {
    runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
          }
        });
  }

  @Override
  public void onBackPressed() {
    new AlertDialog.Builder(this)
        .setMessage("You are about to exit the app. Do you want to keep playing after exit?")
        .setCancelable(false)
        .setPositiveButton(
            "Exit but keep playing",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                MainActivity.super.onBackPressed();
              }
            })
        .setNeutralButton(
            "Cancel dialog",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {}
            })
        .setNegativeButton(
            "Stop Music and Exit",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.onStopPlay();
                MainActivity.super.onBackPressed();
              }
            })
        .show();
  }
}
