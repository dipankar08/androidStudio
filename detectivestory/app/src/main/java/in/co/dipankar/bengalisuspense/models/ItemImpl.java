package in.co.dipankar.bengalisuspense.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.co.dipankar.quickandorid.views.QuickListView;

public  class ItemImpl implements QuickListView.Item{
    String title, subtitle, image, id, media, type;

    public ItemImpl(String a){
        title = a;
    }
    public ItemImpl(String title, String subtitle, String image, String media) {
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.media = media;
    }

    @Override

    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return subtitle;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    @Override
    public String getId() {
        return id;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getType() {
        return type;
    }

    @JsonProperty("image_url")
    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("media_url")
    public void setMedia(String media) {
        this.media = media;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }


    public String getUrl(){
        return media;
    }
    public ItemImpl(){

    }
}