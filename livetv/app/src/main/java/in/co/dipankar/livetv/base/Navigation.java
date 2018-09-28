package in.co.dipankar.livetv.base;

import android.os.Bundle;

public interface Navigation {
  void goBack();

  void navigate(
      Screen screen,
      Bundle bundle,
      boolean isReplace /*is replace or add */,
      boolean shouldBackStack);

  void navigate(Screen screen, Bundle bundle);

  void gotoHome();
}
