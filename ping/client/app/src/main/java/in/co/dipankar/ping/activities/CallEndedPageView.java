package in.co.dipankar.ping.activities;

import in.co.dipankar.ping.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CallEndedPageView extends RelativeLayout implements View.OnClickListener{

    public interface Callback {
        void onClickAudioCallBtn();
        void onClickVideoCallBtn();
    }

    private Callback mCallback;
    LayoutInflater mInflater;

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
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_landing_page, this, true);
        ImageButton callAudio = (ImageButton) v.findViewById(R.id.audio_call);
        ImageButton callVideo = (ImageButton) v.findViewById(R.id.video_call);
        callAudio.setOnClickListener(this);
        callVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_call:
                mCallback.onClickAudioCallBtn();break;
            case R.id.video_call:
                mCallback.onClickVideoCallBtn();break;
        }
    }

}