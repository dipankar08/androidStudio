package in.peerreview.fmradioindia.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.compactlist.CategoriesListView;
import in.peerreview.fmradioindia.ui.player.PlayerView;

public class MainActivity extends Activity implements BaseView<MainState> {
  private ViewGroup mSplashView;
  private SearchView mSearchView;
  private CategoriesListView mCategoriesList;

  private ViewGroup mHomeView;

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
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);

    mSplashView = findViewById(R.id.splash);
    mSearchView = findViewById(R.id.search);

    mHomeView = findViewById(R.id.home);
    mCategoriesList = findViewById(R.id.categories_list);

    initCallback();
    mPresenter = new MainPresenter();
  }

  private void initCallback() {
    mCategoriesList.addCallback(
        new CategoriesListView.Callback() {
          @Override
          public void onItemClick(String id) {
            mPresenter.onItemClick(id);
          }

          @Override
          public void onMoreClick(int i) {
              mSearchView.setVisibility(VISIBLE);
          }
        });

    mSearchView.addCallback(new SearchView.Callback() {
        @Override
        public void onClose() {
            mSearchView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onOpen() {

        }
    });
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
            if (state.getCurrentPage() != MainState.Page.NONE) {
              mSplashView.setVisibility(GONE);
              mHomeView.setVisibility(GONE);
              switch (state.getCurrentPage()) {
                case HOME:
                  mHomeView.setVisibility(VISIBLE);
                  break;
                case SPASH:
                  mSplashView.setVisibility(VISIBLE);
                  break;
              }
            }
            if (state.getCategoriesMap() != null) {
              mCategoriesList.setup(state.getCategoriesMap());
            }
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
