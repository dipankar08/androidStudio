package in.co.dipankar.ping.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import in.co.dipankar.ping.R;


public class CallIncommingPageView extends RelativeLayout implements View.OnClickListener{

    public interface Callback {
        void onAccept();
        void onReject();
    }

    private Callback mCallback;
    LayoutInflater mInflater;

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public CallIncommingPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CallIncommingPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CallIncommingPageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.view_call_incomming_page, this, true);
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

}