package in.peerreview.fmradioindia.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;

public class MainActivity extends Activity implements BaseView<MainState> {

  private ViewGroup mSplashView;
  private ImageView mLogo;
  private TextView mVersion;

  private ViewGroup mHomeView;
  private TextView mHomeTitle;
  private ViewPager mViewPager;
  private ViewGroup mControlHolder;

  private ProgressBar mProgressBar;
  private ImageButton mPrevious;
  private ImageButton mPlayPause;
  private ImageButton mNext;
  private TextView mNowPlayingText;
  private TextView mLive;

  private RecyclerView mRecyclerView;

  private RecyclerView mCategoriesRV;
  private CategoriesAdapter mCategoriesAdapter;

  private MainPresenter mPresenter;
  private ListAdapter mAdapter;
  private EditText mSearchText;

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null && intent.getExtras() != null) {
      DLog.d("onNewIntent " + intent.getExtras().toString());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);
    mSplashView = findViewById(R.id.splash);
    mLogo = findViewById(R.id.logo);
    mVersion = findViewById(R.id.version);
    mHomeView = findViewById(R.id.home);
    mHomeTitle = findViewById(R.id.home_title);

    mCategoriesRV = findViewById(R.id.categories_rv);

    mRecyclerView = findViewById(R.id.rv);
    mSearchText = findViewById(R.id.search_text);
    mViewPager = (ViewPager) findViewById(R.id.pager);

    // play control
    mControlHolder = findViewById(R.id.controls);
    mProgressBar = findViewById(R.id.progress_bar);
    mPlayPause = findViewById(R.id.play_pause);
    mPrevious = findViewById(R.id.previous);
    mNext = findViewById(R.id.next);
    mNowPlayingText = findViewById(R.id.now_playing_text);
    mLive = findViewById(R.id.live);

    initElement();
    initCallback();
    // at the end you should init the Presenter.
    mPresenter = new MainPresenter();
  }

  private void initElement() {
    Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
    mLogo.startAnimation(pulse);
    mVersion.setText(Utils.getVersionString());
    WizardPagerAdapter adapter = new WizardPagerAdapter();
    mViewPager.setAdapter(adapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(mViewPager);
    tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_gray);
    tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_gray);
    tabLayout.setOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
              case 0:
                tab.setIcon(R.drawable.ic_home_black);
                break;
              case 1:
                tab.setIcon(R.drawable.ic_search_black);
                break;
            }
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
              case 0:
                tab.setIcon(R.drawable.ic_home_gray);
                break;
              case 1:
                tab.setIcon(R.drawable.ic_search_gray);
                break;
            }
          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}
        });
    // tabLayout.getTabAt(2).setIcon(R.drawable.ic_radio);

  }

  private void initCallback() {

    mViewPager.setOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          @Override
          public void onPageSelected(int position) {
            switch (position) {
              case 0:
                mHomeTitle.setText(getResources().getString(R.string.my_radio));
                break;
              case 1:
                mHomeTitle.setText(getResources().getString(R.string.search));
                break;
            }
          }

          @Override
          public void onPageScrollStateChanged(int state) {}
        });

    mNext.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.onNextClicked();
          }
        });

    mPrevious.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.onPrevClicked();
          }
        });

    mPlayPause.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.onPlayPauseClicked();
          }
        });

    // Suggestion.
    mCategoriesAdapter =
        new CategoriesAdapter(
            this,
            null,
            new CategoriesAdapter.Callback() {
              @Override
              public void onClickAllButton(int i) {
                mPresenter.onMoreClick(i);
              }

              @Override
              public void onClickItem(String id) {
                mPresenter.onItemClick(id);
              }
            });

    mCategoriesRV.setLayoutManager(new LinearLayoutManager(this));
    mCategoriesRV.setItemAnimator(new DefaultItemAnimator());
    mCategoriesRV.setAdapter(mCategoriesAdapter);

    // Search View.
    mSearchText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mPresenter.OnSearchQueryChanged(charSequence.toString());
          }

          @Override
          public void afterTextChanged(Editable editable) {}
        });
    mAdapter = new ListAdapter(this);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(
        new RecyclerTouchListener(
            this,
            mRecyclerView,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                mPresenter.onItemClick(position);
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));
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
            if (state.getIsShowLoading() != null) {
              if (state.getIsShowLoading()) {
                mProgressBar.setVisibility(VISIBLE);
              } else {
                mProgressBar.setVisibility(GONE);
              }
            }
            if (state.getErrorMsg() != null) {
              mNowPlayingText.setText(state.getErrorMsg());
            }

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
            if (state.getPage() != -1) {
              mViewPager.setCurrentItem(state.getPage());
            }

            if (state.getChannel() != null) {
              mAdapter.setItems(state.getChannel());
            }

            if (state.getIsPlaying() != null) {
              if (state.getIsPlaying()) {
                mPlayPause.setImageResource(R.drawable.pause);
                mLive.setVisibility(VISIBLE);
              } else {
                mPlayPause.setImageResource(R.drawable.play);
                mLive.setVisibility(GONE);
              }
            }
            if (state.getCategoriesMap() != null) {
              mCategoriesAdapter.setItems(state.getCategoriesMap());
            }

            if (state.getCurChannel() != null) {
              mNowPlayingText.setText(state.getCurChannel().getName());
              mControlHolder.setVisibility(VISIBLE);
            }
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (mViewPager.getCurrentItem() != 0) {
      mViewPager.setCurrentItem(0);
    } else {
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
}
