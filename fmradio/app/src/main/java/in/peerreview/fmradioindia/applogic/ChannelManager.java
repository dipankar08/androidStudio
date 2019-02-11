package in.peerreview.fmradioindia.applogic;

import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelManager {
  private static final int MAX_LIMIT = 6;

  private HashMap<String, Channel> mIdToChannelMap;
  private HashMap<String, Integer> mIdToIndex;
  private List<Channel> mFullChannelList;
  private List<Channel> mRecentSearchChannelList;
  private List<Channel> mRecentPlayedChannelList;
  private List<Channel> mLikedChannelList;
  private List<Category> mCategories;
  List<Callback> mCallbacks;

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
    mCategories = new ArrayList<>();
    mIdToChannelMap = new HashMap<>();
    mIdToIndex = new HashMap<>();
    mRecentSearchChannelList = mStorageManager.getRecentSearch();
    mRecentPlayedChannelList = mStorageManager.getRecentPlayed();
    mLikedChannelList = mStorageManager.getLike();
    mFullChannelList = mStorageManager.getAll();
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

  private void processChannel() {
    for (int i = 0; i < mFullChannelList.size(); i++) {
      Channel channel = mFullChannelList.get(i);
      mIdToChannelMap.put(channel.getId(), channel);
      mIdToIndex.put(channel.getId(), i);
    }
    reBuildCatList();
    for (Callback callback : mCallbacks) {
      callback.onLoadSuccess();
    }
  }

  private void reBuildCatList() {
    mCategories = new ArrayList<>();
    mCategories.add(new Category("Recent Play List").addList(mRecentPlayedChannelList));
    mCategories.add(new Category("Liked Play List").addList(mLikedChannelList));
    ArrayList<String> mTags =
        new ArrayList<String>() {
          {
            add("Mostly Played");
            add("Bengali Hits");
            add("Hindi Hits");
            add("Bengali TV");
            add("All India Radio");
            // add("Hindi TV");
            add("Bangladesh Radio");
          }
        };
    for (String tag : mTags) {
      mCategories.add(new Category(tag));
    }

    for (Channel channel : mFullChannelList) {
      if (channel.getCategories() != null) {
        for (Category c : mCategories) {
          if (c.getName().toLowerCase().equals(channel.getCategories().toLowerCase())) {
            c.addItem(channel);
            break;
          }
        }
      }
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
    return mCategories;
  }

  public void notifyUpdate() {
    reBuildCatList();
    for (Callback callback : mCallbacks) {
      callback.onDataRefreshed();
    }
  }

  public void saveList() {
    mStorageManager.saveAll(mFullChannelList);
    mStorageManager.saveRecentPlayed(mRecentPlayedChannelList);
    mStorageManager.saveRecentSearch(mRecentSearchChannelList);
    mStorageManager.saveLike(mLikedChannelList);
  }
}
