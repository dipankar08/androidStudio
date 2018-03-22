package in.co.dipankar.ping.activities.callscreen.subviews;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.contracts.ICallSignalingApi;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class CallEndedPageView extends RelativeLayout {

    public interface Callback {
        void onClickClose();
        void onClickRedail();
    }

    private Callback mCallback;
    private Context mContext;
    LayoutInflater mInflater;

    TextView mTitle;
    TextView mSubTitle;
    ImageView mPeerBackgroud;
    CircleImageView mPeerImage;

    public void setCallback(Callback callback){
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
        View v = mInflater.inflate(R.layout.view_call_ended_page, this, true);
        ImageButton close =  v.findViewById(R.id.close);
        ImageButton redail =  v.findViewById(R.id.redail);

        mPeerImage = v.findViewById(R.id.peer_img);
        mPeerBackgroud = v.findViewById(R.id.peer_back);
        mTitle =  v.findViewById(R.id.title);
        mSubTitle =  v.findViewById(R.id.subtitle);

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickClose();
            }
        });
        redail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickRedail();
            }
        });
    }
    public void updateView(String title, String subtitle){
        IRtcUser peer = PingApplication.Get().getPeer();
        if(peer != null){
            Glide.with(mContext)
                    .load(peer.getProfilePictureUrl())
                    .into(mPeerImage);
            Glide.with(mContext)
                    .load(peer.getCoverPictureUrl())
                    .into(mPeerBackgroud);
        }
        mTitle.setText(title);
        mSubTitle.setText(subtitle);
    }
}