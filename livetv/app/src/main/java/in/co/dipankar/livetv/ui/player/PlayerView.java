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
import android.widget.TextView;

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
  TextView mLoadingText;
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
    mLoadingText = findViewById(R.id.loading_text);
      setKeepScreenOn(true);
    mVideoView = (VideoView) findViewById(R.id.video_view);
    mVideoView.setOnErrorListener(
        new OnErrorListener() {
          @Override
          public boolean onError(Exception e) {
            DLog.e("setOnErrorListener called");
            mLoadingText.setText("Not able to play this channel!");
            mLoadingText.setVisibility(VISIBLE);
            return false;
          }
        });

    mVideoView.setOnPreparedListener(
        new OnPreparedListener() {
          @Override
          public void onPrepared() {
              mLoadingText.setVisibility(GONE);
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

      final ImageView mNext = findViewById(R.id.next);
      mNext.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              mChannelManager.next();
          }
      });

      final ImageView mPrev = findViewById(R.id.previous);
      mNext.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              mChannelManager.prev();
          }
      });


      mChannelManager.addActionCallback(new ChannelManager.ActionCallback() {
          @Override
          public void onAction(String action) {
              switch (action){
                  case "volp":
                      break;
                  case "voln":
                      break;
                  case "play":
                      if(mVideoView.isPlaying()){
                          mVideoView.pause();
                      } else{
                          mVideoView.start();
                      }
                      break;
                  case "track_change":
                      play();

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
    play();

  }

    private void play() {
        Channel c = mChannelManager.getCurrent();
        mLoadingText.setText("Wait! Try playing "+c.getName());
        if (c != null) {
            mVideoView.setVideoURI(Uri.parse(c.getUrl()));
        }
    }
}
