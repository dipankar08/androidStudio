package in.co.dipankar.fmradio.ui.viewpresenter.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.quickandorid.utils.DLog;

public class SplashView extends BaseView implements SplashViewPresenter.ViewContract {
  SplashViewPresenter mPresenter;

  public SplashView(Context context) {
    super(context);
    init(context);
  }

  public SplashView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(getContext()).inflate(R.layout.view_spalsh, this);
    mPresenter = new SplashViewPresenter();
    setPresenter(mPresenter);
    mPresenter.fetchData();

    ImageView iv = (ImageView) findViewById(R.id.logo);
    ObjectAnimator scaleDown =
        ObjectAnimator.ofPropertyValuesHolder(
            iv,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f));
    scaleDown.setDuration(310);
    scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
    scaleDown.start();
  }

  @Override
  public void onFetchSuccess() {
    DLog.d("SplashView::onFetchSuccess called");
    getNavigation().gotoHome();
  }

  @Override
  public void onFetchFailed() {
    DLog.d("SplashView::onFetchFailed called");
    Toast.makeText(getContext(), "Internal Error, Not able to Fetch Live data", Toast.LENGTH_SHORT)
        .show();
  }
}
