package in.peerreview.fmradioindia.activities.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.home.IHomeContract;
import in.peerreview.fmradioindia.activities.home.presenter.HomePresenter;
import in.peerreview.fmradioindia.activities.player.view.PlayerActivity;
import in.peerreview.fmradioindia.common.Constants;
import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.views.MusicHozListView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements IHomeContract.View {

  private static final String TAG = "MainActivity";
  private IHomeContract.Presenter presenter;
  private List<PinnedSetach> pinnedSetachList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new HomePresenter(this);
    setContentView(R.layout.activity_home);
    initViews();
  }

  private void initViews() {
    // update versions.
    pinnedSetachList = new ArrayList<>();
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_RECENT,
            Constants.PINNED_SERACH_KEYWORD_RECENT,
            (MusicHozListView) findViewById(R.id.recent)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_TREADING,
            Constants.PINNED_SERACH_KEYWORD_TREADING,
            (MusicHozListView) findViewById(R.id.trading)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_BENGALI_MOVIE,
            Constants.PINNED_SERACH_KEYWORD_BENGALI_MOVIE,
            (MusicHozListView) findViewById(R.id.bengali_movie)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_HINDI_MOVIE,
            Constants.PINNED_SERACH_KEYWORD_HINDI_MOVIE,
            (MusicHozListView) findViewById(R.id.hindi_movie)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_RADIO,
            Constants.PINNED_SERACH_KEYWORD_RADIO,
            (MusicHozListView) findViewById(R.id.radio)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_BENGALI_OLD,
            Constants.PINNED_SERACH_KEYWORD_BENGALI_OLD,
            (MusicHozListView) findViewById(R.id.bengali_old)));
    pinnedSetachList.add(
        new PinnedSetach(
            Constants.PINNED_SERACH_TITLE_BENGALI_BAND,
            Constants.PINNED_SERACH_KEYWORD_BENGALI_BAND,
            (MusicHozListView) findViewById(R.id.bengali_band)));

    for (final PinnedSetach entry : pinnedSetachList) {
      MusicHozListView view = entry.getView();
      String title = entry.getTitle();
      final String serach_keyword = entry.getSearch_keyword();
      view.prepareView(
          title.toUpperCase() + " SONGS",
          "Play all",
          new MusicHozListView.IMusicHozListViewCallback() {
            @Override
            public void onButtonClicked() {
              moveToPlayerActivity(serach_keyword, 0);
            }

            @Override
            public void onItemClicked(int pos) {
              moveToPlayerActivity(serach_keyword, pos);
            }
          });
      presenter.loadAlbum(serach_keyword);
    }
  }

  private void moveToPlayerActivity(String name, int start) {
    Intent intent = new Intent(this, PlayerActivity.class);
    Bundle bundle = new Bundle();
    List<MusicNode> node = presenter.getData(name);
    bundle.putParcelableArrayList("mylist", (ArrayList<MusicNode>) presenter.getData(name));
    bundle.putInt("start", start);
    intent.putExtras(bundle);
    this.startActivity(intent);
  }

  @Override
  public void renderItem(String type, List<MusicNode> list) {
    for (PinnedSetach entry : pinnedSetachList) {
      if (entry.getSearch_keyword().equals(type)) {
        entry.getView().updateData(list);
      }
    }
  }

  private class PinnedSetach {
    String title;
    String search_keyword;
    MusicHozListView view;

    public PinnedSetach(String title, String search_keyword, MusicHozListView view) {
      this.title = title;
      this.search_keyword = search_keyword;
      this.view = view;
    }

    public String getTitle() {
      return title;
    }

    public String getSearch_keyword() {
      return search_keyword;
    }

    public MusicHozListView getView() {
      return view;
    }
  };
}
