package in.peerreview.ping.activities.call;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.activities.call.subviews.AcceptRejectNotification;
import in.peerreview.ping.activities.call.subviews.CallEndedPageView;
import in.peerreview.ping.activities.call.subviews.CallIncomingPageView;
import in.peerreview.ping.activities.call.subviews.CallLandingPageView;
import in.peerreview.ping.activities.call.subviews.CallOngoingPageView;
import in.peerreview.ping.activities.call.subviews.CallOutgoingPageView;
import in.peerreview.ping.activities.call.subviews.CallVideoGridView;
import in.peerreview.ping.common.model.IContactManager;
import in.peerreview.ping.common.utils.AudioManagerUtils;
import in.peerreview.ping.common.utils.CustomButtonSheetView;
import in.peerreview.ping.common.utils.SheetItem;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.webrtc.SessionDescription;

public class CallActivity extends Activity implements ICallPage.IView {

  private boolean VERBOSE = true;
  private final String TAG = "DIAPNAKR";

  // Views
  RelativeLayout mRootView;
  CallLandingPageView mCallLandingPageView;
  CallIncomingPageView mCallIncomingPageView;
  CallOngoingPageView mCallOngoingPageView;
  CallOutgoingPageView mCallOutgoingPageView;
  CallEndedPageView mCallEndedPageView;

  CallVideoGridView mCallVideoGridView;
  CustomFontTextView mNotificationView;
  AcceptRejectNotification mAcceptRejectNotification;
  // Presneter
  ICallPage.IPresenter mPresenter;

  // Andpid Utils
  MediaPlayer mMediaPlayer;
  AudioManagerUtils mAudioManagerUtils;

  private CustomButtonSheetView mCustomButtonSheetView;

  // private state
  private boolean mIsCameraOpen = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_call);
    proceedAfterPermission();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    proceedAfterPermission();
  }

  private void proceedAfterPermission() {
    initView();
    initButtonSheet();
    initCall();
  }

  private void initButtonSheet() {
    mCustomButtonSheetView = findViewById(R.id.custom_button_sheetview);
    List<CustomButtonSheetView.ISheetItem> mSheetItems = new ArrayList<>();
    mSheetItems.add(
        new SheetItem(
            102,
            "Test",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                DLog.e("Test");
              }
            },
            null));
    mSheetItems.add(
        new SheetItem(
            102,
            "Share places in map",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mCallOngoingPageView.toggleAddonView();
              }
            },
            null));
    mSheetItems.add(
        new SheetItem(
            102,
            "Change Audio Quality",
            CustomButtonSheetView.Type.OPTIONS,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                switch (v) {
                  case 0:
                    mPresenter.changeAudioBitrate(56);
                    break;
                  case 1:
                    mPresenter.changeAudioBitrate(42);
                    break;
                  case 2:
                    mPresenter.changeAudioBitrate(6);
                }
                DLog.e("Share music Clicked");
              }
            },
            new CharSequence[] {"HD", "Medium", "low"}));
    mCustomButtonSheetView.addMenu(mSheetItems);
    mCustomButtonSheetView.hide();
    mCallOngoingPageView.setOnLongClickListener(
        new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            mCustomButtonSheetView.show();
            return true;
          }
        });
  }

  private void initCall() {
    Intent intent = getIntent();
    boolean isComing = intent.getBooleanExtra("isComing", false);
    IRtcUser peer = (IRtcUser) intent.getSerializableExtra("peer");
    PingApplication.Get().setPeer(peer);

    String shareType = intent.getStringExtra("shareType");
    PingApplication.Get().getUserManager().addCallback(mContactMangerCallback);

    if (mPresenter != null) {
      // This is case - We already have a presneter
      mPresenter.reset(peer, ICallInfo.ShareType.valueOf(shareType.toUpperCase()));
    } else {
      mPresenter =
          new CallPresenter(
              this, peer, ICallInfo.ShareType.valueOf(shareType.toUpperCase()), mCallVideoGridView);
    }

    if (!isComing) {
      mPresenter.startOutgoingCall();
    } else {
      String callId = intent.getStringExtra("callId");
      String sdp_data = intent.getStringExtra("sdp_data");
      String sdp_type = intent.getStringExtra("sdp_type");
      SessionDescription sdp =
          new SessionDescription(SessionDescription.Type.valueOf(sdp_type), sdp_data);
      mPresenter.startIncomingCall(callId, sdp);
    }
    DLog.e("Call Presenter Initilized");
  }

  private void initView() {
    mRootView = findViewById(R.id.root_view);
    mCallLandingPageView = findViewById(R.id.call_landing_page_view);
    mCallLandingPageView.setCallback(mCallLandingPageViewCallBack);

    mCallIncomingPageView = findViewById(R.id.call_incoming_page_view);
    mCallIncomingPageView.setCallback(mCallIncomingPageViewCallBack);

    mCallOngoingPageView = findViewById(R.id.call_ongoing_page_view);
    mCallOngoingPageView.setCallback(mCallOngoingPageViewCallBack);

    mCallOutgoingPageView = findViewById(R.id.call_outgoing_page_view);
    mCallOutgoingPageView.setCallback(mCallOutgoingPageViewCallBack);

    mCallEndedPageView = findViewById(R.id.call_ended_page_view);
    mCallEndedPageView.setCallback(mCallEndedPageViewCallBack);

    mCallVideoGridView = findViewById(R.id.call_video_grid_view);
    mCallVideoGridView.setCallback(mCallVideoGridViewCallBack);

    mCallLandingPageView.setVisibility(View.VISIBLE);

    mMediaPlayer = MediaPlayer.create(this, R.raw.tone);

    mNotificationView = findViewById(R.id.notification);
    mAcceptRejectNotification = findViewById(R.id.peer_notification);

    // test
    Button test = findViewById(R.id.test);
    test.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            switchToView(ICallPage.PageViewType.ONGOING);
          }
        });

    // update
    mAudioManagerUtils =
        new AudioManagerUtils(
            this,
            new AudioManagerUtils.Callback() {
              @Override
              public void onSpeakerOn() {}

              @Override
              public void onSpeakerOff() {}
            });
  }

  private CallLandingPageView.Callback mCallLandingPageViewCallBack =
      new CallLandingPageView.Callback() {
        @Override
        public void onClickAudioCallBtn(IRtcUser user) {
          // mPresenter.startAudio(user);
          // mCallOutgoingPageView.setVisibilityOfPeerInfo(true);
        }

        @Override
        public void onClickVideoCallBtn(IRtcUser user) {
          // mPresenter.startVideo(user);
          // mCallOutgoingPageView.setVisibilityOfPeerInfo(false);
        }

        @Override
        public void onClickPokeBtn(IRtcUser user) {
          // todo
        }
      };

  private CallIncomingPageView.Callback mCallIncomingPageViewCallBack =
      new CallIncomingPageView.Callback() {
        @Override
        public void onAccept() {
          mPresenter.acceptCall();
        }

        @Override
        public void onReject(String msg) {
          mPresenter.rejectCall(msg);
        }
      };

  private CallOngoingPageView.Callback mCallOngoingPageViewCallBack =
      new CallOngoingPageView.Callback() {
        @Override
        public void onClickEnd() {
          mPresenter.endCall();
        }

        @Override
        public void onClickToggleVideo(boolean isOn) {
          mPresenter.toggleVideo(isOn);
        }

        @Override
        public void onClickToggleAudio(boolean isOn) {
          mPresenter.toggleAudio(isOn);
        }

        @Override
        public void onClickToggleCamera(boolean isOn) {
          mPresenter.toggleCamera(isOn);
        }

        @Override
        public void onClickToggleSpeaker(boolean isOn) {
          mAudioManagerUtils.setEnableSpeaker(isOn);
        }

        @Override
        public void onClickToggleLayout(int idx) {
          switch (idx) {
            case 0:
              mCallVideoGridView.setlayout(CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN);
              break;
            case 1:
              mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);
              break;
            case 3:
              mCallVideoGridView.setlayout(CallVideoGridView.Layout.SPLIT_VIEW);
              break;
          }
        }
      };

  private CallOutgoingPageView.Callback mCallOutgoingPageViewCallBack =
      new CallOutgoingPageView.Callback() {

        @Override
        public void onClickedEnd() {
          mPresenter.endCall();
        }

        @Override
        public void onClickedToggleVideo(boolean isOn) {
          mPresenter.toggleVideo(isOn);
        }

        @Override
        public void onClickedToggleAudio(boolean isOn) {
          mPresenter.toggleAudio(isOn);
        }

        @Override
        public void onClickedToggleCamera(boolean isOn) {
          mPresenter.toggleCamera(isOn);
        }

        @Override
        public void onClickedToggleSpeaker(boolean isOn) {
          mAudioManagerUtils.setEnableSpeaker(isOn);
        }
      };
  private CallEndedPageView.Callback mCallEndedPageViewCallBack =
      new CallEndedPageView.Callback() {

        @Override
        public void onClickClose() {
          finish();
        }

        @Override
        public void onClickRedail() {}
      };
  private CallVideoGridView.Callback mCallVideoGridViewCallBack =
      new CallVideoGridView.Callback() {

        @Override
        public void onClickedEnd() {}

        @Override
        public void onClickedToggleVideo(boolean isOn) {}

        @Override
        public void onClickedToggleAudio(boolean isOn) {}
      };

  @Override
  public void onStart() {
    super.onStart();
    if (VERBOSE) Log.v(TAG, "++ ON START ++");
  }

  @Override
  public void onResume() {
    super.onResume();
    if (VERBOSE) Log.v(TAG, "+ ON RESUME +");
  }

  @Override
  public void onStop() {
    super.onStop();
    if (VERBOSE) Log.v(TAG, "-- ON STOP --");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (VERBOSE) Log.v(TAG, "- ON DESTROY -");
  }

  @Override
  public void finish() {
    super.finish();
    mAcceptRejectNotification.hide();
    mNotificationView.setVisibility(View.GONE);
    PingApplication.Get().getUserManager().removeCallback(mContactMangerCallback);
    if (mPresenter != null) {
      mPresenter.finish();
    }
    // overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    mCallEndedPageView = null;
    mPresenter = null;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    RuntimePermissionUtils.getInstance()
        .onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mPresenter.onActivityResult(requestCode, resultCode, data);
  }

  // Override function
  @Override
  public void switchToView(ICallPage.PageViewType type) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            switchToViewInternal(type);
          }
        });
  }

  @Override
  public void showNetworkNotification(String process, String s) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            showNetworkNotificationInternal(process, s);
          }
        });
  }

  @Override
  public void onCameraOff() {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            onCameraOffInternal();
          }
        });
  }

  @Override
  public void onCameraOn() {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            onCameraOnInternal();
          }
        });
  }

  @Override
  public void onRtcStat(Map<String, String> reports) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            onRtcStatInternal(reports);
          }
        });
  }

  @Override
  public void toggleViewBasedOnVideoEnabled(boolean type) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            toggleViewBasedOnVideoEnabledInternal(type);
          }
        });
  }

  @Override
  public void prepareCallUI(IRtcUser peer, ICallInfo callinfo) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            prepareCallUIIntrenal(peer, callinfo);
          }
        });
  }

  @Override
  public void updateOutgoingView(String title, String subtitle) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            updateOutgoingViewInternal(title, subtitle);
          }
        });
  }

  @Override
  public void updateIncomingView(String title, String subtitle) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            updateIncomingViewInternal(title, subtitle);
          }
        });
  }

  @Override
  public void updateEndView(String title, String subtitle) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            updateEndViewInternal(title, subtitle);
          }
        });
  }

  @Override
  public void updateOngoingView(String title, String subtitle) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            updateOngoingViewInternal(title, subtitle);
          }
        });
  }

  @Override
  public void endActivity(int s) {
    this.runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            endActivityInternal(s);
          }
        });
  }

  private void hideAll() {
    mCallLandingPageView.requestFocus();
    mCallLandingPageView.setVisibility(View.GONE);
    mCallOutgoingPageView.setVisibility(View.INVISIBLE);
    mCallIncomingPageView.setVisibility(View.INVISIBLE);
    mCallOngoingPageView.setVisibility(View.INVISIBLE);
    mCallEndedPageView.setVisibility(View.INVISIBLE);
  }

  private void switchToViewInternal(ICallPage.PageViewType pageViewType) {
    DLog.e("Swicth To View: " + pageViewType);
    // Toast.makeText(getApplicationContext(), "Swicth To View: "+pageViewType,
    // Toast.LENGTH_SHORT).show();
    hideAll();
    switch (pageViewType) {
      case LANDING:
        mCallLandingPageView.setVisibility(View.VISIBLE);
        mCallVideoGridView.setVisibility(View.GONE);
        break;
      case OUTGOING:
        mCallOutgoingPageView.setVisibility(View.VISIBLE);
        if (mIsCameraOpen) {
          mCallVideoGridView.setVisibility(View.VISIBLE);
          mCallVideoGridView.setlayout(CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN);
        } else {
          mCallVideoGridView.setVisibility(View.GONE);
        }
        break;
      case INCOMMING:
        mCallIncomingPageView.setVisibility(View.VISIBLE);
        mCallIncomingPageView.requestFocus();
        if (mIsCameraOpen) {
          mCallVideoGridView.setVisibility(View.VISIBLE);
          mCallVideoGridView.setlayout(
              CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN); // _VIEW_FULL_SCREEN);
        } else {
          mCallVideoGridView.setVisibility(View.GONE);
        }
        break;
      case ONGOING:
        mCallOngoingPageView.setVisibility(View.VISIBLE);
        if (mIsCameraOpen) {
          mCallVideoGridView.setVisibility(View.VISIBLE);
          mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);
        } else {
          mCallVideoGridView.setVisibility(View.GONE);
        }
        break;
      case ENDED:
        mCallEndedPageView.setVisibility(View.VISIBLE);
        mCallVideoGridView.setlayout(CallVideoGridView.Layout.HIDE_VIEW);
        mCallVideoGridView.setVisibility(View.GONE);
        break;
    }
    if (pageViewType == ICallPage.PageViewType.OUTGOING
        || pageViewType == ICallPage.PageViewType.INCOMMING) {
      mMediaPlayer.start();
    } else {
      if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
      }
    }
  }

  @SuppressLint("ResourceAsColor")
  private void showNetworkNotificationInternal(String type, String s) {
    if (type.equals("success")) {
      mNotificationView.setBackgroundResource(R.color.Notification_Success);
      new Handler()
          .postDelayed(
              new Runnable() {
                public void run() {
                  mNotificationView.setVisibility(View.GONE);
                }
              },
              5000);
    } else if (type.equals("error")) {
      mNotificationView.setBackgroundResource(R.color.Notification_Error);
    } else {
      mNotificationView.setBackgroundResource(R.color.Notification_Progress);
    }
    mNotificationView.setText(s);
    mNotificationView.setVisibility(View.VISIBLE);
  }

  private void onCameraOffInternal() {
    DLog.e("onCameraOff");
    mCallVideoGridView.setVisibility(View.GONE);
    mIsCameraOpen = false;
  }

  private void onCameraOnInternal() {
    DLog.e("onCameraOn");
    mCallVideoGridView.setVisibility(View.VISIBLE);
    mIsCameraOpen = true;
  }

  private void onRtcStatInternal(Map<String, String> reports) {
    // DLog.e(reports.toString());
  }

  private void toggleViewBasedOnVideoEnabledInternal(boolean isVideoEnabled) {
    if (isVideoEnabled) {
      mCallVideoGridView.setVisibility(View.GONE);
    } else {
      mCallVideoGridView.setVisibility(View.VISIBLE);
    }
  }

  private IContactManager.Callback mContactMangerCallback =
      new IContactManager.Callback() {
        @Override
        public void onContactListChange(List<IRtcUser> userList) {
          mCallLandingPageView.updateUserList(userList);
        }

        @Override
        public void onSingleContactChange(IRtcUser user) {}

        @Override
        public void onPresenceChange(IRtcUser user, boolean isOnline) {}

        @Override
        public void onCallListChange(List<ICallInfo> mCallInfo) {}
      };

  private void prepareCallUIIntrenal(IRtcUser peer, ICallInfo callinfo) {
    if (callinfo.getIsVideo()) {
      mCallVideoGridView.setVisibility(View.VISIBLE);
      mCallIncomingPageView.renderVideoPeerView(peer);
      mCallOutgoingPageView.renderVideoPeerView(peer);
      mCallOngoingPageView.renderVideoPeerView(peer);
    } else {
      mCallVideoGridView.setVisibility(View.GONE);
      mCallIncomingPageView.renderAudioPeerView(peer);
      mCallOutgoingPageView.renderAudioPeerView(peer);
      mCallOngoingPageView.renderAudioPeerView(peer);
    }
    mCallEndedPageView.renderAudioPeerView(peer);

    if (callinfo.getType() == ICallInfo.CallType.INCOMMING_CALL
        || callinfo.getType() == ICallInfo.CallType.MISS_CALL_INCOMMING) {
      mCallVideoGridView.setlayout(CallVideoGridView.Layout.SELF_VIEW_FULL_SCREEN);
    } else {
      mCallVideoGridView.setlayout(CallVideoGridView.Layout.PEER_VIEW_FULL_SCREEN);
    }
  }

  private void updateEndViewInternal(String title, String subtitle) {
    if (title != null) {
      mCallEndedPageView.updateTitle(title);
    }
    if (subtitle != null) {
      mCallEndedPageView.updateSubtitle(subtitle);
    }
  }

  private void updateOutgoingViewInternal(String title, String subtitle) {
    if (title != null) {
      mCallOutgoingPageView.updateTitle(title);
    }
    if (subtitle != null) {
      mCallOutgoingPageView.updateSubtitle(subtitle);
    }
  }

  private void updateIncomingViewInternal(String title, String subtitle) {
    if (title != null) {
      mCallIncomingPageView.updateTitle(title);
    }
    if (subtitle != null) {
      mCallIncomingPageView.updateSubtitle(subtitle);
    }
  }

  private void updateOngoingViewInternal(String title, String subtitle) {
    if (title != null) {
      mCallOngoingPageView.updateTitle(title);
    }
    if (subtitle != null) {
      mCallOngoingPageView.updateSubtitle(subtitle);
    }
  }

  private void endActivityInternal(int sec) {
    Handler handler = new Handler();

    handler.postDelayed(
        new Runnable() {
          public void run() {
            finish();
          }
        },
        sec * 1000);
  }
}
