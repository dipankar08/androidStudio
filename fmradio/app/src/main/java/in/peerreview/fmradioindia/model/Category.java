package in.peerreview.fmradioindia.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
  private String name;
  private List<Channel> mList;

  public Category(String name) {
    this.name = name;
    mList = new ArrayList<>();
  }

  public void addItem(Channel c) {
    mList.add(c);
  }

  public String getName() {
    return name;
  }

  public List<Channel> getList() {
    return mList;
  }

  public Category addList(List<Channel> list) {
    mList = list;
    return this;
  }
}
