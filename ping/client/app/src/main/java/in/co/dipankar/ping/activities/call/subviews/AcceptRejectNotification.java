package in.co.dipankar.ping.activities.call.subviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.co.dipankar.ping.R;

public class AcceptRejectNotification extends RelativeLayout {

    public interface Callback {
        void onAccept();
        void onReject();
    }

    private Callback mCallback;
    private TextView mTextView;

    public AcceptRejectNotification(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public AcceptRejectNotification(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AcceptRejectNotification(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.view_accept_reject_notification, this, true);
        mTextView = findViewById(R.id.noti_text);
        Button ac = findViewById(R.id.noti_accept);
        ac.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onAccept();
            }
        });
        Button rj = findViewById(R.id.noti_reject);
        rj.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onReject();
            }
        });
    }

    public void handleNotification(String msg, Callback callback){
        mCallback = callback;
        mTextView.setText(msg);
        setVisibility(GONE);
    }

    public void hide(){
        setVisibility(GONE);
    }
}