package in.co.dipankar.fmradio.entity.radio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Radio implements Serializable {
    private static final long serialVersionUID = 1L;

    public class Builder{
        private String name;
        private String mediaUrl;
        private String imageUrl;
        private String categories;
        private String tags;

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setImageUrl(String name){
            this.imageUrl = name;
            return this;
        }

        public Builder setMediaUrl(String name){
            this.mediaUrl = name;
            return this;
        }

        public Builder setCategories(String name){
            this.categories = name;
            return this;
        }

        public Builder setTags(String name){
            this.tags = name;
            return this;
        }

        public Radio build(){
            return  new Radio(this);
        }

    }

    private Radio(Builder builder) {
        name= builder.name;
        mediaUrl=builder.mediaUrl;
        imageUrl= builder.imageUrl;
        categories= builder.categories;
        tags=builder.tags;
    }

    @JsonProperty("name")
    private  String name;
    @JsonProperty("url")
    private  String mediaUrl;
    @JsonProperty("img")
    private  String imageUrl;
    @JsonProperty("category")
    private  String categories;
    @JsonIgnoreProperties
    private  String tags;
    @JsonProperty("status")
    private String status;
    @JsonProperty("rank")
    private int rank;


    public String getName(){
        return name;
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

    @Override
    public String toString() {
        return super.toString();
    }
    // used my mapper
    public Radio(String name, String mediaUrl, String imageUrl, String categories, String tags, String status, int rank) {
        this.name = name;
        this.mediaUrl = mediaUrl;
        this.imageUrl = imageUrl;
        this.categories = categories;
        this.tags = tags;
        this.status = status;
        this.rank = rank;
    }
    public Radio() {}

}
