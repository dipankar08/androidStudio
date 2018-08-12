package in.co.dipankar.fmradio.entity.radio;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.co.dipankar.fmradio.entity.radio.remote.RadioRemoteFetcher;

public class RadioManager {


    public interface RadioManagerCallback{
        void onSuccess(List<Radio> radio);
        void onFail(String msg);
    }



    private Context mContext;
    private @Nullable  List<Radio> mRadioList;
    private LinkedHashMap<String, List<Radio>> mCategoriesMap;
    private RadioRemoteFetcher mRemoteFetcher;


    public RadioManager(Context context){
        mContext = context;
        mRemoteFetcher = new RadioRemoteFetcher(mContext);
    }


    public void fetchData(final RadioManagerCallback mRadioManagerCallback){
        if(mRadioList != null){
            mRadioManagerCallback.onSuccess(mRadioList);
        }

        mRemoteFetcher.fetch(new RadioRemoteFetcher.Callback() {
            @Override
            public void onSuccess(List<Radio> list) {
                setList(list);
                mRadioManagerCallback.onSuccess(list);
            }

            @Override
            public void onFail(String msg) {
                if(mRadioManagerCallback !=null){
                    mRadioManagerCallback.onFail(msg);
                }
            }
        });
    }
    public void setList(List<Radio> newList){
        mRadioList = newList;
        buildCategories();
    }

    public List<String> getCategories(){
        return new ArrayList<>(mCategoriesMap.keySet());
    }

    public List<Radio> getRadioByCategories(String type){
        return mCategoriesMap.get(type);
    }

    private void buildCategories(){
        if(mRadioList == null) return;
        mCategoriesMap = new LinkedHashMap<>();
        for(Radio radio:mRadioList){
            List<String> listCat = Arrays.asList(radio.getCategories().split(","));
            for(String c: listCat){
                if(!mCategoriesMap.containsKey(c)){
                    mCategoriesMap.put(c, new ArrayList<>());
                }
                mCategoriesMap.get(c).add(radio);
            }
        }
    }

    public List<Radio> getRadioList() {
        return mRadioList;
    }
}
