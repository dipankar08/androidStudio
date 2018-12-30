package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.co.dipankar.quickandorid.utils.Network;
import in.peerreview.fmradioindia.model.Channel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class DataFetcher {
  public interface Callback {
    void onSuccess(List<Channel> mChannelList);

    void onError(String msg);
  }

  private static String URL = "http://simplestore.dipankar.co.in/api/nodel_bengalifm?_limit=1000";
  private List<Channel> mChannelList;
  private Network mNetwork;
  private List<Callback> mCallback;

  public DataFetcher(Context context) {
    mChannelList = new ArrayList<>();
    mNetwork = new Network(context, true);
  }

  public void fetchData(final Callback callback) {
    mNetwork.retrive(
        URL,
        Network.CacheControl.GET_CACHE_ELSE_LIVE,
        new Network.Callback() {

          @Override
          public void onSuccess(JSONObject jsonObject) {
            callback.onSuccess(parse(jsonObject));
          }

          @Override
          public void onError(String s) {
            callback.onError("Not able to retrived data");
          }
        });
  }

  private List<Channel> parse(JSONObject jsonObject) {
    ObjectMapper mMapper = new ObjectMapper();
    try {
      if (jsonObject.getString("status").equals("success")) {
        String jsonList = jsonObject.getString("out");
        String stringVal = jsonList;
        mChannelList = mMapper.readValue(stringVal, new TypeReference<List<Channel>>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Collections.sort(
        mChannelList,
        new Comparator<Channel>() {
          @Override
          public int compare(Channel o1, Channel o2) {
            return o2.getCount_click() - o1.getCount_click();
          }
        });
    return mChannelList;
  }
}
