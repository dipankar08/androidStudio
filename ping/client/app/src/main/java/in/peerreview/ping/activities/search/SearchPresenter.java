package in.peerreview.ping.activities.search;

public class SearchPresenter implements ISearch.Presenter {

  ISearch.View mView;

  SearchPresenter(ISearch.View view) {
    mView = view;
  }
}
