package in.co.dipankar.samplemvp.mainview.presenter;

import in.co.dipankar.samplemvp.mainview.model.IItemProvider;
import in.co.dipankar.samplemvp.mainview.view.IMainView;
import java.util.List;

/** Created by dip on 1/24/18. */
public class MainPresenter implements IMainPresenter, IItemProvider.OnFinishedListener {

  private IMainView mainView;
  private IItemProvider itemProvider;

  public MainPresenter(IMainView mainView, IItemProvider itemProvider) {
    this.mainView = mainView;
    this.itemProvider = itemProvider;
  }

  @Override
  public void onAttach() {
    if (mainView != null) {
      mainView.showProgress();
    }

    itemProvider.loadItems(this);
  }

  @Override
  public void onItemClicked(int position) {
    if (mainView != null) {
      mainView.showMessage(String.format("Position %d clicked", position + 1));
    }
  }

  @Override
  public void onDetach() {
    mainView = null;
  }

  @Override
  public void onFinished(List<String> items) {
    if (mainView != null) {
      mainView.setItems(items);
      mainView.hideProgress();
    }
  }

  public IMainView getMainView() {
    return mainView;
  }
}
