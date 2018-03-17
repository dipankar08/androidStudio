package in.co.dipankar.ping.activities.callscreen.subviews;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.contracts.IRtcUser;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static org.webrtc.ContextUtils.getApplicationContext;

public class CallLandingPageView extends RelativeLayout{

    public void setRecentUser() {
        prepareRecentUser();
    }

    public interface Callback {
        void onClickAudioCallBtn(IRtcUser user);
        void onClickVideoCallBtn(IRtcUser user);
    }

    private List<IRtcUser> userList = new ArrayList<>();

    private Callback mCallback;
    private RecyclerView mRecyclerView;
    private RecentUserAdapter mRecentUserAdapter;

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
        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_landing_page, this, true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecentUserAdapter = new RecentUserAdapter(userList);
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
                mCallback.onClickVideoCallBtn(userClicked);
            }
            @Override
            public void onLongClick(View view, int position) {
               //TODO
            }
        }));

        prepareRecentUser();
    }

    private void prepareRecentUser() {
        userList = PingApplication.Get().getUserManager().getRecentUserList();
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
}