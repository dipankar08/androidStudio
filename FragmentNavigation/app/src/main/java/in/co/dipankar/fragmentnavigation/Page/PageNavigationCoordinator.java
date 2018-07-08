package in.co.dipankar.fragmentnavigation.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;

/* This component is responsible for providing details
 of where and how the app should navigate to next, given the current location and state of the app.
*/
public interface PageNavigationCoordinator {
  PageTransition<PageLocation> nextTransitionForFlow(
      @Nullable PageLocation fromLocation, @Nullable Bundle args, Bundle savedInstanceState);
}
