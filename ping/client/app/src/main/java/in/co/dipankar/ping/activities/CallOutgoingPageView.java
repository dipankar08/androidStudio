package in.co.dipankar.ping.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import in.co.dipankar.ping.R;


public class CallOutgoingPageView extends RelativeLayout implements View.OnClickListener{

    public interface Callback {
        void onClickedEnd();
        void onClickedToggleVideo(boolean isOn);
        void onClickedToggleAudio(boolean isOn);
    }

    private Callback mCallback;
    private Context mContext;
    LayoutInflater mInflater;
    private  MediaPlayer ring;

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
        ImageButton a1 = (ImageButton) v.findViewById(R.id.toggle_video);
        ImageButton a2 = (ImageButton) v.findViewById(R.id.toggle_audio);
        ImageButton a3 = (ImageButton) v.findViewById(R.id.end);
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        a3.setOnClickListener(this);
        ring = MediaPlayer.create(mContext,R.raw.tone);
        ring.setLooping(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toggle_audio:
                mCallback.onClickedToggleAudio(true);break;
            case R.id.toggle_video:
                mCallback.onClickedToggleVideo(true);break;
            case R.id.end:
                mCallback.onClickedEnd();break;
        }
    }

    public void show(){
        ring.start();
        this.setVisibility(View.VISIBLE);
    }
    public void hide(){
        ring.pause();
        this.setVisibility(View.GONE);
    }

}