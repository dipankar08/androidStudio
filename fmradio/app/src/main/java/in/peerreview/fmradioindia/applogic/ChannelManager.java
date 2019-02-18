package in.peerreview.fmradioindia.applogic;

import android.support.annotation.Nullable;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.model.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelManager {
  private static final int MAX_LIMIT = 6;
  private static final String SUGGESTION_LIST_TAG = "Mostly Played";

  private HashMap<String, Channel> mIdToChannelMap;
  private HashMap<String, Integer> mIdToIndex;
  private List<Channel> mFullChannelList;
  private List<Channel> mRecentSearchChannelList;
  private List<Channel> mRecentPlayedChannelList;
  private List<Channel> mLikedChannelList;
  private LinkedHashMap<String, Category> mCategoriesMap;
  private List<Category> mSelectedCatList;
  List<Callback> mCallbacks;
  private LinkedHashMap<String, Boolean> mUserPref;
  List<Channel> mSuggested;

  private DataFetcher mDataFetcher;
  private StorageManager mStorageManager;

  public interface Callback {
    void onLoadError(String err);

    void onLoadSuccess();

    void onDataRefreshed();
  }

  @Inject
  public ChannelManager(DataFetcher dataFetcher, StorageManager storageManager) {
    mDataFetcher = dataFetcher;
    mStorageManager = storageManager;
    mCallbacks = new ArrayList<>();
    mCategoriesMap = new LinkedHashMap<>();
    mSelectedCatList = new ArrayList<>();
    mIdToChannelMap = new HashMap<>();
    mIdToIndex = new HashMap<>();
    mRecentSearchChannelList = mStorageManager.getRecentSearch();
    mRecentPlayedChannelList = mStorageManager.getRecentPlayed();
    mLikedChannelList = mStorageManager.getLike();
    mFullChannelList = mStorageManager.getAll();
    mUserPref = new LinkedHashMap<>();
    mSuggested = new ArrayList<>();
  }

  public void fetch() {
    if (mFullChannelList != null && mFullChannelList.size() > 0) {
      processChannel();
    }
    mDataFetcher.fetchData(
        new DataFetcher.Callback() {
          @Override
          public void onSuccess(final List<Channel> channels) {
            if (channels != null) {
              mFullChannelList = channels;
              processChannel();
            } else {
              for (Callback callback : mCallbacks) {
                callback.onLoadError("Not able to load channel");
              }
            }
          }

          @Override
          public void onSuccessConfig(Config config) {
            processConfig(config);
          }

          @Override
          public void onError(String msg) {
            for (Callback callback : mCallbacks) {
              callback.onLoadError(msg);
            }
          }
        });
  }

  public void addCallback(Callback callback) {
    mCallbacks.add(callback);
  }

  public void removeCallback(Callback callback) {
    mCallbacks.remove(callback);
  }

  private void processConfig(Config config) {
    // Might need to process the config
    for (String s : config.getCatList()) {
      mUserPref.put(s, false);
    }

    for (String s : config.getLangList()) {
      mUserPref.put(s, false);
    }
    maybuildCatList();
  }

  private void processChannel() {
    for (int i = 0; i < mFullChannelList.size(); i++) {
      Channel channel = mFullChannelList.get(i);
      mIdToChannelMap.put(channel.getId(), channel);
      mIdToIndex.put(channel.getId(), i);
    }
    maybuildCatList();
  }

  private void maybuildCatList() {
    if (mUserPref.size() == 0 || mFullChannelList.size() == 0) {
      DLog.d("Skip building the list as UserPref or mFullChannelList not avilable");
      return;
    }

    mCategoriesMap = new LinkedHashMap<>();
    mCategoriesMap.put(
        "Recent Play List".toLowerCase(),
        new Category("Recent Play List", true).addList(mRecentPlayedChannelList));
    mCategoriesMap.put(
        "Liked Play List".toLowerCase(),
        new Category("Liked Play List", true).addList(mLikedChannelList));
    mCategoriesMap.put(
        SUGGESTION_LIST_TAG.toLowerCase(),
        new Category(SUGGESTION_LIST_TAG, true).addList(mLikedChannelList));

    for (Map.Entry<String, Boolean> item : mUserPref.entrySet()) {
      mCategoriesMap.put(item.getKey().toLowerCase().trim(), new Category(item.getKey(), true));
    }
    // Populated
    for (Channel channel : mFullChannelList) {
      // cat exist.
      String cat = channel.getCategories();
      if (cat != null) {
        if (mCategoriesMap.get(cat.toLowerCase().trim()) == null) {
          mCategoriesMap.put(cat.toLowerCase().trim(), new Category(cat, false));
        }
        mCategoriesMap.get(cat.toLowerCase().trim()).addItem(channel);
      }
      // tag
      if (channel.getTags() != null) {
        for (String c : mCategoriesMap.keySet()) {
          if (channel.getTags().toLowerCase().contains(c)) {
            mCategoriesMap.get(c).addItem(channel);
          }
        }
      }
    }
    if (mCategoriesMap.get(SUGGESTION_LIST_TAG.trim().toLowerCase()) != null) {
      mSuggested.addAll(mCategoriesMap.get(SUGGESTION_LIST_TAG.trim().toLowerCase()).getList());
    }
    for (Callback callback : mCallbacks) {
      callback.onLoadSuccess();
    }
    reBuildCatList();
  }

  // cat_list: "Recent Play List, Liked Play List, Suggested Live, Kolkata Live, Bengali News TV,
  // Hindi News Tv, English News Tv, Mumbai Live, Delhi Live, Bangalore Live, Pune Live, Hyderabad
  // Live, All India Radio, Bangladesh Radio ",
  private void reBuildCatList() {
    mSelectedCatList = new ArrayList<>();
    for (Map.Entry<String, Category> entry : mCategoriesMap.entrySet()) {
      if (entry.getValue().isShouldShow()) {
        mSelectedCatList.add(entry.getValue());
      }
    }
    for (Callback callback : mCallbacks) {
      callback.onDataRefreshed();
    }
  }

  public List<Channel> applySearch(String s) {
    List<Channel> mSearchResult = new ArrayList<>();
    if (s == null || s.length() == 0) {
      return mFullChannelList;
    } else {
      for (Channel c : mFullChannelList) {
        if (c.getName().toLowerCase().contains(s.toLowerCase())) {
          mSearchResult.add(c);
        }
      }
    }
    return mSearchResult;
  }

  public List<Channel> getAll() {
    return mFullChannelList;
  }

  public List<Channel> getRecentPlayed() {
    return mRecentPlayedChannelList;
  }

  public void markRecentPlayed(String id) {
    Channel channel = mIdToChannelMap.get(id);
    // remove older if exist.
    mRecentPlayedChannelList.remove(channel);
    if (channel != null) {
      mRecentPlayedChannelList.add(0, channel);
    }
    ;
    if (mRecentPlayedChannelList.size() > MAX_LIMIT) {
      mRecentPlayedChannelList.remove(MAX_LIMIT);
    }
    notifyUpdate();
  }

  public List<Channel> getRecentSearch() {
    return mRecentSearchChannelList;
  }

  public void markRecentSearch(String id) {
    Channel channel = mIdToChannelMap.get(id);
    // remove older if exist.
    mRecentSearchChannelList.remove(channel);
    if (channel != null) {
      mRecentSearchChannelList.add(0, channel);
    }
    ;
    if (mRecentSearchChannelList.size() > MAX_LIMIT) {
      mRecentSearchChannelList.remove(MAX_LIMIT);
    }
    notifyUpdate();
  }

  public List<Channel> getFev() {
    return mLikedChannelList;
  }

  public void toggleFev(String id) {
    Channel channel = mIdToChannelMap.get(id);
    if (channel == null) {
      return;
    }
    if (mLikedChannelList.contains(channel)) {
      mLikedChannelList.remove(channel);
    } else {
      mLikedChannelList.add(0, channel);
      if (mLikedChannelList.size() > MAX_LIMIT) {
        mLikedChannelList.remove(MAX_LIMIT);
      }
    }
    notifyUpdate();
  }

  public boolean isFev(String id) {
    Channel channel = mIdToChannelMap.get(id);
    if (channel == null) {
      return false;
    }
    return mLikedChannelList.contains(channel);
  }

  public Channel getChannelForId(String id) {
    return mIdToChannelMap.get(id);
  }

  public Integer getIndexForId(String id) {
    return mIdToIndex.get(id);
  }

  public List<Category> getCatMap() {
    return mSelectedCatList;
  }

  public LinkedHashMap<String, Boolean> getUserPref() {
    return mUserPref;
  }

  public @Nullable List<Channel> getSuggestedList() {
    return mSuggested;
  }

  public void notifyUpdate() {
    reBuildCatList();
    for (Callback callback : mCallbacks) {
      callback.onDataRefreshed();
    }
  }

  public void setFilterUserPref(Map<String, Boolean> val) {
    for (Map.Entry<String, Boolean> entry : val.entrySet()) {
      mUserPref.put(entry.getKey(), entry.getValue());
    }
    notifyUpdate();
  }

  public void saveList() {
    mStorageManager.saveAll(mFullChannelList);
    mStorageManager.saveRecentPlayed(mRecentPlayedChannelList);
    mStorageManager.saveRecentSearch(mRecentSearchChannelList);
    mStorageManager.saveLike(mLikedChannelList);
  }
}
