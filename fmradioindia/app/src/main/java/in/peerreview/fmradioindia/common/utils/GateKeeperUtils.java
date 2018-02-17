package in.peerreview.fmradioindia.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import in.peerreview.fmradioindia.activities.radio.model.RadioNode;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dip on 2/17/18.
 */

public class GateKeeperUtils {

    enum GK{
      MUSIC_PALYER_APP_FEATURE,

    };

    //public API
    // This func should be called for download and initilization.
    public void init(Context context, String remoteUrl){
        mContext = context;
        downlaodAndSaveRemoteConfirg(remoteUrl);
    }

    public boolean isFeatureEnabled(String gk_name, boolean defl){
        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(gk_name, null);
        if (restoredText != null) {
            if(restoredText.toLowerCase().equals("true") || restoredText.toLowerCase().equals("1") ){
                return  true;
            } else {
                int num = getStringInt(restoredText);
                return rollDie(num);
            }
        } else{
            return defl;
        }
    }

    public boolean isDebugOnlyFeature(){
        return AndroidUtils.isDebug();
    }

    public String getRemoteSetting(String gk_name, String defl){
        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(gk_name, null);
        if (restoredText != null) {
           return restoredText;
        } else{
            return defl;
        }
    }

    public boolean rollDie(int percentGiven)
    {
        Random rand = new Random();
        int roll = rand.nextInt(100);
        if(roll < percentGiven) {
            return true;
        }
        else {
            return false;
        }
    }

    //private
    private int getStringInt(String s)
    {
        try
        {
            int num = Integer.parseInt(s);
            if(num >=0 && num <=100){
                return num;
            } else{
                return 0;
            }
        } catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    private void downlaodAndSaveRemoteConfirg(String remoteUrl){
        Network.getInstance().retrive(
                remoteUrl,
                Network.CacheControl.GET_LIVE_ONLY,
                new Network.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                        JSONArray Jarray;
                        Map<String,String> map = new HashMap<>();
                        try {
                            Jarray = jsonObject.getJSONArray("out");
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object = Jarray.getJSONObject(i);
                                if (object.has("gk_name") && object.has("gk_value")) {
                                    map.put(object.getString("gk_name"), object.getString("gk_value"));
                                }
                            }
                            if(map.size() > 0){
                                configMap = map;
                                SharedPreferences.Editor editor = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                for (Map.Entry<String, String> entry : configMap.entrySet()) {
                                    editor.putString(entry.getKey().toString(),entry.getValue().toString());
                                }
                                editor.apply();
                                Log.d(TAG,"GateKeep configuration loaded successfully!");
                            }
                        } catch (JSONException e) {
                            Log.d(TAG,"ERROR104"+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

    }

    //private
    Map<String,String> configMap;
    private Context mContext;
    private static String TAG = "DIPANKAR";
    private static final String MY_PREFS_NAME = "GK_FILE";

    //singleton
    private static class GateKeeperUtilsLoader {
        private static final GateKeeperUtils INSTANCE = new GateKeeperUtils();
    }
    private GateKeeperUtils() {
        if (GateKeeperUtilsLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }
    public static GateKeeperUtils getInstance() {
        return GateKeeperUtilsLoader.INSTANCE;
    }

}
