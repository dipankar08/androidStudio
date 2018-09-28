package in.co.dipankar.fmradio.ui.base;

import android.os.Bundle;

public interface Navigation {
  void goBack();

  void goBackAfterShowingAds();

  void navigate(
      Screen screen,
      Bundle bundle,
      boolean isReplace /*is replace or add */,
      boolean shouldBackStack);

  void navigate(Screen screen, Bundle bundle);

  void navigateAfterShowingAdd(Screen screen, Bundle bundle);

  void gotoHome();
}
