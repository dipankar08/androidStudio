package in.co.dipankar.fragmentnavigation.Page;

public final class PageTransition<PageLocation> {

  public enum TransitionType {
    APPENDING,
    REPLACING,
    REPLACING_ALL,
  }

  public enum AnimationType {
    NONE,
    SLIDE_IN_OUT,
    SLIDE_IN_OUT_REVERSE,
    FADE_IN_OUT
  }

  private final PageLocation mLocation;
  private final TransitionType mTransitionType;
  private final AnimationType mAnimationType;

  public PageTransition(
      PageLocation location, TransitionType transitionType, AnimationType transitionAnimation) {
    mLocation = location;
    mTransitionType = transitionType;
    mAnimationType = transitionAnimation;
  }

  public PageLocation getLocation() {
    return mLocation;
  }

  public TransitionType getTransitionType() {
    return mTransitionType;
  }

  public AnimationType getAnimation() {
    return mAnimationType;
  }
}
