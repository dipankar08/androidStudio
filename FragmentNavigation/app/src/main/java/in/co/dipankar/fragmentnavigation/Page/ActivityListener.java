package in.co.dipankar.fragmentnavigation.Page;

import android.os.Bundle;

public interface ActivityListener {
  public void navigateToStart(Bundle state);

  public void navigateBack(Bundle state);

  public void navigateNext(Bundle state);

  public void finish();

  public void navigateToLocation(PageLocation location, Bundle state);
}
