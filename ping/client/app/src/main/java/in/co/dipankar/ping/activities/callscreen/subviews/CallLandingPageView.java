package in.co.dipankar.ping.activities.callscreen.subviews;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.co.dipankar.quickandorid.views.StateImageButton;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static org.webrtc.ContextUtils.getApplicationContext;

public class CallLandingPageView extends RelativeLayout{

    public interface Callback {
        void onClickAudioCallBtn(IRtcUser user);
        void onClickVideoCallBtn(IRtcUser user);
        void onClickPokeBtn(IRtcUser user);
    }

    private List<IRtcUser> userList = new ArrayList<>();

    private Context mContext;
    private Callback mCallback;
    private RecyclerView mRecyclerView;
    private RecentUserAdapter mRecentUserAdapter;

    StateImageButton mAudio, mVideo, mPoke;

    UserInfoView mPeerInfo;

    IRtcUser mSelectedUser = null;


    public CallLandingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallLandingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallLandingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_landing_page, this, true);

        mPeerInfo =  v.findViewById(R.id.peer_info);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecentUserAdapter = new RecentUserAdapter(context, userList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecentUserAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(),
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                IRtcUser userClicked  = userList.get(position);
                mPeerInfo.updateView(userClicked);
                mSelectedUser = userClicked;
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAudio  = findViewById(R.id.start_audio_call);
        mVideo  = findViewById(R.id.start_video_call);
        mPoke  = findViewById(R.id.start_poke);

        mAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickAudioCallBtn(mSelectedUser);
            }
        });
        mVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickVideoCallBtn(mSelectedUser);
            }
        });
        mPoke.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickPokeBtn(mSelectedUser);
            }
        });
    }

    private void refreshInternal() {
        userList = PingApplication.Get().getUserManager().getRecentUserList();
        assert(userList!= null);
        mRecentUserAdapter.updateUserList(userList);
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public void setVisibility(boolean isVisible){
        if(isVisible){

        } else{
            mRecyclerView.setVisibility(GONE);
        }
    }
    public void updateView(){
        refreshInternal();
    }
}