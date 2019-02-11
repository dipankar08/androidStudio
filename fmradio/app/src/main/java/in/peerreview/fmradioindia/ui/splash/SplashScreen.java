package in.peerreview.fmradioindia.ui.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import javax.annotation.Nullable;

public class SplashScreen extends ConstraintLayout implements BaseView<SplashState> {
  private TextView mVersion;
  private ViewGroup mFTUX, mBoot;
  private SplashPresenter mSplashPresenter;
  @Nullable private Callback mCallback;
  private VideoView mVideoView;

  public interface Callback {
    void onLoadSuccess();
  }

  public SplashScreen(Context context) {
    super(context);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.activity_splash, this, true);
    mFTUX = findViewById(R.id.ftux);
    mBoot = findViewById(R.id.boot);
      mVideoView = findViewById(R.id.videoView);
      Uri uri = Uri.parse("android.resource://"+getContext().getPackageName()+"/"+R.raw.backgroud_preview);
      mVideoView.setVideoURI(uri);
      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
              mp.setLooping(true);
          }
      });
      mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
          @Override
          public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
              return false;
          }
      });

    mSplashPresenter = new SplashPresenter("SplashPresenter");
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
      mVideoView.start();
    mSplashPresenter.attachView(this);
    //mSplashPresenter.startFetch();
  }

  @Override
  protected void onDetachedFromWindow() {
    mSplashPresenter.detachView();
    super.onDetachedFromWindow();
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  @Override
  public void render(final SplashState splashState) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (splashState.getError() == null) {
                  if (mCallback != null) {
                    mCallback.onLoadSuccess();
                  }
                } else {
                  AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                  alertDialog.setTitle("Not able to Load Channel");
                  alertDialog.setMessage("Please make sure you have internet connection");
                  alertDialog.setButton(
                      AlertDialog.BUTTON_NEUTRAL,
                      "Quit",
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                      });
                  alertDialog.setButton(
                      AlertDialog.BUTTON_POSITIVE,
                      "Try agin",
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                          mSplashPresenter.startFetch();
                        }
                      });
                  alertDialog.show();
                }
              }
            });
  }
}
