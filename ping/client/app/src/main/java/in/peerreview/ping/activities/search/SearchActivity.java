package in.peerreview.ping.activities.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity implements ISearch.View {

  ISearch.Presenter mPresenter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(in.peerreview.ping.R.layout.activity_serach);
    mPresenter = new SearchPresenter(this);
  }
}
