package in.co.dipankar.livetv.data;

import android.content.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.co.dipankar.quickandorid.utils.Network;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelManager {

  private static ChannelManager sChannelManager;
  private static String URL = "http://simplestore.dipankar.co.in/api/livetv?_limit=100";

  private Context mContext;
  private List<Channel> mChannelList;
  private int mChannel;
  private Network mNetwork;

  private ChannelManager() {
      mActionCallbackList = new ArrayList<>();
      mChannelList = new ArrayList<>();
  }

  public interface ActionCallback{
      void onAction(String action);
  }

  private List<ActionCallback> mActionCallbackList;
  public static synchronized ChannelManager Get() {
    if (sChannelManager == null) {
      sChannelManager = new ChannelManager();
    }
    return sChannelManager;
  }

  public void fetchData(final Callback callback) {
    mNetwork.retrive(
        URL,
        Network.CacheControl.GET_LIVE_ELSE_CACHE,
        new Network.Callback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {
            callback.onSuccess(parse(jsonObject));
          }

          @Override
          public void onError(String msg) {
            callback.onFail(msg);
          }
        });
  }

  public void init(Context context) {
    mContext = context;
    mChannelList = new ArrayList<>();
    mNetwork = new Network(mContext, false);
  }

  public void addActionCallback(ActionCallback cb){
      mActionCallbackList.add(cb);
  }

    public void setAction(String data) {
      switch (data){
          case "next":
              next();
              break;
          case "prev":
              prev();
              break;
      }

      for(ActionCallback ac: mActionCallbackList){
          ac.onAction(data);
      }
    }

    public interface Callback {
    void onFail(String msg);
    void onSuccess(List<Channel> radio);
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
    return mChannelList;
  }

  public List<Channel> getList() {
    return mChannelList;
  }

  public void setCurrent(int id) {
    mChannel = id;
  }

  public Channel getCurrent() {
    return mChannelList.get(mChannel);
  }

  public void next(){
      mChannel ++;
      if(mChannel == mChannelList.size()){
          mChannel =0;
      }
      for(ActionCallback c: mActionCallbackList){
          c.onAction("track_change");
      }
  }
    public void prev(){
        mChannel --;
        if(mChannel < 0 ){
            mChannel =mChannelList.size() -1;
        }
        for(ActionCallback c: mActionCallbackList){
            c.onAction("track_change");
        }
    }
}
