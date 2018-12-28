package in.peerreview.fmradioindia.newui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;
import in.co.dipankar.quickandorid.arch.BaseView;

public class MainActivity extends Activity implements BaseView<MainState> {

    private ViewGroup mSplashView;
    private ImageView mLogo;
    private TextView mVersion;

    private ViewGroup mHomeView;
    private TextView mErrorText;
    private ViewGroup mControls;
    private ProgressBar mProgressBar;
    private ImageButton mPrevious;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private RecyclerView mRecyclerView;

    private MainPresenter mPresenter;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mSplashView = findViewById(R.id.splash);
        mLogo = findViewById(R.id.logo);
        mVersion = findViewById(R.id.version);
        mHomeView = findViewById(R.id.home);
        mErrorText = findViewById(R.id.error_text);
        mControls = findViewById(R.id.controls);
        mProgressBar = findViewById(R.id.progress_bar);
        mPlayPause = findViewById(R.id.play_pause);
        mPrevious = findViewById(R.id.previous);
        mNext = findViewById(R.id.next);

        mRecyclerView = findViewById(R.id.rv);
        initElement();
        initCallback();
        // at the end you should init the Presenter.
        mPresenter = new MainPresenter();
    }

    private void initElement() {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        mLogo.startAnimation(pulse);
        mVersion.setText(Utils.getVersionString());
    }

    private void initCallback() {

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
                            public void onLongClick(View view, int position) {
                            }
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
                        if(state.getIsShowLoading()!= null){
                            if(state.getIsShowLoading()){
                                mProgressBar.setVisibility(VISIBLE);
                            } else{
                                mProgressBar.setVisibility(GONE);
                            }
                        }
                        if (state.getErrorMsg() != null) {
                            if (state.getErrorMsg().length() > 0) {
                                mErrorText.setText(state.getErrorMsg());
                                mErrorText.setVisibility(VISIBLE);
                            } else {
                                mErrorText.setVisibility(GONE);
                            }
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
                    }
                });
    }
}
