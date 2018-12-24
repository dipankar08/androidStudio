package in.co.dipankar.livetv.newUI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

import in.co.dipankar.livetv.R;
import in.co.dipankar.quickandorid.arch.BaseView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends Activity implements BaseView<MainState> {

    private VideoView mVideoView;
    private ViewGroup mControls;
    private ImageButton mPrevious;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private TextView mLoadingText;
    private RecyclerView mRecyclerView;

    private MainPresenter mPresenter;
    private TvListAdapter mTVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter();
        setContentView(R.layout.activity_main1);
        mVideoView = findViewById(R.id.video_view);
        mControls = findViewById(R.id.controls);
        mPlayPause = findViewById(R.id.play_pause);
        mPrevious = findViewById(R.id.previous);
        mNext = findViewById(R.id.next);
        mLoadingText = findViewById(R.id.loading);
        mRecyclerView = findViewById(R.id.rv);
        initCallback();
    }

    private void initCallback() {

        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(Exception e) {
                mPresenter.onVideoPlayerError();
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mVideoView.start();
                mPresenter.onVideoPlayerSuccess();
            }
        });

        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onTouchViewView();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onNextClicked();
            }
        });

        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPrevClicked();
            }
        });

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
            }
        });

        mTVAdapter = new TvListAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTVAdapter);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(state.getChannel() != null){
                    mTVAdapter.setItems(state.getChannel());
                    mRecyclerView.setVisibility(VISIBLE);
                }

                if(state.getErrorMsg() != null){
                    mLoadingText.setText(state.getErrorMsg());
                    mLoadingText.setVisibility(VISIBLE);
                } else{
                    mLoadingText.setVisibility(GONE);
                }

                if(state.getCurChannel() != null){
                    mVideoView.setVideoURI(Uri.parse(state.getCurChannel().getUrl()));
                }

                if(state.getIsShowControl() != null) {
                    if (state.getIsShowControl()) {
                        mControls.setVisibility(VISIBLE);
                        mRecyclerView.setVisibility(VISIBLE);
                    } else {
                        mControls.setVisibility(GONE);
                        mRecyclerView.setVisibility(GONE);
                    }
                }
            }
        });
    }
}
