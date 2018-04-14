package in.peerreview.ping.activities.call.addon.map;


/** Created by dip on 4/14/18. */
public interface IDirectionFinder {
  public interface Callback {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(String start, String end, String dis, String dur);

    void onError(String s);
  }
}
