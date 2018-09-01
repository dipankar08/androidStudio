package in.co.dipankar.fmradio.data.radio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Radio implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum STATE {
        LIVE_RADIO,
        LIVE_TV,
        ONLINE,
        OFFLINE,
        MOSTLY_WORKING,
        REMOVED,
        SLOW,
        LIMITED_TIME_BROADCAST
    }

    public class Builder {
        private String id;
        private String name;
        private String mediaUrl;
        private String imageUrl;
        private String categories;
        private String tags;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setImageUrl(String name) {
            this.imageUrl = name;
            return this;
        }

        public Builder setMediaUrl(String name) {
            this.mediaUrl = name;
            return this;
        }

        public Builder setCategories(String name) {
            this.categories = name;
            return this;
        }

        public Builder setTags(String name) {
            this.tags = name;
            return this;
        }

        public Radio build() {
            return new Radio(this);
        }

    }

    private Radio(Builder builder) {
        name = builder.name;
        mediaUrl = builder.mediaUrl;
        imageUrl = builder.imageUrl;
        categories = builder.categories;
        tags = builder.tags;
        id = builder.id;
    }

    @JsonProperty("uid")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String mediaUrl;
    @JsonProperty("img")
    private String imageUrl;
    @JsonProperty("category")
    private String categories;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("status")
    private String status;
    @JsonProperty("rank")
    private int rank;
    @JsonProperty("play_count")
    private int count;
    @JsonProperty("help")
    private String help;
    private STATE state;


    public String getName() {
        return name;
    }

    public STATE getState() {
        return state;
    }

    public String getCategories() {
        return categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTags() {
        return tags;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getStatus() {
        return status;
    }

    public STATE getRank() {
        if (rank < 2) {
            return STATE.OFFLINE;
        } else if (rank < 6) {
            return STATE.MOSTLY_WORKING;
        } else {
            return STATE.OFFLINE;
        }
    }

    public int getCount() {
        return count;
    }

    public String getHelp() {
        return help;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    // used my mapper
    public Radio(String name, String mediaUrl, String imageUrl, String categories, String tags, String status, int rank, int count, String help) {
        this.name = name;
        this.mediaUrl = mediaUrl;
        this.imageUrl = imageUrl;
        this.categories = categories;
        this.tags = tags;
        this.status = status;
        this.rank = rank;
        this.count = count;
        this.status = status;


    }

    public void process() {
        buildState();
    }

    private void buildState() {
        if (tags != null) {
            tags = tags.toLowerCase();
            if (tags.contains("live_tv")) {
                state = STATE.LIVE_TV;
            } else if (tags.contains("slow")) {
                state = STATE.SLOW;
            } else if (tags.contains("limited_time_boardcast")) {
                state = STATE.LIMITED_TIME_BROADCAST;
            } else {
                state = STATE.ONLINE;
            }
        } else {
            state = STATE.LIVE_RADIO;
        }
    }

    public boolean isVideo() {
        if (state == STATE.LIVE_TV) {
            return true;
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public String getSubTitle() {
        return categories;
    }

    public Radio() {
    }

}
