package in.peerreview.fmradioindia.common.models;

import android.os.Parcel;
import android.os.Parcelable;
import in.co.dipankar.quickandorid.views.QuickListView;

public class Node implements Parcelable, QuickListView.Item {

  public boolean isSongType() {
    return this.type == Type.RADIO;
  }

  public String getTags() {
    return tags;
  }

  public int getSuccess() {
    return count_success;
  }

  public int getError() {
    return count_error;
  }

  public enum Type {
    RADIO,
    MP3,
    YOUTUBE
  }

  public Node(
      String uid,
      String name,
      String img_url,
      String media_url,
      String tags,
      int count_error,
      int count_success,
      int count_click,
      int rank,
      Type type) {
    this.id = uid;
    this.title = name;
    this.image_url = img_url;
    this.media_url = media_url;
    this.tags = tags;
    this.count_error = count_error;
    this.count_success = count_success;
    this.count_click = count_click;
    this.rank = rank;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String getSubTitle() {
    String msg = "<span color='black'>" + getCount() + "</span> plays  ." + getRankMessage();
    return msg;
  }

  public String getImageUrl() {
    return image_url;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public String getMedia_url() {
    return media_url;
  }

  public void setMedia_url(String media_url) {
    this.media_url = media_url;
  }

  public String getImage_url() {
    return image_url;
  }

  public void setImage_url(String image_url) {
    this.image_url = image_url;
  }

  public int getCount() {
    return this.count_click;
  }

  public String getRankMessage() {
    if (this.rank == 0) {
      return " <font color='red'>Be the first player</font>";
    } else if (this.rank <= 2) {
      return " <font color='red'>Not working</font>";
    } else if (this.rank <= 6) {
      return " <font color='#470A51'>Working</font>";
    } else {
      return " <font color='#1a512e'>Always working</font>";
    }
  }

  public Node(String id, String title, String subtitle, String media_url, String image_url) {
    this.id = id;
    this.title = title;
    this.subtitle = subtitle;
    this.media_url = media_url;
    this.image_url = image_url;
  }

  String id;
  String title;
  String subtitle;
  String media_url;
  String image_url;
  String tags;
  int count_error;
  int count_success;
  int count_click;
  int rank;
  Type type;

  // Parsel..
  @Override
  public int describeContents() {
    return 0;
  }

  public Node(Parcel in) {
    id = in.readString();
    title = in.readString();
    subtitle = in.readString();
    media_url = in.readString();
    image_url = in.readString();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(title);
    dest.writeString(subtitle);
    dest.writeString(media_url);
    dest.writeString(image_url);
  }

  public static final Parcelable.Creator<Node> CREATOR =
      new Parcelable.Creator<Node>() {
        public Node createFromParcel(Parcel in) {
          return new Node(in);
        }

        public Node[] newArray(int size) {
          return new Node[size];
        }
      };
}
