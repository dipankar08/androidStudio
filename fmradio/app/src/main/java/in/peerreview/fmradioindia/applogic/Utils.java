package in.peerreview.fmradioindia.applogic;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import in.peerreview.fmradioindia.newui.MyApplication;

public class Utils {
  public static String getVersionString() {
    try {
      PackageInfo pInfo =
          MyApplication.Get()
              .getPackageManager()
              .getPackageInfo(MyApplication.Get().getPackageName(), 0);
      String version = pInfo.versionName;
      int no = pInfo.versionCode;
      return "Bengali FM: V-" + no + " (" + version + ")";
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "Unknown Version";
  }
}
