package in.co.dipankar.fmradio.data;

import android.content.Context;
import java.util.List;

import in.co.dipankar.fmradio.data.radio.BaseDataSource;
import in.co.dipankar.fmradio.data.radio.RemoteDataSource;
import in.co.dipankar.fmradio.entity.radio.Radio;

public class DataManager implements RadioListManager{

    ////////  This is a Single Ton /////////////
    private static DataManager sDataManager;
    public static DataManager Get(){
        if(sDataManager == null){
            sDataManager = new DataManager();
        }
        return sDataManager;
    }

    /*  Define all Local */
    private BaseDataSource mRemoteSource;

    /* Fetch Radio List */
    @Override
    public void fetchRadioList(RadioListManagerCallBack callBack){
        mRemoteSource.fetch(new BaseDataSource.Callback() {
            @Override
            public void onSuccess(List<Radio> list) {
                callBack.onReceived(list);
            }

            @Override
            public void onFail(String error) {
                callBack.onError(error);
            }
        });
    }

    //// Life Cycle Call backs
    public void init(Context context){
        mRemoteSource = new RemoteDataSource(context);
    }
    public void destroy(){
        // mRemoteSource takes context;
        mRemoteSource = null;
    }
}
