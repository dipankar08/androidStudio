package in.co.dipankar.ping.activities.callscreen;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.ICallSignalingApi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CallEndedPageView extends RelativeLayout {

    public void setEndString(ICallSignalingApi.EndCallType type, String reason) {
        if(mType != null){
            switch (type){
                case BUSY:
                    mType.setText("User is busy with other call as this moments!");
                    break;
                case OTHER:
                    mType.setText("Can't connect call due to internal error!");
                    break;
                case USER_REJECT:
                    mType.setText("User reject your call!");
                    break;
                case OFFLINE:
                    mType.setText("User is offline !");
            }
        }
    }

    public interface Callback {
        void onClickClose();
        void onClickRedail();
    }

    private Callback mCallback;
    LayoutInflater mInflater;
    TextView mType;

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
        View v = mInflater.inflate(R.layout.view_call_ended_page, this, true);
        ImageButton close =  v.findViewById(R.id.close);
        ImageButton redail =  v.findViewById(R.id.redail);

        mType =  v.findViewById(R.id.subtitle);

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
}