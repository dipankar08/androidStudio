package in.peerreview.ping.activities.bell;

/** Created by dip on 4/21/18. */
public class BellPresenter implements IBell.Presenter {

  IBell.View mView;

  public BellPresenter(IBell.View view) {
    mView = view;
  }

  @Override
  public void accept() {}

  @Override
  public void reject() {}

  @Override
  public void resend() {}

  @Override
  public void finish() {}
}
