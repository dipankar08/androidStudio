package in.co.dipankar.fmradio.ui.viewpresenter.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;


public class SettingView extends BaseView implements SettingViewPresenter.ViewContract {
    SettingViewPresenter mPresenter;
    ImageView mBack;
    public SettingView(Context context) {
        super(context);
        init(context);
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_setting, this);
        mPresenter = new SettingViewPresenter();
        setPresenter(mPresenter);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavigation().goBack();
            }
        });
    }
}
