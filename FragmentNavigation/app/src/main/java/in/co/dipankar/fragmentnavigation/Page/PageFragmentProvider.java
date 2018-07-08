package in.co.dipankar.fragmentnavigation.Page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public interface PageFragmentProvider {
  public Fragment getPageFragment(PageLocation location, @Nullable Bundle state);
}
