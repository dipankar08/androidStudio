package in.peerreview.fmradioindia.activities.radio;

import android.content.Context;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.DialogUtils;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.Constants;
import in.peerreview.fmradioindia.common.HelperToBeMovedToLib;

public class RadioHelper {
  public final String PREF_FLAG_RATE_DIALOG = "flag_rate_dialog";
  private final String PREF_PLAY_COUNT = "COUNT_OF_RATE_DIALOG_SHOWN";
  private final String GK_VERSION_STR = "GK_VERSION_STR";
  private Context mContext;
  private SharedPrefsUtil mSharedPrefsUtil;

  public RadioHelper(Context context) {
    mContext = context;
    mSharedPrefsUtil = SharedPrefsUtil.getInstance();
  }

  public void shouldShowRateDialog(boolean force) {
    if (mSharedPrefsUtil.getBoolean(PREF_FLAG_RATE_DIALOG, Boolean.TRUE) || force) {
      FMRadioIndiaApplication.Get().getTelemetry().markHit(Constants.TELEMETRY_SHOW_MENU_RATE_AUTO);
      DialogUtils.showPlayStoreRateAlert(
          mContext,
          new DialogUtils.PlayStoreRateDialogCallback() {
            @Override
            public void onSubmit() {
              mSharedPrefsUtil.setBoolean(PREF_FLAG_RATE_DIALOG, Boolean.FALSE);
            }

            @Override
            public void onDismiss() {}
          });
    }
  }

  public void incrementPlayCount() {
    int play_count = mSharedPrefsUtil.getInt(PREF_PLAY_COUNT, 0) + 1;
    mSharedPrefsUtil.setInt(PREF_PLAY_COUNT, play_count);
    if (play_count % 200 == 20) {
      shouldShowRateDialog(false);
    }
    if (play_count % 50 == 10) {
      shouldShowUpgradeDialog();
    }
  }

  public void shouldShowUpgradeDialog() {

    int var = HelperToBeMovedToLib.getVersion(mContext);

    final String gkString =
        FMRadioIndiaApplication.Get().getGateKeeperUtils().getRemoteSetting(GK_VERSION_STR, null);
    if (gkString == null) {
      return;
    }
    // check if the version is ok.
    int var2 = 0;
    try {
      var2 = Integer.parseInt(gkString);
    } catch (Exception e) {
      DLog.e("NumberFormatException at shouldShowUpgradeDialog" + e.getMessage());
      return;
    }

    if (var2 <= var) {
      return;
    }

    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .markHit(Constants.TELEMETRY_SHOW_MENU_UPGRADE_AUTO);
    DialogUtils.showAppUpgradeAlert(
        mContext,
        new DialogUtils.AppUpgradeCallback() {
          @Override
          public void onSubmit() {}

          @Override
          public void onDismiss() {}
        });
  }

  public boolean isQuickListEnabled() {
    return FMRadioIndiaApplication.Get()
        .getGateKeeperUtils()
        .isFeatureEnabled("GK_QUICK_LIST", false);
  }
}
