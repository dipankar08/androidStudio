package in.peerreview.fmradioindia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

  @JsonProperty("uid")
  private String id;

  @JsonProperty("rank")
  private int rank;

  @JsonProperty("count_click")
  private int count_click;

  @JsonProperty("count_error")
  private int count_error;

  @JsonProperty("count_success")
  private int count_success;

  @JsonProperty("name")
  private String name;

  @JsonProperty("img")
  private String img;

  @JsonProperty("url")
  private String url;

  @JsonProperty("group")
  private String group;

  @JsonProperty("tags")
  private String tags;

  @JsonProperty("help")
  private String help;

  @JsonProperty("state")
  private String state;
@JsonProperty("category")
private String categories;

  public String getId() {
    return id;
  }

  public int getRank() {
    return rank;
  }

  public int getCount_click() {
    return count_click;
  }

  public int getCount_error() {
    return count_error;
  }

  public int getCount_success() {
    return count_success;
  }

  public String getName() {
    return name;
  }

  public String getImg() {
    return img;
  }

  public String getUrl() {
    return url;
  }

  public String getGroup() {
    return group;
  }

  public String getTags() {
    return tags;
  }

  public String getHelp() {
    return help;
  }

  public String getState() {
    return state;
  }

    public String getCategories() {
        return categories;
    }

    public Channel(
      String id,
      int rank,
      int count_click,
      int count_error,
      int count_success,
      String name,
      String img,
      String url,
      String group,
      String tags,
      String help,
      String state,
      String categories) {
    this.id = id;
    this.rank = rank;
    this.count_click = count_click;
    this.count_error = count_error;
    this.count_success = count_success;
    this.name = name;
    this.img = img;

    this.url = url;
    this.group = group;
    this.tags = tags;
    this.help = help;
    this.state = state;
    this.categories = categories;
  }

  public Channel() {}
  public Channel(String name, String img){
      this.name = name;
      this.img = img;
    }

  public String getSubTitle() {
    String msg = "<span color='black'>" + getCount_click() + "</span> plays  ." + getRankMessage();
    return msg;
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
}
