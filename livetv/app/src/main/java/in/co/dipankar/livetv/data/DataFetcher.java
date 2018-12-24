package in.co.dipankar.livetv.data;

import android.content.Context;
import android.telecom.Call;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.quickandorid.utils.Network;

public class DataFetcher {
    public interface Callback{
        void onSuccess(List<Channel> mChannelList);
        void onError(String msg);
    }
    private static String URL = "http://simplestore.dipankar.co.in/api/livetv?_limit=100";
    private List<Channel> mChannelList;
    private Network mNetwork;
    private List<Callback> mCallback;
    public DataFetcher(Context context){
        mChannelList = new ArrayList<>();
        mNetwork = new Network(context, true);
    }
    public void fetchData(final Callback callback ){
        mNetwork.retrive(URL, Network.CacheControl.GET_LIVE_ONLY, new Network.Callback(){

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
        return mChannelList;
    }

}
