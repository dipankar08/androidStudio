package in.co.dipankar.fmradio.data.radio;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;

import static in.co.dipankar.fmradio.utils.Constants.REMOTE_DB_ENDPOINT;

public class RemoteDataSource extends BaseDataSource {
    Network mNetWork;
    public RemoteDataSource(Context context){
        mNetWork = new Network(null, true);
    }

    private List<Radio> buildListFromJson(JSONObject jsonObject) {
        ObjectMapper objectMapper = new ObjectMapper();

        String carJson =
                "[{ \"brand\" : \"Mercedes\", \"doors\" : 5 }]";
        List<Radio> radioList = null;
        try {
            radioList = objectMapper.readValue(carJson, new TypeReference<List<Radio>>(){});
            DLog.d(radioList.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return radioList;
    }

    @Override
    public void fetch(Callback callback) {
        mNetWork.retrive(REMOTE_DB_ENDPOINT, Network.CacheControl.GET_LIVE_ONLY, new Network.Callback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                List<Radio> list = buildListFromJson(jsonObject);
                callback.onSuccess(list);
            }

            @Override
            public void onError(String msg) {
                callback.onFail(msg);
            }
        });
    }
}
