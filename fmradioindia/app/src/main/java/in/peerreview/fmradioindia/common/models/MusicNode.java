package in.peerreview.fmradioindia.common.models;

/**
 * Created by dip on 2/19/18.
 */

public class MusicNode {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
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
}
