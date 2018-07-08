package in.co.dipankar.samplemvp.mainview.view;

import java.util.List;

/** Created by dip on 1/24/18. */
public interface IMainView {
  void showProgress();

  void hideProgress();

  void setItems(List<String> items);

  void showMessage(String message);
}
