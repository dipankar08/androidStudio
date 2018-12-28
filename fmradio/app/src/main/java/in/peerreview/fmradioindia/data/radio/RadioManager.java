package in.peerreview.fmradioindia.data.radio;

public class RadioManager {
  /*
  public List<Radio> getSuggested() {
    List<Radio> radioList = new ArrayList<>();
    radioList.addAll(getLoveList());
    radioList.addAll(getRecentList());
    if (radioList.isEmpty()) {
      return getRadioByCategories("radio mirchi live");
    }
    return radioList;
  }

  public interface RefreshCallback {
    void onDone(boolean done);
  }

  public interface LoveCallback {
    void isLoveMarked(boolean isMarked);
  }

  public enum DataListType {
    ALL,
    RECENT,
    FEV
  }

  public interface DataChangeCallback {
    void onDataChanged(DataListType type);
  }

  public interface PlayChangeCallback {
    void onPlayChange(Radio r, STATE newDtate);
  }

  public interface RadioManagerCallback {
    void onSuccess(List<Radio> radio);

    void onFail(String msg);
  }

  public void refreshData(RefreshCallback callback) {
    mRefreshCallback = callback;
    fetchData(null);
  }

  public enum STATE {
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

  private Context mContext;
  private @Nullable List<Radio> mAllRadioList;
  List<Radio> mActiveRadioList;
  private @Nullable HashMap<String, Radio> mIdToRadioMap;
  private @Nullable LinkedHashMap<String, List<Radio>> mCategoriesMap;
  private RadioRemoteFetcher mRemoteFetcher;
  private String mCurrentID;
  private STATE mState;
  private @Nullable Set<Radio> mLoveList;
  Set<Radio> mRecentList;
  private List<PlayChangeCallback> mPlayChangeCallbacks;

  private boolean fetchInProgress = false;

  public RadioManager(Context context) {
    mContext = context;
    mRemoteFetcher = new RadioRemoteFetcher(mContext);
    mIdToRadioMap = new HashMap<>();
    mDataChangeCallback = new ArrayList<>();
    mLoveList = new HashSet<>();
    mRecentList = new LinkedHashSet<>();
    mPlayChangeCallbacks = new ArrayList<>();
    mActiveRadioList = new ArrayList<>();
    mAllRadioList = new ArrayList<>();
    FmRadioApplication.Get()
        .getConfigurationManager()
        .addConfigChangeListener(
            new ConfigurationManager.ConfigChangeListener() {
              @Override
              public void onChange(ConfigurationManager.Config config, Object newvalue) {
                if (config == ConfigurationManager.Config.ENABLE_TV) {
                  processList();
                }
              }
            });
  }

  public void fetchData(final RadioManagerCallback mRadioManagerCallback) {
    if (fetchInProgress) {
      mRadioManagerCallback.onFail("We are trying to fetch one");
    }

    fetchInProgress = true;
    mRemoteFetcher.fetch(
        new RadioRemoteFetcher.Callback() {
          @Override
          public void onSuccess(List<Radio> list) {
            setList(list);
            runOnUI(
                new Runnable() {
                  @Override
                  public void run() {
                    if (mRadioManagerCallback != null) {
                      mRadioManagerCallback.onSuccess(list);
                    }

                    if (mRefreshCallback != null) {
                      mRefreshCallback.onDone(true);
                    }

                    for (DataChangeCallback c : mDataChangeCallback) {
                      c.onDataChanged(DataListType.ALL);
                    }
                  }
                });
            fetchInProgress = false;
          }

          @Override
          public void onFail(String msg) {
            runOnUI(
                new Runnable() {
                  @Override
                  public void run() {
                    if (mRadioManagerCallback != null) {
                      mRadioManagerCallback.onFail(msg);
                    }
                    if (mRefreshCallback != null) {
                      mRefreshCallback.onDone(false);
                    }
                  }
                });
            fetchInProgress = false;
          }
        });
  }

  public void setList(List<Radio> newList) {
    mAllRadioList = newList;
    processList();
  }

  public boolean isLove(String id) {
    return mLoveList.contains(id);
  }

  // Love APIS
  public void toggleLove(String id, LoveCallback loveCallback) {
    Radio r = mIdToRadioMap.get(id);
    if (r == null) {
      return;
    }
    if (mLoveList.contains(r)) {
      mLoveList.remove(r);
      loveCallback.isLoveMarked(false);
    } else {
      loveCallback.isLoveMarked(true);
      mLoveList.add(r);
    }
    notifyDataChanges(DataListType.FEV);
  }

  public List<Radio> getLoveList() {
    return new ArrayList<>(mLoveList);
  }

  // Recent APIS
  public List<Radio> getRecentList() {
    return new ArrayList<>(mRecentList);
  }

  public void updateRecent(String id) {
    Radio r = mIdToRadioMap.get(id);
    if (r != null) {
      mRecentList.add(r);
      notifyDataChanges(DataListType.RECENT);
    }
  }

  private void notifyDataChanges(DataListType type) {
    for (DataChangeCallback changeCallback : mDataChangeCallback) {
      changeCallback.onDataChanged(type);
    }
  }

  public Radio getCurrentRadio() {
    return mIdToRadioMap.get(getCurrentID());
  }

  public List<String> getCategories() {
    return new ArrayList<>(mCategoriesMap.keySet());
  }

  public List<Radio> getRadioByCategories(String type) {
    return mCategoriesMap.get(type);
  }

  public Map<String, List<Radio>> searchRadio(String str) {
    Map<String, List<Radio>> map = new LinkedHashMap<String, List<Radio>>();
    if (str == null || str.isEmpty()) {
      return map;
    }

    for (Map.Entry<String, List<Radio>> item : mCategoriesMap.entrySet()) {
      String key = item.getKey();
      List<Radio> val = new ArrayList<>();
      for (Radio r : item.getValue()) {
        if (r.getName().toLowerCase().contains(str.toLowerCase())) {
          val.add(r);
        }
      }
      if (!val.isEmpty()) {
        map.put(key, val);
      }
    }
    return map;
  }

  public List<Radio> getAllRadioForId(String id) {
    Radio r = mIdToRadioMap.get(id);
    if (r == null) {
      return null;
    }
    return mCategoriesMap.get(r.getCategories().toLowerCase());
  }

  public void setCurrentRadio(String id, STATE s) {
    mCurrentID = id;
    mState = s;
    processTelemetry(id, s);
    for (PlayChangeCallback cb : mPlayChangeCallbacks) {
      cb.onPlayChange(getRadioForId(id), s);
    }

    if (mState == STATE.TRY_PLAYING) {
      updateRecent(id);
    }
  }

  public STATE getState() {
    return mState;
  }

  public String getCurrentID() {
    return mCurrentID;
  }

  public List<Radio> getAllTvForId(String id) {
    // At present just return all TV.
    List<Radio> radios = new ArrayList<>();
    for (Radio r : mActiveRadioList) {
      if (r.isVideo()) {
        radios.add(r);
      }
    }
    return radios;
  }

  public void addPlayChangeCallback(PlayChangeCallback playChangeCallback) {
    mPlayChangeCallbacks.add(playChangeCallback);
  }

  public void removePlayChangeCallback(PlayChangeCallback changeCallback) {
    mPlayChangeCallbacks.remove(changeCallback);
  }

  public void addDataChangeCallback(DataChangeCallback dataChangeCallback) {
    mDataChangeCallback.add(dataChangeCallback);
  }

  public void removeDataChangeCallback(DataChangeCallback dataChangeCallback) {
    mDataChangeCallback.remove(dataChangeCallback);
  }

  private void processTelemetry(String id, STATE s) {
    switch (s) {
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

  private void processList() {
    if (mAllRadioList == null) return;

    // filter
    mActiveRadioList = new ArrayList<>();
    for (Radio radio : mAllRadioList) {
      Object obj =
          FmRadioApplication.Get()
              .getConfigurationManager()
              .getConfig(ConfigurationManager.Config.ENABLE_TV);
      if (radio.isVideo() && (obj == null || !obj.equals(true))) {
        continue;
      }
      mActiveRadioList.add(radio);
    }
    // sort
    Collections.sort(
        mActiveRadioList,
        new Comparator<Radio>() {
          @Override
          public int compare(Radio o1, Radio o2) {
            return o2.getCount() - o1.getCount();
          }
        });

    mCategoriesMap = new LinkedHashMap<>();
    for (Radio radio : mActiveRadioList) {
      mIdToRadioMap.put(radio.getId(), radio);
      List<String> listCat = Arrays.asList(radio.getCategories().split(","));
      for (String c : listCat) {
        if (!mCategoriesMap.containsKey(c.toLowerCase())) {
          mCategoriesMap.put(c.toLowerCase(), new ArrayList<>());
        }
        mCategoriesMap.get(c.toLowerCase()).add(radio);
      }
    }

    // indicaate data chnages.
    notifyDataChanges(DataListType.ALL);
  }

  private void runOnUI(Runnable r) {
    new Handler(Looper.getMainLooper()).post(r);
  }
  */
}
