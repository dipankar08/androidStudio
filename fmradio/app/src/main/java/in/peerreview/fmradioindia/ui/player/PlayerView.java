package in.peerreview.fmradioindia.ui.player;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;

public class PlayerView extends ConstraintLayout implements BaseView<PlayerState>  {
    ViewGroup mMiniView, mFullView;
    TextView mPlayTitle1, mPlayTitle2, mSubtitle, mCount;
    ImageView mPlayPause1, mPlayPause2, mNext, mPrev, mBack;
    PlayerPresenter mPresenter;

    public PlayerView(Context context) {
        super(context);
        init();
    }
    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.screen_player, this, true);
        mMiniView = findViewById(R.id.mini_view);
        mFullView = findViewById(R.id.full_view);
        mPlayTitle1 = findViewById(R.id.play_text1);
        mPlayTitle2 = findViewById(R.id.title);
        mSubtitle = findViewById(R.id.sub_title);
        mCount = findViewById(R.id.count);
        mPlayPause1 = findViewById(R.id.play_pause1);
        mPlayPause2 = findViewById(R.id.play_pause2);
        mNext = findViewById(R.id.full_next);
        mPrev = findViewById(R.id.full_prev);
        mBack = findViewById(R.id.back);


        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullView.setVisibility(GONE);
                mMiniView.setVisibility(VISIBLE);
            }
        });
        mMiniView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullView.setVisibility(VISIBLE);
                mMiniView.setVisibility(GONE);
            }
        });
        mPlayPause1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClickPlayPause();
            }
        });
        mPlayPause2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClickPlayPause();
            }
        });
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClickNext();
            }
        });
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClickPrevious();
            }
        });
        mPresenter = new PlayerPresenter("PlayerPresenter");
    }

    @Override
    public void render(PlayerState state) {
        if(state.getChannel() != null){
            mPlayTitle1.setText(state.getChannel().getName());
            mPlayTitle2.setText(state.getChannel().getName());
            mSubtitle.setText(state.getChannel().getCategories());
            mCount.setText(state.getChannel().getCount_click() + "times played");
        }
        if(state.getState() != null){
            switch (state.getState()){
                case STOP:
                case ERROR:
                case PAUSE:
                    mPlayPause1.setImageResource(R.drawable.ic_play_red);
                    mPlayPause2.setImageResource(R.drawable.ic_play_white);
                    break;
                case PLAYING:
                case RESUME:
                    mPlayPause1.setImageResource(R.drawable.ic_pause_red);
                    mPlayPause2.setImageResource(R.drawable.ic_pause_white);
                    break;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPresenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPresenter.detachView();
    }
}
