package in.peerreview.fmradioindia.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import com.bumptech.glide.Glide;

import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;

public class MainActivity extends Activity implements BaseView<MainState> {

  private ViewGroup mSplashView;
  private ImageView mLogo;
  private TextView mVersion;

  private ViewGroup mHomeView;
  private TextView mHomeTitle;
  private ViewPager mViewPager;


    private RecyclerView mSuggestionRV;
    private ViewGroup mTileNowPlaying;
    private ProgressBar mProgressBar;
    private ImageButton mPrevious;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private ImageView mNowPlayingImage;
    private TextView mNowPlayingText;

  private RecyclerView mRecyclerView;

  private SuggestionAdapter mSuggestionAdapter;

  private MainPresenter mPresenter;
  private ListAdapter mAdapter;
  private EditText mSearchText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);
    mSplashView = findViewById(R.id.splash);
    mLogo = findViewById(R.id.logo);
    mVersion = findViewById(R.id.version);
    mHomeView = findViewById(R.id.home);
    mProgressBar = findViewById(R.id.progress_bar);
    mPlayPause = findViewById(R.id.play_pause);
    mPrevious = findViewById(R.id.previous);
    mNext = findViewById(R.id.next);
    mHomeTitle = findViewById(R.id.home_title);
    mSuggestionRV = findViewById(R.id.suggestion_rv);
    mTileNowPlaying = findViewById(R.id.tile_nowplaying);

    mRecyclerView = findViewById(R.id.rv);
    mSearchText = findViewById(R.id.search_text);
      mViewPager = (ViewPager) findViewById(R.id.pager);
      mNowPlayingText = findViewById(R.id.now_playing_text);
      mNowPlayingImage = findViewById(R.id.now_playing_image);
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
  }

  private void initCallback() {

      mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

          }

          @Override
          public void onPageSelected(int position) {
              switch (position){
                  case 0:
                      mHomeTitle.setText(getResources().getString(R.string.my_radio));
                      break;
                  case 1:
                      mHomeTitle.setText(getResources().getString(R.string.search));
                      break;
              }
          }

          @Override
          public void onPageScrollStateChanged(int state) {

          }
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
      mSuggestionAdapter = new SuggestionAdapter(this);
      mSuggestionRV.setLayoutManager(new GridLayoutManager(this, 3));
      mSuggestionRV.setItemAnimator(new DefaultItemAnimator());
      mSuggestionRV.setAdapter(mSuggestionAdapter);
      mSuggestionRV.addOnItemTouchListener(
              new RecyclerTouchListener(
                      this,
                      mSuggestionRV,
                      new RecyclerTouchListener.ClickListener() {
                          @Override
                          public void onClick(View view, int position) {
                              if(position == 5){
                                  mViewPager.setCurrentItem(1);
                              } else {
                                  mPresenter.onSuggestionItemClick(position);
                              }
                          }

                          @Override
                          public void onLongClick(View view, int position) {}
                      }));

    // Search View.
      mSearchText.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              mPresenter.OnSearchQueryChanged(charSequence.toString());
          }

          @Override
          public void afterTextChanged(Editable editable) {

          }
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

            if (state.getChannel() != null) {
              mAdapter.setItems(state.getChannel());
            }

            if (state.getIsPlaying() != null) {
              if (state.getIsPlaying()) {
                mPlayPause.setImageResource(R.drawable.pause);
              } else {
                mPlayPause.setImageResource(R.drawable.play);
              }
            }
            if(state.getSuggestionList() != null){
                mSuggestionAdapter.setItems(state.getSuggestionList());
            }

            if(state.getCurChannel() != null){
                mNowPlayingText.setText("Now playing "+state.getCurChannel().getName());
                String Img = state.getCurChannel().getImg();
                if (Img != null && Img.length() != 0) {
                    Glide.with(getBaseContext()).load(Img).into(mNowPlayingImage);
                } else{
                    mNowPlayingImage.setImageResource(R.drawable.ic_music);
                }
                mTileNowPlaying.setVisibility(VISIBLE);
            }
          }
        });
  }
}
