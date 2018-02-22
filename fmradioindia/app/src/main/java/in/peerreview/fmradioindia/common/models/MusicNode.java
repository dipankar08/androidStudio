package in.peerreview.fmradioindia.common.models;

import android.os.Parcel;
import android.os.Parcelable;

/** Created by dip on 2/19/18. */
public class MusicNode implements Parcelable {
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
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

  public MusicNode(String id, String title, String subtitle, String media_url, String image_url) {
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

  // Parsel..
  @Override
  public int describeContents() {
    return 0;
  }

  public MusicNode(Parcel in) {
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

  public static final Parcelable.Creator<MusicNode> CREATOR =
      new Parcelable.Creator<MusicNode>() {
        public MusicNode createFromParcel(Parcel in) {
          return new MusicNode(in);
        }

        public MusicNode[] newArray(int size) {
          return new MusicNode[size];
        }
      };
}
