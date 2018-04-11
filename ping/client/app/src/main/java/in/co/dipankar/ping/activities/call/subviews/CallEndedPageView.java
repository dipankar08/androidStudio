package in.co.dipankar.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IRtcUser;

public class CallEndedPageView extends RelativeLayout {

  public interface Callback {
    void onClickClose();

    void onClickRedail();
  }

  private Callback mCallback;
  private Context mContext;
  LayoutInflater mInflater;

  private View mRootView;
  private ViewletPeerInfoAudio mViewletPeerInfoAudio;
  private ViewletPeerInfoAudio mViewletPeerInfoVideo;

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public CallEndedPageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public CallEndedPageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public CallEndedPageView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
    mRootView = mInflater.inflate(R.layout.view_call_ended_page, this, true);
    initButtons();
    mViewletPeerInfoAudio = findViewById(R.id.peer_audio_info);
    mViewletPeerInfoVideo = findViewById(R.id.peer_video_info);
  }

  private void initButtons() {
    ImageButton close = mRootView.findViewById(R.id.close);
    ImageButton redail = mRootView.findViewById(R.id.redail);
    close.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickClose();
          }
        });
    redail.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mCallback.onClickRedail();
          }
        });
  }

  public void renderAudioPeerView(IRtcUser user) {
    mViewletPeerInfoAudio.setVisibility(VISIBLE);
    mViewletPeerInfoAudio.updateView(user);
  }

  public void updateTitle(String title) {
    mViewletPeerInfoAudio.updateTitle(title);
  }

  public void updateSubtitle(String title) {
    mViewletPeerInfoAudio.updateSubTitle(title);
  }
}
