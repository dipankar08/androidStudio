package in.co.dipankar.fmradio.ui.viewpresenter.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager.Config;
import in.co.dipankar.fmradio.ui.base.BaseView;


public class SettingView extends BaseView implements SettingViewPresenter.ViewContract {
    SettingViewPresenter mPresenter;
    Switch mLiveTvSwitch;
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
/*
        final TextView mSetting = findViewById(R.id.page_title);
        final LinearLayout mInternalSettings = findViewById(R.id.internal);
        mSetting.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mInternalSettings.setVisibility(VISIBLE);
                return true;
            }
        });
*/
        // live tv.
        mLiveTvSwitch = findViewById(R.id.live_tv);
        mLiveTvSwitch.setChecked(FmRadioApplication.Get().getConfigurationManager().getConfig(Config.ENABLE_TV).equals(true));
        mLiveTvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FmRadioApplication.Get().getConfigurationManager().updateConfig(ConfigurationManager.Config.ENABLE_TV, b);
            }
        });

    }
}
