package in.peerreview.fmradioindia.activities.welcome;

import android.Manifest;
import android.content.Context;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.Configuration;
import in.peerreview.fmradioindia.common.models.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomePresenter implements IWelcomeContract.Presenter {
  private final String url = Configuration.DB_ENDPOINT + "?limit=300&state=Active";
  private IWelcomeContract.View mView;

  public WelcomePresenter(IWelcomeContract.View loginView) {
    mView = loginView;
  }

  @Override
  public void loadData() {
    RuntimePermissionUtils.getInstance().init((Context) mView);
    RuntimePermissionUtils.getInstance()
        .askPermission(
            new String[] {
              Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            },
            new RuntimePermissionUtils.CallBack() {
              @Override
              public void onSuccess() {
                loadDataInternal();
              }

              @Override
              public void onFail() {
                mView.showPermissionDialog();
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit("exit_for_RuntimePermissionUtils_deny");
                mView.exit();
              }
            });
  }

  private void loadDataInternal() {
    final long startTime = System.currentTimeMillis();
    final List<Node> nodes = new ArrayList<>();
    FMRadioIndiaApplication.Get()
        .getNetwork()
        .retrive(
            url,
            Network.CacheControl.GET_LIVE_ELSE_CACHE,
            new Network.Callback() {
              @Override
              public void onSuccess(JSONObject jsonObject) {
                DLog.d("Data Received:" + jsonObject.length());
                JSONArray Jarray;
                try {
                  Jarray = jsonObject.getJSONArray("out");
                  for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject object = Jarray.getJSONObject(i);
                    if (object.has("name") && object.has("url")) {

                      nodes.add(
                          new Node(
                              object.optString("uid", null),
                              object.optString("name", null),
                              object.optString("img", null),
                              object.optString("url", null),
                              object.optString("tags", null),
                              object.optInt("count_error", 0),
                              object.optInt("count_success", 0),
                              object.optInt("count_click", 0),
                              object.optInt("rank", 5),
                              Node.Type.RADIO));
                    }
                  }
                  Collections.sort(
                      nodes,
                      new Comparator<Node>() {
                        @Override
                        public int compare(Node a1, Node a2) {
                          return a2.getCount() - a1.getCount();
                        }
                      });
                  FMRadioIndiaApplication.Get().getNodeManager().updateList(nodes);
                  mView.gotoHome();
                } catch (JSONException e) {
                  e.printStackTrace();
                  FMRadioIndiaApplication.Get()
                      .getTelemetry()
                      .markHit("exit_for_loadDataInternal_catch");
                  mView.exit();
                }
              }

              @Override
              public void onError(String msg) {
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit("exit_for_loadDataInternal_onError");
                mView.exit();
              }
            });
  }
}
