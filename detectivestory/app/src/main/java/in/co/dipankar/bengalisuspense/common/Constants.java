package in.co.dipankar.bengalisuspense.common;

import in.co.dipankar.bengalisuspense.BuildConfig;

public class Constants {

  private static final String ORIG_ADMOB_ID = "ca-app-pub-6413024436378029~8938660178";
  private static final String ORIG_ADMOB_UNIT_ID = "ca-app-pub-6413024436378029/7323709062";

  private static final String TEST_ADMOB_ID = "ca-app-pub-3940256099942544~3347511713";
  private static final String TEST_ADMOB_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

  public static String getAdmobAdId() {
    return BuildConfig.DEBUG ? TEST_ADMOB_ID : ORIG_ADMOB_ID;
  }

  public static String getAdmobAdUnitId() {
    return BuildConfig.DEBUG ? TEST_ADMOB_UNIT_ID : ORIG_ADMOB_UNIT_ID;
  }
}
