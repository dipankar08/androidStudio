package in.peerreview.fmradioindia.activities.radio.model;

/**
 * Created by dip on 2/14/18.
 */

public class RadioNode {
    public enum TYPE {
        RADIO,
        OFFLINE,
        MUSIC,
        ADD,
    };
    public RadioNode(String uid, String name, String img, String url, String tags, int error, int success, int click, TYPE type) {
        this.uid = uid;
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.mediaurl = url;
        this.count_error = error;
        this.count_success = success;
        this.count_click = click;
        this.type = type;
    }

    public RadioNode(TYPE type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getTags() {
        return tags;
    }

    public TYPE getType() {
        return type;
    }

    public String getUrl() {
        return mediaurl;
    }

    public int getCount() {
        return count_click;
    }

    public int getSuccess() {
        return count_success;
    }

    public int getError() {
        return count_error;
    }

    public int getRank() {
        return count_click;
    }

    String uid, name, img, tags, mediaurl;

    int count_error, count_success, count_click;
    TYPE type;

    public boolean isSongType() {
        return type == TYPE.RADIO ||  type == TYPE.MUSIC;
    }
}
