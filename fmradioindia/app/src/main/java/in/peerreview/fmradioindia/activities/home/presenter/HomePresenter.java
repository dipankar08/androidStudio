package in.peerreview.fmradioindia.activities.home.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import in.peerreview.fmradioindia.activities.home.IHomeContract;
import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.utils.Configuration;
import in.peerreview.fmradioindia.common.utils.Network;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomePresenter implements IHomeContract.Presenter {
  static final String TAG = "HomePresenter";

  private static final String url = Configuration.DB_ENDPOINT + "?limit=300&state=Active";
  private IHomeContract.View mView;

  private static HashMap<String, List<MusicNode>> mMusicMap;

  public HomePresenter(IHomeContract.View loginView) {
    mView = loginView;
    mMusicMap = new HashMap<>();
  }

  @Override
  public void loadAlbum(final String type) {
    String url = Configuration.MUSIC_ENDPOINT + "?limit=30&album_name=" + type;
    Network.getInstance()
        .retrive(
            url,
            Network.CacheControl.GET_CACHE_ELSE_LIVE,
            new Network.INetworkCallback() {
              @Override
              public void onSuccess(JSONObject jsonObject) {
                try {
                  final List<MusicNode> list = new ArrayList<>();
                  JSONArray output = jsonObject.getJSONArray("out");
                  for (int i = 0; i < output.length(); i++) {
                    JSONObject js = output.getJSONObject(i);
                    list.add(
                        new MusicNode(
                            js.getString("uid"),
                            js.getString("title"),
                            js.getString("subtitle"),
                            js.getString("media_url"),
                            js.getString("image_url")));
                  }
                  if (list.size() > 0) {
                    mMusicMap.put(type, list);

                    new Handler(Looper.getMainLooper())
                        .post(
                            new Runnable() {
                              @Override
                              public void run() {
                                mView.renderItem(type, list);
                              }
                            });
                  }
                } catch (Exception e) {
                  Log.d("DIPANKAR", e.getMessage());
                }
              }

              @Override
              public void onError(String msg) {}
            });
  }

  public List<MusicNode> getData(String name) {
    return mMusicMap.get(name);
  }
}
