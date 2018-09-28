package in.co.dipankar.fmradio.ui.viewpresenter.adds;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.utils.Constants;

public class BannerAdsView extends BaseView {
  private AdView mAdView;

  public BannerAdsView(Context context) {
    super(context);
    init();
  }

  public BannerAdsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BannerAdsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mAdView = new AdView(getContext());
    mAdView.setAdUnitId(
        FmRadioApplication.Get()
                .getConfigurationManager()
                .getConfig(ConfigurationManager.Config.TEST_ADD)
                .equals(true)
            ? Constants.BANNER_ADS_ID_TEST
            : Constants.BANNER_ADS_ID);
    mAdView.setAdSize(AdSize.BANNER);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);
    mAdView.setAdListener(new AdListener() {});

    addView(mAdView);
    setGravity(CENTER_VERTICAL);
  }
}
