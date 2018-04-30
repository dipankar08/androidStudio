package in.peerreview.fmradioindia.common.models;

import android.content.Context;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NodeManager {

  private List<Node> nodeList;
  private LinkedList<Node> feblist;
  private LinkedList<Node> rectlist;
  private List<Callback> mCallbackList;

  public interface Callback {
    void onItemAddToFeb();

    void onItemRemFromFeb();
  }

  public NodeManager(Context context) {
    nodeList = new ArrayList<>();
    mCallbackList = new ArrayList<>();
    Paper.init(context);
  }

  public void updateList(List<Node> nodes) {
    if (nodes != null) {
      nodeList = nodes;
    }
  }

  public void addCallback(Callback callback) {
    mCallbackList.add(callback);
  }

  public List<Node> getList() {
    return nodeList;
  }

  public List<Node> getFavorite() {
    if (feblist == null) {
      feblist = Paper.book().read("FevList", new LinkedList());
    }
    return feblist;
  }

  public void handleFavorite(Node temp, boolean add) {
    if (feblist == null) {
      feblist = Paper.book().read("FevList", new LinkedList());
    }
    Node found = null;
    for (Node a : feblist) {
      if (!a.isSongType()) continue;
      if (a.getTitle().equals(temp.getTitle())) {
        found = a;
        break;
      }
    }
    if (found != null) {
      feblist.remove(found);
      for (Callback callback : mCallbackList) {
        callback.onItemRemFromFeb();
      }
    } else {
      feblist.add(0, temp);
      if (feblist.size() == 11) {
        feblist.remove(feblist.size() - 1);
      }
      for (Callback callback : mCallbackList) {
        callback.onItemAddToFeb();
      }
    }
    Paper.book().write("FevList", feblist);
  }

  public boolean isFev(Node n) {
    if (feblist == null) {
      feblist = Paper.book().read("FevList", new LinkedList());
    }
    for (Node a : feblist) {
      if (a.type != Node.Type.RADIO) continue;
      if (a.getTitle().equals(n.getTitle())) {
        return true;
      }
    }
    return false;
  }

  public List<Node> getRecent() {
    if (rectlist == null) {
      rectlist = Paper.book().read("RecentList", new LinkedList());
    }
    return rectlist;
  }

  public void addToRecent(Node n) {
    if (n.type != Node.Type.RADIO) return;
    if (rectlist == null) {
      rectlist = Paper.book().read("RecentList", new LinkedList());
    }
    for (Node a : rectlist) {
      if (n.type != Node.Type.RADIO) continue;
      if (a.getTitle().equals(n.getTitle())) {
        return;
      }
    }
    rectlist.add(0, n);
    if (rectlist.size() == 11) {
      rectlist.remove(rectlist.size() - 1);
    }
    Paper.book().write("RecentList", rectlist);
  }
}
