package in.co.dipankar.ping.activities.callscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import in.co.dipankar.ping.R;
import in.co.dipankar.quickandorid.views.MultiStateImageButton;
import in.co.dipankar.quickandorid.views.StateImageButton;


public class CallOngoingPageView extends RelativeLayout {

    public interface Callback {
        void onClickEnd();
        void onClickToggleVideo(boolean isOn);
        void onClickToggleAudio(boolean isOn);
        void onClickToggleCamera(boolean isOn);
        void onClickToggleLayout(int idx);
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
        StateImageButton cam =  v.findViewById(R.id.toggle_camera);
        StateImageButton audio =  v.findViewById(R.id.toggle_audio);
        ImageButton end =  v.findViewById(R.id.end);
        StateImageButton video =  v.findViewById(R.id.toggle_video);
        MultiStateImageButton layout =  v.findViewById(R.id.toggle_layout);

        cam.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickToggleCamera(b);
            }
        });
        audio.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickToggleAudio(b);
            }
        });
        video.setCallBack(new StateImageButton.Callback(){
            @Override
            public void click(boolean b) {
                mCallback.onClickToggleVideo(b);
            }
        });
        end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickEnd();
            }
        });

        layout.setCallBack(new MultiStateImageButton.Callback() {
            @Override
            public void click(int i) {
                mCallback.onClickToggleLayout(i);
            }
        });
    }

}