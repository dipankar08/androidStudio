package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import in.peerreview.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.StateImageButton;

public class CallOutgoingPageView extends RelativeLayout {
  public interface Callback {
    void onClickedEnd();

    void onClickedToggleVideo(boolean isOn);

    void onClickedToggleAudio(boolean isOn);

    void onClickedToggleCamera(boolean isOn);

    void onClickedToggleSpeaker(boolean isOn);
  }

  private Callback mCallback;
  LayoutInflater mInflater;

  private View mRootView;
  private ViewletPeerInfoAudio mViewletPeerInfoAudio;
  private ViewletPeerInfoVideo mViewletPeerInfoVideo;

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public CallOutgoingPageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public CallOutgoingPageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public CallOutgoingPageView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mInflater = LayoutInflater.from(context);
    mRootView = mInflater.inflate(in.peerreview.ping.R.layout.view_call_outgoing_page, this, true);
    initButtons();
    mViewletPeerInfoAudio = findViewById(in.peerreview.ping.R.id.peer_audio_info);
    mViewletPeerInfoVideo = findViewById(in.peerreview.ping.R.id.peer_video_info);
  }

  private void initButtons() {
    StateImageButton video = mRootView.findViewById(in.peerreview.ping.R.id.toggle_video);
    StateImageButton audio = mRootView.findViewById(in.peerreview.ping.R.id.toggle_video);
    StateImageButton camera = mRootView.findViewById(in.peerreview.ping.R.id.toggle_camera);
    StateImageButton speaker = mRootView.findViewById(in.peerreview.ping.R.id.toggle_speaker);
    StateImageButton end = mRootView.findViewById(in.peerreview.ping.R.id.end);

    audio.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            mCallback.onClickedToggleAudio(b);
          }
        });
    video.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            mCallback.onClickedToggleVideo(b);
          }
        });
    end.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickedEnd();
          }
        });

    camera.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickedToggleCamera(!camera.isViewEnabled());
            camera.setViewEnabled(!camera.isViewEnabled());
          }
        });

    speaker.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickedToggleSpeaker(!speaker.isViewEnabled());
            speaker.setViewEnabled(!speaker.isViewEnabled());
          }
        });
  }

  public void renderAudioPeerView(IRtcUser user) {
    mViewletPeerInfoAudio.setVisibility(VISIBLE);
    mViewletPeerInfoVideo.setVisibility(GONE);
    mViewletPeerInfoAudio.updateView(user);
  }

  public void renderVideoPeerView(IRtcUser user) {
    mViewletPeerInfoAudio.setVisibility(GONE);
    mViewletPeerInfoVideo.setVisibility(VISIBLE);
    mViewletPeerInfoVideo.updateView(user);
  }

  public void updateTitle(String title) {
    mViewletPeerInfoAudio.updateTitle(title);
  }

  public void updateSubtitle(String title) {
    mViewletPeerInfoAudio.updateSubTitle(title);
  }
}
