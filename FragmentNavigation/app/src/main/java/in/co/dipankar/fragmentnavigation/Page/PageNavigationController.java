package in.co.dipankar.fragmentnavigation.Page;

import static in.co.dipankar.fragmentnavigation.Page.PageTransition.TransitionType.REPLACING;
import static in.co.dipankar.fragmentnavigation.Page.PageTransition.TransitionType.REPLACING_ALL;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import in.co.dipankar.fragmentnavigation.R;

// It is responsible for performing the navigation between
// fragments via {@link #goForward} or {@link #goBack}.
public final class PageNavigationController {

  private static String LOCATION = "location";
  private final int mContainerViewId;
  private final FragmentManager mFragmentManager;
  private final PageFragmentProvider mPageFragmentProvider;
  private final PageNavigationCoordinator mPageNavigationCoordinator;
  private PageLocation mCurrentLocation;

  public PageNavigationController(
      @IdRes int containerViewId,
      FragmentManager fragmentManager,
      PageFragmentProvider fragmentProvider,
      PageNavigationCoordinator co) {
    mContainerViewId = containerViewId;
    mFragmentManager = fragmentManager;
    mPageFragmentProvider = fragmentProvider;
    mPageNavigationCoordinator = co;
  }

  public void setCurrentLocation(PageLocation currentLocation) {
    mCurrentLocation = currentLocation;
  }

  @Nullable
  public PageLocation getCurrentLocation() {
    return mCurrentLocation;
  }

  public boolean goBack() {
    if (mFragmentManager.getBackStackEntryCount() == 0) {
      return false;
    }
    mFragmentManager.popBackStack();
    return true;
  }

  private void moveToLocationInternal(Bundle args, Bundle savedInstanceState) {
    Fragment fragment = null;
    boolean reCreate = isRecreateOnConfigChange(savedInstanceState);
    if (reCreate) {
      String location = savedInstanceState.getString(LOCATION);
      if (location != null) {
        fragment = mFragmentManager.findFragmentByTag(location);
        mCurrentLocation = PageLocation.getLocation(location);
      }
    }
    // already exist - just return
    if (fragment != null) return;

    // ask coordinator to return next Location.
    PageTransition<PageLocation> transition =
        mPageNavigationCoordinator.nextTransitionForFlow(
            mCurrentLocation, args, savedInstanceState);
    PageLocation nextLocation = transition.getLocation();
    PageTransition.TransitionType tType = transition.getTransitionType();
    PageTransition.AnimationType aType = transition.getAnimation();

    // get the fragmnets and update the state of the fragment
    fragment = mPageFragmentProvider.getPageFragment(nextLocation, args);
    Bundle bundle = fragment.getArguments();
    if (bundle != null && args != null) {
      bundle.putAll(args);
    } else if (bundle == null) {
      bundle = args;
    }
    fragment.setArguments(bundle);

    // do the actual trasition.
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    applyTransactionAnimation(transaction, aType);
    applyTransactionType(transaction, nextLocation, fragment, tType);
    transaction.commit();

    mCurrentLocation = nextLocation;
  }

  private String getTag(PageLocation location) {
    return location.toString();
  }

  private void popAllFragments() {
    if (mFragmentManager.getBackStackEntryCount() > 0) {
      String bottomTag = mFragmentManager.getBackStackEntryAt(0).getName();
      mFragmentManager.popBackStack(bottomTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
  }

  private static boolean isRecreateOnConfigChange(@Nullable Bundle savedInstanceState) {
    return savedInstanceState != null;
  }

  private static void applyTransactionAnimation(
      FragmentTransaction transaction, PageTransition.AnimationType animation) {

    switch (animation) {
      case SLIDE_IN_OUT:
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right);
        return;
      case SLIDE_IN_OUT_REVERSE:
        transaction.setCustomAnimations(
            R.anim.slide_in_left,
            R.anim.slide_out_right,
            R.anim.slide_in_right,
            R.anim.slide_out_left);
        return;
      case FADE_IN_OUT:
        transaction.setCustomAnimations(
            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        return;
      case NONE:
      default:
        break;
    }
  }

  private void applyTransactionType(
      FragmentTransaction transaction,
      PageLocation location,
      Fragment fragment,
      PageTransition.TransitionType type) {

    String tag = getTag(location);

    if (type == REPLACING_ALL) {
      popAllFragments();
    } else if (type == REPLACING) {
      mFragmentManager.popBackStack();
    }
    // Always Replace hete
    transaction.replace(mContainerViewId, fragment, tag);
    transaction.addToBackStack(tag);
  }
}
