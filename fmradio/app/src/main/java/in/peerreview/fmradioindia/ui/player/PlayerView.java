package in.peerreview.fmradioindia.ui.player;

import android.animation.Animator;
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
import in.peerreview.fmradioindia.applogic.Utils;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;

public class PlayerView extends ConstraintLayout implements BaseView<PlayerState> {
  ViewGroup mMiniView, mFullView;
  TextView mPlayTitle1, mPlayTitle2, mSubtitle, mCount;
  ImageView mPlayPause1, mPlayPause2, mNext, mPrev, mBack, mFev;
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
    mFullView = findViewById(R.id.player_screen);
    mPlayTitle1 = findViewById(R.id.play_text1);
    mPlayTitle2 = findViewById(R.id.title);
    mSubtitle = findViewById(R.id.sub_title);
    mCount = findViewById(R.id.count);
    mPlayPause1 = findViewById(R.id.play_pause1);
    mPlayPause2 = findViewById(R.id.play_pause2);
    mNext = findViewById(R.id.full_next);
    mPrev = findViewById(R.id.full_prev);
    mBack = findViewById(R.id.back);
    mFev = findViewById(R.id.fev);

    mBack.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            showMiniView();
          }
        });
    mMiniView.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            showFullView();
          }
        });
    mPlayPause1.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPlayPause();
          }
        });
    mPlayPause2.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPlayPause();
          }
        });
    mNext.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickNext();
          }
        });
    mPrev.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPrevious();
          }
        });
    mFev.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.toggleFev();
          }
        });
    mPresenter = new PlayerPresenter();
  }

  private void showFullView() {
    mFullView
        .animate()
        .translationY(Utils.convertDpToPixel(60, getContext()))
        .alpha(1.0f)
        .setListener(
            new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animator) {}

              @Override
              public void onAnimationEnd(Animator animator) {
                mMiniView.setVisibility(GONE);
              }

              @Override
              public void onAnimationCancel(Animator animator) {}

              @Override
              public void onAnimationRepeat(Animator animator) {}
            });
  }

  private void showMiniView() {
    // Hide FullView
    mFullView
        .animate()
        .translationY(mFullView.getHeight() - Utils.convertDpToPixel(60, getContext()))
        .alpha(1.0f)
        .setListener(
            new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animator) {}

              @Override
              public void onAnimationEnd(Animator animator) {
                mMiniView.setVisibility(VISIBLE);
              }

              @Override
              public void onAnimationCancel(Animator animator) {}

              @Override
              public void onAnimationRepeat(Animator animator) {}
            });
  }

  @Override
  public void render(PlayerState state) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (state.getChannel() != null) {
                  mPlayTitle1.setText(state.getChannel().getName());
                  mPlayTitle2.setText(state.getChannel().getName());
                  mSubtitle.setText(state.getChannel().getCategories());
                  mCount.setText(state.getChannel().getCount_click() + "times played");
                }
                if (state.getState() != null) {
                  switch (state.getState()) {
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
                if (state.isFev()) {
                  mFev.setImageResource(R.drawable.ic_bookmark_fill_white_32);
                } else {
                  mFev.setImageResource(R.drawable.ic_bookmark_empty_white_32);
                }
              }
            });
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
