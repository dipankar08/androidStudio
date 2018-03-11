package in.co.dipankar.ping.activities.callscreen;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IRtcUser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CallLandingPageView extends RelativeLayout{

    public interface Callback {
        void onClickAudioCallBtn();
        void onClickVideoCallBtn();
    }

    private Callback mCallback;
    private View mView;

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
        mView = mInflater.inflate(R.layout.view_call_landing_page, this, true);

        ImageButton callAudio = mView.findViewById(R.id.audio_call);
        ImageButton callVideo = mView.findViewById(R.id.video_call);

        callAudio.setOnClickListener(v1 -> mCallback.onClickAudioCallBtn());
        callVideo.setOnClickListener(v12 -> mCallback.onClickVideoCallBtn());
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }
    public void setData(IRtcUser mRtcUser) {
        TextView title = mView.findViewById(R.id.name);
        title.setText(mRtcUser.getUserName());
        TextView subtitle = mView.findViewById(R.id.subtitle);
        subtitle.setText(mRtcUser.getUserId());
    }

}