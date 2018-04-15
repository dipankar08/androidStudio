package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import in.co.dipankar.quickandorid.views.StateImageButton;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.activities.call.addon.BaseAddonView;
import in.peerreview.ping.common.model.CallInfo;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;

public class CallOngoingPageView extends RelativeLayout {

  public interface Callback {
    void onClickEnd();

    void onClickToggleVideo(boolean isOn);

    void onClickToggleAudio(boolean isOn);

    void onClickToggleCamera(boolean isOn);

    void onClickToggleSpeaker(boolean isOn);

    void onClickToggleLayout(int idx);
  }

  private Callback mCallback;
  LayoutInflater mInflater;

  private View mRootView;
  private ViewletPeerInfoAudio mViewletPeerInfoAudio;
  private ViewletPeerInfoVideo mViewletPeerInfoVideo;
  private BaseAddonView mAddonView;
  private View mButtonHolder;

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public CallOngoingPageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public CallOngoingPageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public CallOngoingPageView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mInflater = LayoutInflater.from(context);
    mRootView = mInflater.inflate(in.peerreview.ping.R.layout.view_call_ongoing_page, this, true);
    initButtons();
    mViewletPeerInfoAudio = findViewById(in.peerreview.ping.R.id.peer_audio_info);
    mViewletPeerInfoVideo = findViewById(in.peerreview.ping.R.id.peer_video_info);
    mAddonView = findViewById(in.peerreview.ping.R.id.addon_view);
  }

  private void initButtons() {
    mButtonHolder = mRootView.findViewById(R.id.buttonHolder);
    StateImageButton audio = mRootView.findViewById(in.peerreview.ping.R.id.toggle_audio);
    ImageButton end = mRootView.findViewById(in.peerreview.ping.R.id.end);
    StateImageButton video = mRootView.findViewById(in.peerreview.ping.R.id.toggle_video);
    StateImageButton camera = mRootView.findViewById(in.peerreview.ping.R.id.toggle_camera);
    StateImageButton speaker = mRootView.findViewById(in.peerreview.ping.R.id.toggle_speaker);
    audio.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            mCallback.onClickToggleAudio(b);
          }
        });
    video.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            mCallback.onClickToggleVideo(b);
          }
        });
    end.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickEnd();
          }
        });
    camera.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            // mCallback.onClickToggleCamera(!camera.isViewEnabled());
          }
        });
    speaker.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickToggleSpeaker(!speaker.isViewEnabled());
          }
        });
    showButtonHolder();
    mRootView.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        showButtonHolder();
        return false;
      }
    });
  }

  public void renderAudioPeerView(IRtcUser user) {
    mViewletPeerInfoAudio.setVisibility(VISIBLE);
    mViewletPeerInfoVideo.setVisibility(GONE);
    mViewletPeerInfoAudio.updateView(user);
    showButtonHolder();
  }

  public void renderVideoPeerView(IRtcUser user) {
    mViewletPeerInfoAudio.setVisibility(GONE);
    mViewletPeerInfoVideo.setVisibility(VISIBLE);
    mViewletPeerInfoVideo.updateView(user);
    mViewletPeerInfoVideo.setVisibilityCenterView(View.GONE);
      showButtonHolder();
  }

  public void updateTitle(String title) {
    mViewletPeerInfoAudio.updateSubTitle(title);
    mViewletPeerInfoVideo.updateSubTitle(title);
  }

  public void updateSubtitle(String title) {
    mViewletPeerInfoAudio.updateSubTitle(title);
    mViewletPeerInfoVideo.updateSubTitle(title);
  }

  public void toggleAddonView() {
    if (mAddonView.getVisibility() == VISIBLE) {
      mAddonView.setVisibility(GONE);
      if (PingApplication.Get().getCurrentCallInfo().getIsVideo()) {
        mViewletPeerInfoVideo.setVisibility(VISIBLE);
      } else {
        mViewletPeerInfoAudio.setVisibility(VISIBLE);
      }
    } else {
      mAddonView.setVisibility(VISIBLE);
      if (PingApplication.Get().getCurrentCallInfo().getIsVideo()) {
        mViewletPeerInfoVideo.setVisibility(GONE);
      } else {
        mViewletPeerInfoAudio.setVisibility(GONE);
      }
    }
  }
  void showButtonHolder() {
      mButtonHolder.setVisibility(VISIBLE);
      //auto hide only for video call.
      ICallInfo callInfo = PingApplication.Get().getCurrentCallInfo();
      if (callInfo != null && callInfo.getIsVideo()) {
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                  mButtonHolder.setVisibility(GONE);
              }
          }, 5 * 1000);
      }
  }
}
