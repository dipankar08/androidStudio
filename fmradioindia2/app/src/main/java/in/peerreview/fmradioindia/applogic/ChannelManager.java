package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import in.co.dipankar.quickandorid.utils.Network;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONObject;

@Singleton
public class ChannelManager {

  public interface Callback {
    void onSuccess(List<Channel> channel);

    void onFailed(String err);
  }

  private static String URL = "http://simplestore.dipankar.co.in/api/livetv?_limit=100";

  private @Inject Context mContext;
  private Network mNetwork;
  private List<Channel> mChannelList;
  private List<Callback> mCallbackList;
  boolean mIsProcessing;

  @Inject
  public ChannelManager() {
    mCallbackList = new ArrayList<>();
    mNetwork = new Network(mContext, true);
  }

  public void fetchFirstData() {
    if (mIsProcessing == true) {
      return;
    }
    mIsProcessing = true;
    mNetwork.retrive(
        URL,
        Network.CacheControl.GET_LIVE_ELSE_CACHE,
        new Network.Callback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {
            mChannelList = parse(jsonObject);
            for (Callback callback : mCallbackList) {
              callback.onSuccess(mChannelList);
            }
            mIsProcessing = false;
          }

          @Override
          public void onError(String msg) {
            for (Callback callback : mCallbackList) {
              callback.onFailed(msg);
            }
            mIsProcessing = false;
          }
        });
  }

  public void addCallback(Callback callback) {
    mCallbackList.add(callback);
  }

  public void removeCallback(Callback callback) {
    mCallbackList.remove(callback);
  }

  private List<Channel> parse(JSONObject jsonObject) {
    return null;
  }
}
