package in.co.dipankar.ping.activities.call.subviews;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;
public class UserInfoView extends RelativeLayout {

    public interface Callback {
        void onClick();
    }

    private UserInfoView.Callback mCallback;

    private Context mContext;
    LayoutInflater mInflater;

    RelativeLayout mPeerInfo;
    public TextView mTitle;
    public TextView mSubTitle;
    public ImageView mPeerBackgroud;
    CircleImageView mPeerImage;

     public UserInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public UserInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public UserInfoView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_userinfo, this, true);
        mPeerInfo =  v.findViewById(R.id.peer_info);
        mPeerImage = v.findViewById(R.id.peer_img);
        mPeerBackgroud = v.findViewById(R.id.peer_back);
        mTitle =  v.findViewById(R.id.title);
        mSubTitle =  v.findViewById(R.id.subtitle);
        mContext = context;
    }
    public void updateView(IRtcUser peer) {
        if (peer != null) {
            Glide.with(mContext).load(peer.getProfilePictureUrl()).into(mPeerImage);
            Glide.with(mContext).load(peer.getCoverPictureUrl()).fitCenter().into(mPeerBackgroud);
            mPeerBackgroud.setColorFilter(0x50000000, PorterDuff.Mode.SRC_ATOP);
            mTitle.setText("Welcome "+ peer.getUserName() + "!");
        }
    }

    public void updateStatus(String status){
        mSubTitle.setText(status);
    }
    public void setCallback(UserInfoView.Callback callback){
        mCallback = callback;
    }

}