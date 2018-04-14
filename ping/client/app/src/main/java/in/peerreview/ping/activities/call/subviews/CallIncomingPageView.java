package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import in.peerreview.ping.R;
import in.peerreview.ping.common.utils.CustomButtonSheetView;
import in.peerreview.ping.common.utils.SheetItem;
import in.peerreview.ping.contracts.IRtcUser;
import java.util.ArrayList;
import java.util.List;

public class CallIncomingPageView extends RelativeLayout {

  public interface Callback {
    void onAccept();

    void onReject(String msg);
  }

  private Callback mCallback;

  LayoutInflater mInflater;

  private View mRootView;
  private ViewletPeerInfoAudio mViewletPeerInfoAudio;
  private ViewletPeerInfoVideo mViewletPeerInfoVideo;
  private CustomButtonSheetView mCustomButtonSheetView;

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
    initButtonSheet();
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
            mCallback.onReject(null);
          }
        });
    reject.setOnLongClickListener(
        new OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            mCustomButtonSheetView.show();
            return true;
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

  private void initButtonSheet() {
    mCustomButtonSheetView = findViewById(R.id.custom_endbutton_sheetview);
    List<CustomButtonSheetView.ISheetItem> mSheetItems = new ArrayList<>();
    mSheetItems.add(
        new SheetItem(
            102,
            "I am in meeting!",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mCallback.onReject("I am in meeting!");
              }
            },
            null));
    mSheetItems.add(
        new SheetItem(
            102,
            "I am busy now and will call you back later",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mCallback.onReject("I am busy now and will call you back later");
              }
            },
            null));
    mSheetItems.add(
        new SheetItem(
            102,
            "Can you call me back after 1 hrs",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mCallback.onReject("Can you call me back after 1 hrs");
              }
            },
            null));
    mSheetItems.add(
        new SheetItem(
            102,
            "Reject and schedule call",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mCallback.onReject("Reject and schedule call");
              }
            },
            null));

    mCustomButtonSheetView.addMenu(mSheetItems);
    mCustomButtonSheetView.hide();
  }
}
