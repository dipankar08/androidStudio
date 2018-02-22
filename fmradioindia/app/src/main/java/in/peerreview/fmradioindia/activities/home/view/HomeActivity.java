package in.peerreview.fmradioindia.activities.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.home.IHomeContract;
import in.peerreview.fmradioindia.activities.home.presenter.HomePresenter;
import in.peerreview.fmradioindia.activities.player.view.PlayerActivity;
import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.views.MusicHozListView;


public class HomeActivity extends AppCompatActivity implements IHomeContract.View {

    private static final String TAG = "MainActivity";
    private IHomeContract.Presenter presenter;
    private MusicHozListView mMusicHozListView;
    private MusicHozListView mMusicHozListView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new HomePresenter(this);
        setContentView(R.layout.activity_home);
        initViews();
    }

    private void initViews() {
        // update versions.
        mMusicHozListView = (MusicHozListView)findViewById(R.id.belgali);
        mMusicHozListView2 = (MusicHozListView)findViewById(R.id.hindi);

        mMusicHozListView.prepareView("Latest Bengali Music", "Play all", new MusicHozListView.IMusicHozListViewCallback() {
            @Override
            public void onButtonClicked() {
                moveToPlayerActivity("benagli", 0);

            }

            @Override
            public void onItemClicked(int pos) {
                moveToPlayerActivity("benagli", pos);

            }

        });

        mMusicHozListView2.prepareView("Latest Hindi Music", "Play all", new MusicHozListView.IMusicHozListViewCallback() {
            @Override
            public void onButtonClicked() {
                moveToPlayerActivity("hindi", 0);
            }

            @Override
            public void onItemClicked(int pos) {
                moveToPlayerActivity("hindi", pos);
            }
        });

        presenter.loadAlbum("bengali");
        presenter.loadAlbum("hindi");

    }

    private void moveToPlayerActivity(String name, int start) {
        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle bundle = new Bundle();
        List<MusicNode> node = presenter.getData(name);
        bundle.putParcelableArrayList("mylist", (ArrayList<MusicNode>) presenter.getData(name));
        bundle.putInt("start",start);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    @Override
    public void renderItem(String type, List<MusicNode> list) {

        if(type.equals("bengali")){
            mMusicHozListView.updateData(list);
        }
        if(type.equals("hindi")){
            mMusicHozListView2.updateData(list);
        }
    }
}