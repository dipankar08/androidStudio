package in.co.dipankar.fmradio.ui.viewpresenter.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager.Config;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import java.util.List;

public class SettingView extends BaseView implements SettingViewPresenter.ViewContract {
  SettingViewPresenter mPresenter;

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

    final TextView mSetting = findViewById(R.id.profile_title);
    final LinearLayout mInternalSettings = findViewById(R.id.internal);
    mSetting.setOnLongClickListener(
        new OnLongClickListener() {
          @Override
          public boolean onLongClick(View view) {
            mInternalSettings.setVisibility(VISIBLE);
            return true;
          }
        });

    Switch refresh = findViewById(R.id.refresh);
    refresh.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            FmRadioApplication.Get()
                .getRadioManager()
                .fetchData(
                    new RadioManager.RadioManagerCallback() {
                      @Override
                      public void onSuccess(List<Radio> radio) {
                        Toast.makeText(
                                getContext(), "Updated the list successfully ", Toast.LENGTH_SHORT)
                            .show();
                      }

                      @Override
                      public void onFail(String msg) {
                        Toast.makeText(
                                getContext(), "Failed to update the list.", Toast.LENGTH_SHORT)
                            .show();
                      }
                    });
          }
        });

    // live tv.
    bind(findViewById(R.id.live_tv), Config.ENABLE_TV);
    bind(findViewById(R.id.add_free), Config.ADD_FREE);
  }

  private void bind(Switch switch1, Config config) {
    switch1.setChecked(
        FmRadioApplication.Get().getConfigurationManager().getConfig(config).equals(true));
    switch1.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            FmRadioApplication.Get().getConfigurationManager().updateConfig(config, b);
          }
        });
  }
}
