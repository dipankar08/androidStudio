package in.co.dipankar.fmradio.data.radio.remote;

import static in.co.dipankar.fmradio.utils.Constants.REMOTE_DB_ENDPOINT;

import android.content.Context;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; // in play 2.3
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.quickandorid.utils.Network;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class RadioRemoteFetcher {

  public interface Callback {
    void onSuccess(List<Radio> list);

    void onFail(String msg);
  }

  private Network mNetwork;
  public static String RANK_UP_URL = REMOTE_DB_ENDPOINT + "?_cmd=rankup&_payload=rank&id=";
  public static String RANK_DOWN_URL = REMOTE_DB_ENDPOINT + "?_cmd=rankdown&_payload=rank&id=";
  public static String FETCH_URL = REMOTE_DB_ENDPOINT + "?_limit=1000&status=active";

  ObjectMapper mMapper;

  public RadioRemoteFetcher(Context context) {
    // TODO: Use DI to get the Context
    mNetwork = new Network(context, true);
    mMapper = new ObjectMapper();
  }

  public void fetch(@NonNull final Callback callback) {
    mNetwork.retrive(
        FETCH_URL,
        Network.CacheControl.GET_LIVE_ONLY,
        new Network.Callback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {
            parseObject(jsonObject, callback);
          }

          @Override
          public void onError(String msg) {
            callback.onFail(msg);
          }
        });
  }

  private void parseObject(JSONObject jsonObject, Callback callback) {
    List<Radio> myList = null;
    try {
      if (jsonObject.getString("status").equals("success")) {
        String jsonList = jsonObject.getString("out");
        String stringVal = jsonList;
        myList = mMapper.readValue(stringVal, new TypeReference<List<Radio>>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    for (Radio r : myList) {
      r.process();
    }
    callback.onSuccess(myList);
  }

  public void updateRank(final String id, final boolean up) {
    if (up) {
      mNetwork.retrive(RANK_UP_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
    } else {
      mNetwork.retrive(RANK_DOWN_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
    }
  }

  public void updateStatOnDBNodes(final String id, final String type) {
    mNetwork.send(
        REMOTE_DB_ENDPOINT,
        new HashMap<String, String>() {
          {
            put("_cmd", "increment");
            put("id", id);
            put("_payload", type);
          }
        },
        new Network.Callback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {}

          @Override
          public void onError(String msg) {}
        });
  }
}
