package in.co.dipankar.livetv.ui.splash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Toast;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseView;
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

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mPresenter.fetchData();
  }

  private void init(Context context) {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_spalsh, this);
    mPresenter = new SplashViewPresenter();
    setPresenter(mPresenter);
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
