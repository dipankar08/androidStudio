package in.co.dipankar.ping.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import in.co.dipankar.ping.R;


public class CallOngoingPageView extends RelativeLayout implements View.OnClickListener{

    public interface Callback {
        void onClickEnd();
        void onClickToggleVideo(boolean isOn);
        void onClickToggleAudio(boolean isOn);
        void onClickToggleCamera(boolean isOn);
    }

    private Callback mCallback;
    LayoutInflater mInflater;

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public CallOngoingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallOngoingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallOngoingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_ongoing_page, this, true);
        ImageButton cam = (ImageButton) v.findViewById(R.id.toggle_camera);
        ImageButton audio = (ImageButton) v.findViewById(R.id.toggle_audio);
        ImageButton end = (ImageButton) v.findViewById(R.id.end);
        ImageButton video = (ImageButton) v.findViewById(R.id.toggle_video);
        cam.setOnClickListener(this);
        audio.setOnClickListener(this);
        end.setOnClickListener(this);
        video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toggle_audio:
                mCallback.onClickToggleAudio(true);break;
            case R.id.toggle_video:
                mCallback.onClickToggleVideo(true);break;
            case R.id.toggle_camera:
                mCallback.onClickToggleCamera(true);break;
            case R.id.end:
                mCallback.onClickEnd();break;
        }
    }

}