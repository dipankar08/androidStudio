package in.co.dipankar.samplemvp.mainview.presenter;

/** Created by dip on 1/24/18. */
public interface IMainPresenter {
  void onAttach();

  void onItemClicked(int position);

  void onDetach();
}
