package in.peerreview.fmradioindia.ui.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.R;
import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity implements BaseView {

  @Inject HomePresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onPause() {
    mPresenter.detachView();
    super.onPause();
  }

  @Override
  protected void onResume() {
    mPresenter.attachView(this);
    super.onResume();
  }

  @Override
  public void render(BaseViewState baseViewState) {}
}
