package in.co.dipankar.fmradio.entity.radio.remote;


import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.quickandorid.utils.Network;

import static in.co.dipankar.fmradio.utils.Constants.REMOTE_URL;

public class RadioRemoteFetcher {

    public interface Callback{
        void onSuccess(List<Radio> list);
        void onFail(String msg);
    }

    private Network mNetwork;
    ObjectMapper mMapper;
    public RadioRemoteFetcher(Context context){
        //TODO: Use DI to get the Context
        mNetwork = new Network(context,true);
        mMapper= new ObjectMapper();
    }

    public void fetch(@NonNull  final Callback callback){
        mNetwork.retrive(REMOTE_URL, Network.CacheControl.GET_LIVE_ONLY, new Network.Callback() {
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
            if(jsonObject.getString("status").equals("success")){
                String jsonList =  jsonObject.getString("out");
                String stringVal = jsonList;
                myList = mMapper.readValue(stringVal, new TypeReference<List<Radio>>(){});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(JSONException e) {
            e.printStackTrace();
        }
        callback.onSuccess(myList);
    }
}
