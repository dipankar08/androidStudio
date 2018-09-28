package in.co.dipankar.fmradio.ui.viewpresenter.adds;

import static in.co.dipankar.fmradio.utils.Constants.REWARDED_ADS_ID;
import static in.co.dipankar.fmradio.utils.Constants.REWARDED_ADS_ID_TEST;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager;
import in.co.dipankar.fmradio.data.configuration.ConfigurationManager.Config;
import in.co.dipankar.fmradio.ui.base.BaseNavigationActivity;
import in.co.dipankar.fmradio.utils.Constants;
import in.co.dipankar.quickandorid.utils.DLog;

public class GoogleAdsUtils {
  InterstitialAd mInterstitialAd;
  RewardedVideoAd mRewardedVideoAd;
  BaseNavigationActivity mActivity;

  public GoogleAdsUtils(BaseNavigationActivity context) {
    mActivity = context;
    initAdds();
  }

  public void initAdds() {
    boolean isTest =
        FmRadioApplication.Get().getConfigurationManager().getConfig(Config.TEST_ADD).equals(true);
    MobileAds.initialize(mActivity, isTest ? Constants.ADDMOB_APPID_TEST : Constants.ADDMOB_APPID);

    mInterstitialAd = new InterstitialAd(mActivity);
    mInterstitialAd.setAdUnitId(
        isTest ? Constants.INTERSTITIAL_ADS_ID_TEST : Constants.INTERSTITIAL_ADS_ID);
    mInterstitialAd.loadAd(new AdRequest.Builder().build());
    mInterstitialAd.setAdListener(
        new AdListener() {
          @Override
          public void onAdClosed() {
            mAdsCallback.afterShown();
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
          }

          @Override
          public void onAdLoaded() {
            DLog.d("mInterstitialAd::onAdLoaded called");
          }

          @Override
          public void onAdFailedToLoad(int var1) {
            DLog.d("mInterstitialAd::onAdFailedToLoad called");
          }
        });

    mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mActivity);
    mRewardedVideoAd.setRewardedVideoAdListener(
        new RewardedVideoAdListener() {
          @Override
          public void onRewardedVideoAdLoaded() {}

          @Override
          public void onRewardedVideoAdOpened() {}

          @Override
          public void onRewardedVideoStarted() {}

          @Override
          public void onRewardedVideoAdClosed() {
            DLog.d("onRewardedVideoAdClosed Done");
            mRewardedVideoAd.loadAd(
                isTest ? REWARDED_ADS_ID_TEST : REWARDED_ADS_ID, new AdRequest.Builder().build());
            if (mAdsCallback != null) {
              mAdsCallback.afterShown();
            }
          }

          @Override
          public void onRewarded(RewardItem rewardItem) {
            DLog.d("onRewarded Done");
          }

          @Override
          public void onRewardedVideoAdLeftApplication() {}

          @Override
          public void onRewardedVideoAdFailedToLoad(int i) {}

          @Override
          public void onRewardedVideoCompleted() {}
        });
    mRewardedVideoAd.loadAd(
        isTest ? REWARDED_ADS_ID_TEST : REWARDED_ADS_ID, new AdRequest.Builder().build());
    DLog.d("initAdds Done...");
  }

  public interface AdsCallback {
    public void afterShown();
  }

  private AdsCallback mAdsCallback;

  public void showAds(final AdsCallback callback) {
    mAdsCallback = callback;
    if (FmRadioApplication.Get()
        .getConfigurationManager()
        .getConfig(Config.ADD_FREE)
        .equals(true)) {
      callback.afterShown();
      return;
    }
    boolean videoAdd =
        FmRadioApplication.Get()
            .getConfigurationManager()
            .getConfig(ConfigurationManager.Config.VIDEO_Add)
            .equals(true);
    if (videoAdd) {
      if (mRewardedVideoAd.isLoaded()) {
        mRewardedVideoAd.show();
      } else {
        callback.afterShown();
      }
    } else {
      if (mInterstitialAd.isLoaded()) {
        mInterstitialAd.show();
      } else {
        callback.afterShown();
      }
    }
  }
}
