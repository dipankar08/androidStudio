package in.co.dipankar.livetv.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
  @JsonProperty("uid")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("url")
  private String url;

  @JsonProperty("image")
  private String img;

  @JsonProperty("categories")
  private String categories;

  public Channel() {}

  public Channel(String id, String name, String url, String img, String categories) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.img = img;
    this.categories = categories;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getImg() {
    return img;
  }

  public String getId() {
    return id;
  }

    public String getCategories() {
        return categories;
    }
}
