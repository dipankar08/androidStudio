package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.peerreview.ping.R;
import in.peerreview.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;
import in.co.dipankar.quickandorid.views.CustomFontTextView;

public class ViewletPeerInfoVideo extends RelativeLayout {

  private CustomFontTextView mName;

  public interface Callback {
    void onClick();
  }

  private ViewletPeerInfoVideo.Callback mCallback;

  private Context mContext;
  LayoutInflater mInflater;
  RelativeLayout mPeerInfo;
  public TextView mTitle;
  public TextView mSubTitle;
  public ImageView mPeerBackgroud;
  CircleImageView mPeerImage;
  private View mCenterHolder;

  public ViewletPeerInfoVideo(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public ViewletPeerInfoVideo(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public ViewletPeerInfoVideo(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mInflater = LayoutInflater.from(context);
    View v = mInflater.inflate(R.layout.viewlet_peer_video_view, this, true);
    mPeerInfo = v.findViewById(R.id.peer_info);
    mPeerImage = v.findViewById(R.id.peer_img);
    mName = v.findViewById(R.id.title);
    mTitle = v.findViewById(R.id.title);
    mSubTitle = v.findViewById(R.id.subtitle);
    mContext = context;
    mCenterHolder = findViewById(R.id.center_holder);
  }

  public void updateView(IRtcUser peer) {
    if (peer != null) {
      Glide.with(mContext).load(peer.getProfilePictureUrl()).into(mPeerImage);
      mName.setText(peer.getUserName());
      mTitle.setText("Calling " + peer.getUserName() + "...");
    }
  }

  public void updateTitle(String title) {
    mTitle.setText(title);
  }

  public void updateSubTitle(String subTitle) {
    mSubTitle.setText(subTitle);
  }

  public void setCallback(ViewletPeerInfoVideo.Callback callback) {
    mCallback = callback;
  }

  public void setVisibilityCenterView(int v) {
    mCenterHolder.setVisibility(v);
  }
}
