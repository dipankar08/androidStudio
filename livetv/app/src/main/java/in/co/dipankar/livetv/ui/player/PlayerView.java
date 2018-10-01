package in.co.dipankar.livetv.ui.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseView;
import in.co.dipankar.livetv.data.Channel;
import in.co.dipankar.livetv.data.ChannelManager;
import in.co.dipankar.quickandorid.utils.DLog;

public class PlayerView extends BaseView {

  VideoView mVideoView;
  ChannelManager mChannelManager;

  LinearLayout mControl;
  RelativeLayout mToolbar;
  Runnable mRunnable;
  Handler mHandler = new Handler();

  public PlayerView(Context context) {
    super(context);
    init(context);
  }

  public PlayerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_player, this);
    mControl = findViewById(R.id.controls);
    mToolbar = findViewById(R.id.tool_bar);

    mVideoView = (VideoView) findViewById(R.id.video_view);
    mVideoView.setOnErrorListener(
        new OnErrorListener() {
          @Override
          public boolean onError(Exception e) {
            DLog.e("setOnErrorListener called");
            return false;
          }
        });

    mVideoView.setOnPreparedListener(
        new OnPreparedListener() {
          @Override
          public void onPrepared() {
            mVideoView.start();
          }
        });

    mVideoView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mControl.getVisibility() == VISIBLE) {
                hideControls();
                mHandler.removeCallbacks(mRunnable);
            } else {
                showControls();
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 5000);
            }
        }
    });

    mRunnable =
        new Runnable() {
          @Override
          public void run() {
            hideControls();
          }
        };

    mChannelManager = ChannelManager.Get();
    ImageView mBack = findViewById(R.id.back);
    mBack.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            getNavigation().goBack();
          }
        });

    final ImageView mPlayPause = findViewById(R.id.play_pause);
    mPlayPause.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mVideoView.isPlaying()) {
              mVideoView.pause();
              mPlayPause.setImageResource(R.drawable.ic_play_white_48);
            } else {
              mVideoView.start();
              mPlayPause.setImageResource(R.drawable.ic_pasue_white_48);
            }
          }
        });
  }

  private void showControls() {
    mControl.setVisibility(VISIBLE);
    mToolbar.setVisibility(VISIBLE);
  }

  private void hideControls() {
    mControl.setVisibility(GONE);
    mToolbar.setVisibility(GONE);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Channel c = mChannelManager.getCurrent();
    if (c != null) {
      mVideoView.setVideoURI(Uri.parse(c.getUrl()));
    }
  }
}
