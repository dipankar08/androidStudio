package in.co.dipankar.fmradio.ui.viewpresenter.search;

public class SearchItem {
    String id;
    String title;
    String subtitle;
    String image;
    Type type;

    public SearchItem(String id, String title, String subtitle, String image, Type type) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public Type getType() {
        return type;
    }

    public enum Type{
        RADIO,
        ADD,
        TITLE,
    }
}
