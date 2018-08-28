package in.co.dipankar.fmradio.entity.radio;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.co.dipankar.fmradio.entity.radio.remote.RadioRemoteFetcher;
import in.co.dipankar.fmradio.ui.viewpresenter.search.SearchItem;

public class RadioManager {


    public void toggleLove(String id, LoveCallback loveCallback ) {
        if(mLoveList.contains(id)){
            mLoveList.remove(id);
            loveCallback.isLoveMarked(false);
        } else{
            loveCallback.isLoveMarked(true);
            mLoveList.add(id);
        }
    }

    public boolean isLove(String id){
        return mLoveList.contains(id);
    }

    public Radio getCurrentRadio() {
        return mIdToRadioMap.get(getCurrentID());
    }

    public interface RefreshCallback{
        void onDone(boolean done);
    }

    public interface LoveCallback{
        void isLoveMarked(boolean isMarked);
    }

    public interface DataChangeCallback{
        void onDataChanged(boolean done);
    }

    public interface PlayChangeCallback{
        void onPlayChange(Radio r, STATE newDtate);
    }

    public void refreshData(RefreshCallback callback) {
        mRefreshCallback = callback;
        fetchData(null);
    }

    public enum STATE{
        NOT_PLAYING,
        PAUSED,
        TRY_PLAYING,
        RESUME,
        COMPLETE,
        SUCCESS,
        ERROR
    }

    private RefreshCallback mRefreshCallback;
    private List<DataChangeCallback> mDataChangeCallback;
    public Radio getRadioForId(String mID) {
        return mIdToRadioMap.get(mID);
    }

    public interface RadioManagerCallback{
        void onSuccess(List<Radio> radio);
        void onFail(String msg);
    }

    private Context mContext;
    private @Nullable  List<Radio> mRadioList;
    private @Nullable HashMap<String, Radio> mIdToRadioMap;
    private @Nullable LinkedHashMap<String, List<Radio>> mCategoriesMap;
    private RadioRemoteFetcher mRemoteFetcher;
    private String mCurrentID;
    private STATE mState;
    private @Nullable Set<String> mLoveList;
    private List<PlayChangeCallback> mPlayChangeCallbacks;

    public RadioManager(Context context){
        mContext = context;
        mRemoteFetcher = new RadioRemoteFetcher(mContext);
        mIdToRadioMap = new HashMap<>();
        mDataChangeCallback = new ArrayList<>();
        mLoveList = new HashSet<>();
        mPlayChangeCallbacks = new ArrayList<>();
    }

    public void addDataChangeCallback(DataChangeCallback dataChangeCallback){
        mDataChangeCallback.add(dataChangeCallback);
    }
    public void removeDataChangeCallback(DataChangeCallback dataChangeCallback){
        mDataChangeCallback.remove(dataChangeCallback);
    }


    public void fetchData(final RadioManagerCallback mRadioManagerCallback){
        if(mRadioList != null){
            if(mRadioManagerCallback!= null) {
                mRadioManagerCallback.onSuccess(mRadioList);
            }
        }

        mRemoteFetcher.fetch(new RadioRemoteFetcher.Callback() {
            @Override
            public void onSuccess(List<Radio> list) {
                setList(list);
                if(mRadioManagerCallback != null) {
                    mRadioManagerCallback.onSuccess(list);
                }

                if(mRefreshCallback != null){
                    mRefreshCallback.onDone(true);
                }

                for(DataChangeCallback c: mDataChangeCallback){
                    c.onDataChanged(true);
                }
            }

            @Override
            public void onFail(String msg) {
                if(mRadioManagerCallback !=null){
                    mRadioManagerCallback.onFail(msg);
                }
                if(mRefreshCallback != null){
                    mRefreshCallback.onDone(false);
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
        // sort
        Collections.sort(mRadioList, new Comparator<Radio>() {
            @Override
            public int compare(Radio o1, Radio o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        mCategoriesMap = new LinkedHashMap<>();
        for(Radio radio:mRadioList){
            mIdToRadioMap.put(radio.getId(), radio);
            List<String> listCat = Arrays.asList(radio.getCategories().split(","));
            for(String c: listCat){
                if(!mCategoriesMap.containsKey(c.toLowerCase())){
                    mCategoriesMap.put(c.toLowerCase(), new ArrayList<>());
                }
                mCategoriesMap.get(c.toLowerCase()).add(radio);
            }
        }
    }

    public List<Radio> getRadioList() {
        return mRadioList;
    }

    public Map<String, List<Radio>> searchRadio(String str) {
        Map<String, List<Radio>> map = new LinkedHashMap<String, List<Radio>>();
        if(str == null || str.isEmpty()){
            return map;
        }

        for(Map.Entry<String, List<Radio>> item : mCategoriesMap.entrySet()) {
            String key = item.getKey();
            List<Radio> val = new ArrayList<>();
            for( Radio r : item.getValue()){
                if(r.getName().toLowerCase().contains(str.toLowerCase())){
                    val.add(r);
                }
            }
            if(!val.isEmpty()){
                map.put(key, val);
            }
        }
        return map;
    }

    public List<Radio> getAllRadioForId(String id) {
        Radio r = mIdToRadioMap.get(id);
        if(r == null){
            return null;
        }
        return mCategoriesMap.get(r.getCategories().toLowerCase());

    }

    public void setCurrentRadio(String id, STATE s){
        mCurrentID = id;
        mState = s;
        processTelemetry(id, s);
        for(PlayChangeCallback cb:mPlayChangeCallbacks){
            cb.onPlayChange(getRadioForId(id),s);
        }
    }

    private void processTelemetry(String id, STATE s) {

        switch (s){
            case TRY_PLAYING:
                mRemoteFetcher.updateStatOnDBNodes(id, "play_count");
                break;
            case SUCCESS:
                mRemoteFetcher.updateStatOnDBNodes(id, "play_success");
                mRemoteFetcher.updateRank(id, true);
                break;
            case ERROR:
                mRemoteFetcher.updateStatOnDBNodes(id, "play_error");
                mRemoteFetcher.updateRank(id, false);
                break;
        }
    }

    public STATE getState(){
        return  mState;
    }

    public String getCurrentID(){
        return mCurrentID;
    }


    public List<Radio> getAllTvForId(String id) {
        // At present just return all TV.
        List<Radio> radios = new ArrayList<>();
        for(Radio r: mRadioList){
            if(r.isVideo()){
                radios.add(r);
            }
        }
        return radios;
    }

    public void addPlayChangeCallback(PlayChangeCallback playChangeCallback){
        mPlayChangeCallbacks.add(playChangeCallback);
    }
    public void removePlayChangeCallback(PlayChangeCallback changeCallback){
        mPlayChangeCallbacks.remove(changeCallback);
    }
}
