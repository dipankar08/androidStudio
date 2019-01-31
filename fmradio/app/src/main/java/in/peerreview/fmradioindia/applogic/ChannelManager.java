package in.peerreview.fmradioindia.applogic;

import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.MyApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelManager {;
  private HashMap<String, Channel> mIdToChannelMap;
  private DataFetcher mDataFetcher;
  private List<Channel> mSearchChannelList;
  private List<Channel> mFullChannelList;
  private List<Channel> mSuggestionChannelList;
  private List<Category> mCategories;
  static ChannelManager sChannelManager;

  private ChannelManager() {
    mCallbacks = new ArrayList<>();
    mSearchChannelList = new ArrayList<>();
    mSuggestionChannelList = new ArrayList<>();
    mFullChannelList = new ArrayList<>();
    mCategories = new ArrayList<>();
    mIdToChannelMap = new HashMap<>();
    mDataFetcher = new DataFetcher(MyApplication.Get().getApplicationContext());
    mDataFetcher = new DataFetcher(MyApplication.Get().getApplicationContext());
  }

  public void fetch() {
    mDataFetcher.fetchData(
        new DataFetcher.Callback() {
          @Override
          public void onSuccess(final List<Channel> channels) {
            if (channels != null) {
              processChannel(channels);
            } else{
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

  public List<Channel> getRecentSearch() {
    return mSearchChannelList;
  }


    public interface Callback {
    void onLoadError(String err);

    void onLoadSuccess(List<Channel> channels);
  }

  List<Callback> mCallbacks;

  public void addCallback(Callback callback) {
    mCallbacks.add(callback);
  }

  public void removeCallback(Callback callback) {
    mCallbacks.remove(callback);
  }

  public static ChannelManager Get() {
    if (sChannelManager == null) {
      sChannelManager = new ChannelManager();
    }
    return sChannelManager;
  }

  private void processChannel(List<Channel> channels) {
    mFullChannelList = channels;
    mSearchChannelList = mFullChannelList;
    buildSuggestionList();

    // build the cat list
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
      mIdToChannelMap.put(channel.getId(), channel);

      if (channel.getCategories() != null) {
        for (Category c : mCategories) {
          if (c.getName().toLowerCase().equals(channel.getCategories().toLowerCase())) {
            c.addItem(channel);
            break;
          }
        }
      }
    }
    for (Callback callback : mCallbacks) {
      callback.onLoadSuccess(channels);
    }
  }

  private void buildSuggestionList() {
    for (int i = 0; i < Math.min(mFullChannelList.size(), 5); i++) {
      mSuggestionChannelList.add(mFullChannelList.get(i));
    }
    mSuggestionChannelList.add(new Channel("Find More..", ""));
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

  public List<Channel> getRecent() {
    return mSuggestionChannelList;
  }

  public List<Channel> getAll() {
    return mFullChannelList;
  }

  public Channel getChannelForId(String id) {
    return mIdToChannelMap.get(id);
  }

    public List<Category> getCatMap() {
        return mCategories;
    }
}
