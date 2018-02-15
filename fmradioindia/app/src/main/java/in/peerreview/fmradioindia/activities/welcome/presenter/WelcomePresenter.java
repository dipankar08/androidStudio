package in.peerreview.fmradioindia.activities.welcome.presenter;

import android.app.Activity;
import android.util.Log;
import in.peerreview.fmradioindia.activities.radio.model.RadioNode;
import in.peerreview.fmradioindia.activities.welcome.IWelcomeContract;
import in.peerreview.fmradioindia.common.utils.Configuration;
import in.peerreview.fmradioindia.common.utils.INetwork;
import in.peerreview.fmradioindia.common.utils.Network;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Created by dip on 2/14/18. */
public class WelcomePresenter implements IWelcomeContract.Presenter {
  static final String TAG = "WelcomePresenter";
  private static final String url = Configuration.DB_ENDPOINT + "?limit=300&state=Active";
  private IWelcomeContract.View mView;
  private static List<RadioNode> mNodes;

  public WelcomePresenter(IWelcomeContract.View loginView) {
    mView = loginView;
  }

  @Override
  public void loadData() {
    final long startTime = System.currentTimeMillis();
    final List<RadioNode> nodes = new ArrayList<>();
    INetwork network = new Network(((Activity) mView).getBaseContext());
    network.retrive(
        url,
        Network.CacheControl.GET_LIVE_ELSE_CACHE,
        new Network.INetworkCallback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {
            Log.d(TAG, jsonObject.toString());
            JSONArray Jarray;
            try {
              Jarray = jsonObject.getJSONArray("out");
              for (int i = 0; i < Jarray.length(); i++) {
                JSONObject object = Jarray.getJSONObject(i);
                if (object.has("name") && object.has("url")) { // TODO
                  nodes.add(
                      new RadioNode(
                          object.optString("uid", null),
                          object.optString("name", null),
                          object.optString("img", null),
                          object.optString("url", null),
                          object.optString("tags", null),
                          object.optInt("count_error", 0),
                          object.optInt("count_success", 0),
                          object.optInt("count_click", 0),
                          RadioNode.TYPE.RADIO));
                }
              }

              Collections.sort(
                  nodes,
                  new Comparator<RadioNode>() {
                    @Override
                    public int compare(RadioNode a1, RadioNode a2) {
                      return a2.getRank() - a1.getRank();
                    }
                  });
              /*
              //adding adds
              for(int i=1;i<nodes.size();i+=10){
                  nodes.add(i,new RadioNode(0));
              }*/
              mNodes = nodes;
              /* final long endTime   = System.currentTimeMillis();
              Telemetry.sendTelemetry("data_fetch_time",  new HashMap<String, String>(){{
                  put("time",endTime - startTime+"");
              }});*/
              mView.gotoHome();

            } catch (JSONException e) {
              e.printStackTrace();
              mView.exit();
            }
          }

          @Override
          public void onError(String msg) {
            mView.exit();
          }
        });
  }

  public static List<RadioNode> getPreFetchedNode() {
    return mNodes;
  }
}
