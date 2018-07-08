package in.co.dipankar.fragmentnavigation.Page;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

public abstract class BasePageView extends ConstraintLayout {

  private @Nullable Bundle mArguments;
  private @NonNull ActivityListener mActivityListener;

  public BasePageView(Context context) {
    super(context);
  }

  public BasePageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BasePageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  protected @NonNull ActivityListener getActivityListener() {
    return mActivityListener;
  }

  protected @Nullable Bundle getArguments() {
    return mArguments;
  }

  public final void setArguments(@Nullable Bundle args) {
    mArguments = args;
  }

  public final void setActivityListener(ActivityListener listener) {
    mActivityListener = listener;
  }
}
