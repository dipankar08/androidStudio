package in.co.dipankar.ping.activities.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.activities.call.CallActivity;
import in.co.dipankar.ping.activities.call.subviews.RecyclerTouchListener;
import in.co.dipankar.ping.common.utils.SheetItem;
import in.co.dipankar.ping.common.subview.QuickContactAdapter;
import in.co.dipankar.ping.common.subview.RecentCallAdapter;
import in.co.dipankar.ping.common.utils.CustomButtonSheetView;
import in.co.dipankar.ping.common.webrtc.RtcDeviceInfo;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.IRtcDeviceInfo;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.CustomFontTextView;

public class HomeActivity extends Activity implements IHome.View{

    private IHome.Presenter mPresenter;
    private RecyclerView quickRecyclerView;
    private RecyclerView mRecentRecyclerView;
    private CustomButtonSheetView mCustomButtonSheetView;


    private QuickContactAdapter mQuickContactAdapter;
    private RecentCallAdapter mRecentCallAdapter;

    private List<IRtcUser> mQuickUserList;
    private List<ICallInfo> mRecentCallList;
    IRtcUser mCurrentFocusUser = null;

    CustomFontTextView mNotificationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        RuntimePermissionUtils.getInstance().init(this);
        RuntimePermissionUtils.getInstance().askPermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

        }, new RuntimePermissionUtils.CallBack() {
            @Override
            public void onSuccess() {
                proceedAfterPermission();
            }

            @Override
            public void onFail() {
                finish();
            }
        });
    }

    private void initView() {
        initQuickList();
        initRecentList();
        initOtherViews();

    }

    private void initOtherViews() {
        mNotificationView = findViewById(R.id.notification);
        mCustomButtonSheetView = findViewById(R.id.custom_button_sheetview);
        List<CustomButtonSheetView.ISheetItem> mSheetItems = new ArrayList<>();
        mSheetItems.add( new SheetItem(100, "Audio Call", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToCallViewInternal(false,mCurrentFocusUser, ICallInfo.ShareType.AUDIO_CALL,null, null);
                mCustomButtonSheetView.hide();
            }
        }));
        mSheetItems.add( new SheetItem(101, "Video Call", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToCallViewInternal(false,mCurrentFocusUser, ICallInfo.ShareType.VIDEO_CALL,null, null);
                mCustomButtonSheetView.hide();
            }
        }));
        mSheetItems.add( new SheetItem(102, "Screen share", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToCallViewInternal(false,mCurrentFocusUser, ICallInfo.ShareType.SCREEN_SHARE,null, null);
                mCustomButtonSheetView.hide();
            }
        }));
        mSheetItems.add( new SheetItem(102, "Stream local file", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToCallViewInternal(false,mCurrentFocusUser, ICallInfo.ShareType.VIDEO_SHARE,null, null);
                mCustomButtonSheetView.hide();
            }
        }));
        mSheetItems.add( new SheetItem(102, "Share Music", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DLog.e("Share music Clicked");
                mCustomButtonSheetView.hide();
            }
        }));
        mCustomButtonSheetView.addMenu(mSheetItems);
        mCustomButtonSheetView.hide();
    }

    private void initQuickList(){
        mQuickUserList = new ArrayList<>();
        quickRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_quick);
        mQuickContactAdapter = new QuickContactAdapter(this, mQuickUserList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        quickRecyclerView.setLayoutManager(mLayoutManager);
        quickRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quickRecyclerView.setAdapter(mQuickContactAdapter);
        quickRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                quickRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                IRtcUser userClicked  = mQuickUserList.get(position);
                DLog.e("RV Cliecked"+position);
                navigateToCallViewInternal(false, userClicked, ICallInfo.ShareType.AUDIO_CALL,null, null);
            }
            @Override
            public void onLongClick(View view, int position) {
                mCurrentFocusUser = mQuickUserList.get(position);
                mCustomButtonSheetView.show();
            }
        }));
    }

    private void initRecentList(){
        mRecentCallList = new ArrayList<>();
        mRecentRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_recent);
        mRecentCallAdapter = new RecentCallAdapter(this, mRecentCallList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecentRecyclerView.setLayoutManager(mLayoutManager);
        mRecentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecentRecyclerView.setAdapter(mRecentCallAdapter);
        mRecentRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecentRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                ICallInfo call  = mRecentCallList.get(position);
                DLog.e("RV Cliecked"+position);
            }
            @Override
            public void onLongClick(View view, int position) {
                mCurrentFocusUser = mQuickUserList.get(position);
                mCustomButtonSheetView.show();
            }
        }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        proceedAfterPermission();
    }

    private void proceedAfterPermission() {
        Intent intent = getIntent();
        IRtcUser mRtcUser = (IRtcUser) intent.getSerializableExtra("RtcUser");
        String deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        IRtcDeviceInfo mRtcDeviceInfo= new RtcDeviceInfo(deviceid,android.os.Build.MODEL,"10");
        PingApplication.Get().setMe(mRtcUser);
        PingApplication.Get().setDevice(mRtcDeviceInfo);
        //Thsi sould be created at end
        mPresenter = new HomePresenter(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void showNetworkNotification(String type, String s) {
        if(type.equals("success")){
            mNotificationView.setBackgroundResource(R.color.Notification_Success);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mNotificationView.setVisibility(View.GONE);
                }
            }, 15000);
        } else  if(type.equals("error")){
            mNotificationView.setBackgroundResource(R.color.Notification_Error);
        } else{
            mNotificationView.setBackgroundResource(R.color.Notification_Progress);
        }
        mNotificationView.setText(s);
        mNotificationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateQuickUserView(List<IRtcUser> userList) {
        if(userList != null) {
            mQuickUserList = userList;
            mQuickContactAdapter.updateList(mQuickUserList);
        }
    }

    @Override
    public void updateRecentCallView(List<ICallInfo> mCallInfo) {
        if(mCallInfo != null) {
            mRecentCallList = mCallInfo;
            mRecentCallAdapter.updateList(mRecentCallList);
        }
    }

    @Override
    public void navigateToInComingCallView(String callId, SessionDescription sdp, IRtcUser rtcUser, boolean isVideoEnabled) {
        ICallInfo.ShareType shareType = isVideoEnabled? ICallInfo.ShareType.VIDEO_CALL : ICallInfo.ShareType.AUDIO_CALL;
        navigateToCallViewInternal(true, rtcUser,shareType, callId,sdp);
    }

    private void navigateToCallViewInternal(boolean isComing, IRtcUser peer, ICallInfo.ShareType shareType, String callId, SessionDescription sdp) {
        Intent myIntent = new Intent(this, CallActivity.class);
        myIntent.putExtra("isComing", isComing);
        myIntent.putExtra("peer", peer);
        myIntent.putExtra("shareType", shareType.mType);
        if(isComing) {
            myIntent.putExtra("sdp_data", sdp.description);
            myIntent.putExtra("sdp_type", sdp.type.toString());
            myIntent.putExtra("callId", callId); //for incomming ..
        }
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    // Logic to  Press ed back
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            super.onBackPressed();
        } else {
            this.backPressedToExitOnce = true;
            showToast("Press again to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 500);
        }
    }
    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }
    private void killToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }
    @Override
    public void onPause() {
        killToast();
        super.onPause();
    }
}