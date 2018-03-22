package in.co.dipankar.ping.activities.callscreen.subviews;

import android.content.Context;
import android.media.MediaPlayer;
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
import in.co.dipankar.quickandorid.views.StateImageButton;


public class CallOutgoingPageView extends RelativeLayout{

    public interface Callback {
        void onClickedEnd();
        void onClickedToggleVideo(boolean isOn);
        void onClickedToggleAudio(boolean isOn);
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

    public CallOutgoingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallOutgoingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallOutgoingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_outgoing_page, this, true);

        mPeerImage = v.findViewById(R.id.peer_img);
        mPeerBackgroud = v.findViewById(R.id.peer_back);
        mTitle =  v.findViewById(R.id.title);
        mSubTitle =  v.findViewById(R.id.subtitle);

        StateImageButton video =  v.findViewById(R.id.toggle_video);
        StateImageButton audio =  v.findViewById(R.id.toggle_audio);
        ImageButton end = v.findViewById(R.id.end);

        audio.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickedToggleAudio(b);
            }
        });
        video.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickedToggleVideo(b);
            }
        });
        end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickedEnd();
            }
        });
    }

    public void show(){
        this.setVisibility(View.VISIBLE);
    }
    public void hide(){
        this.setVisibility(View.GONE);
    }

    public void updateView(String subtitle){
        IRtcUser peer = PingApplication.Get().getPeer();
        if(peer != null){
            Glide.with(mContext)
                    .load(peer.getProfilePictureUrl())
                    .into(mPeerImage);
            /*
            Glide.with(mContext)
                    .load(peer.getCoverPictureUrl())
                    .into(mPeerBackgroud);
            mTitle.setText(peer.getUserName());
            */
        }
        mSubTitle.setText(subtitle);
    }
}