package in.co.dipankar.ping.activities.callscreen.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;


public class CallIncomingPageView extends RelativeLayout implements View.OnClickListener{


    public interface Callback {
        void onAccept();
        void onReject();
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
        mContext = context;
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_incomming_page, this, true);


        mPeerImage = v.findViewById(R.id.peer_img);
        mPeerBackgroud = v.findViewById(R.id.peer_back);
        mTitle =  v.findViewById(R.id.title);
        mSubTitle =  v.findViewById(R.id.subtitle);



        ImageButton accept = (ImageButton) v.findViewById(R.id.accept);
        ImageButton reject = (ImageButton) v.findViewById(R.id.reject);

        accept.setOnClickListener(this);
        reject.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept:
                mCallback.onAccept();break;
            case R.id.reject:
                mCallback.onReject();break;
        }
    }

    public void updateView(String subtitle){
        IRtcUser peer = PingApplication.Get().getPeer();
        if(peer != null){
            Glide.with(mContext)
                    .load(peer.getProfilePictureUrl())
                    .into(mPeerImage);
            Glide.with(mContext)
                    .load(peer.getCoverPictureUrl())
                    .into(mPeerBackgroud);
            mTitle.setText(peer.getUserName());
        }
        mSubTitle.setText(subtitle);
    }

}