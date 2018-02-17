package in.peerreview.fmradioindia.common.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import in.peerreview.fmradioindia.BuildConfig;

/** Created by dip on 2/15/18. */
public class AndroidUtils {

  // public APIS
  public static void Init(Context cx) {
    mContext = cx;
  }
  public static AndroidUtils Get() {
    if (sAndroidUtils == null) {
      sAndroidUtils = new AndroidUtils();
    }
    return sAndroidUtils;
  }


  public static boolean  isDebug() {
    return isDebugInternal();
  }
  public void rateApp() {
    rateAppInternal();
  }


  // Private
  private static Context mContext;
  private static AndroidUtils sAndroidUtils;

  private static boolean isDebugInternal() {
    if (BuildConfig.DEBUG) {
      return true;
    } else {
      return false;
    }
  }

  private void rateAppInternal() {
    Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY
            | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    try {
      mContext.startActivity(goToMarket);
    } catch (ActivityNotFoundException e) {
      mContext.startActivity(
          new Intent(
              Intent.ACTION_VIEW,
              Uri.parse(
                  "http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
    }
  }
}
