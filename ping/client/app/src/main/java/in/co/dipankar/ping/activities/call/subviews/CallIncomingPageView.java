package in.co.dipankar.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IRtcUser;

public class CallIncomingPageView extends RelativeLayout {

  public interface Callback {
    void onAccept();

    void onReject();
  }

  private Callback mCallback;

  LayoutInflater mInflater;

  private View mRootView;
  private ViewletPeerInfoAudio mViewletPeerInfoAudio;
  private ViewletPeerInfoVideo mViewletPeerInfoVideo;

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public CallIncomingPageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public CallIncomingPageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public CallIncomingPageView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mInflater = LayoutInflater.from(context);
    mRootView = mInflater.inflate(R.layout.view_call_incomming_page, this, true);
    initButtons();
    mViewletPeerInfoAudio = findViewById(R.id.peer_audio_info);
    mViewletPeerInfoVideo = findViewById(R.id.peer_video_info);
  }

  private void initButtons() {
    ImageButton accept = (ImageButton) mRootView.findViewById(R.id.accept);
    ImageButton reject = (ImageButton) mRootView.findViewById(R.id.reject);
    accept.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onAccept();
          }
        });
    reject.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onReject();
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
