package in.peerreview.fmradioindia.ui;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;

public class MainState extends BaseViewState {
  public enum Page {
    NONE,
    SPASH,
    HOME,
    SEARCH;
  }

  private List<Channel> mChannel;
  private List<Channel> suggestionList;
  private Page currentPage;
  private String mError;
  private String mDidYouKnowText;
  private Boolean isListOpen;
  private Channel mCurChannel;
  private Boolean mIsShowControl;
  private Boolean mIsShowLoading;
  private Boolean isPlaying;
  private List<String> mCat;
  List<Category> mCategoriesMap = null;
  private int mPage = -1;

  protected MainState(Builder builder) {
    super(builder);
    mChannel = builder.mChannel;
    mError = builder.mError;
    isListOpen = builder.isListOpen;
    mCurChannel = builder.mCurChannel;
    mIsShowControl = builder.mIsShowControl;
    mIsShowLoading = builder.mIsShowLoading;
    mCat = builder.mCat;
    currentPage = builder.currentPage;
    isPlaying = builder.isPlaying;
    suggestionList = builder.suggestionList;
    mDidYouKnowText = builder.mDidYouKnowText;
    mCategoriesMap = builder.mCategoriesMap;
    mPage = builder.mPage;
  }

  public String getErrorMsg() {
    return mError;
  }

  public Channel getCurChannel() {
    return mCurChannel;
  }

  public Boolean getIsListOpen() {
    return isListOpen;
  }

  public Page getCurrentPage() {
    return currentPage;
  }

  public Boolean getIsShowControl() {
    return mIsShowControl;
  }

  public Boolean getIsShowLoading() {
    return mIsShowLoading;
  }

  public Boolean getIsPlaying() {
    return isPlaying;
  }

  public String getDidYouKnowText() {
    return mDidYouKnowText;
  }

  public List<Channel> getChannel() {
    return mChannel;
  }

  public List<Channel> getSuggestionList() {
    return suggestionList;
  }

  public int getPage() {
    return mPage;
  }

  public List<String> getCat() {
    return mCat;
  }

  public List<Category> getCategoriesMap() {
    return mCategoriesMap;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    private Page currentPage = Page.NONE;
    private int mPage = -1;
    private List<Channel> mChannel = null;
    private Channel mCurChannel;
    private String mError = null;
    private Boolean isListOpen = null;
    private Boolean mIsShowControl = null;
    private Boolean mIsShowLoading = null;
    private List<String> mCat = null;
    private Boolean isPlaying = null;
    private List<Channel> suggestionList = null;
    private String mDidYouKnowText = null;
    List<Category> mCategoriesMap = null;

    public Builder() {}

    public Builder setChannel(List<Channel> channel) {
      mChannel = channel;
      return this;
    }

    public Builder setErrorMsg(String error) {
      mError = error;
      return this;
    }

    public Builder setIsListOpen(boolean isListOpen) {
      this.isListOpen = isListOpen;
      return this;
    }

    public Builder setCurChannel(Channel curChannel) {
      this.mCurChannel = curChannel;
      return this;
    }

    public Builder setIsShowControl(boolean mIsShowControl) {
      this.mIsShowControl = mIsShowControl;
      return this;
    }

    public Builder setIsShowLoading(boolean mIsShowLoading) {
      this.mIsShowLoading = mIsShowLoading;
      return this;
    }

    public Builder setCat(List<String> mCat) {
      this.mCat = mCat;
      return this;
    }

    public Builder setpage(int page) {
      this.mPage = page;
      return this;
    }

    public Builder setSuggestionList(List<Channel> sug) {
      this.suggestionList = sug;
      return this;
    }

    public Builder setCurPage(Page page) {
      this.currentPage = page;
      return this;
    }

    public Builder setIsPlaying(boolean isPlaying) {
      this.isPlaying = isPlaying;
      return this;
    }

    public Builder setDidYouKnowText(String bundle) {
      this.mDidYouKnowText = bundle;
      return this;
    }

    public Builder setCategoriesList(List<Category> mCategoriesMap) {
      this.mCategoriesMap = mCategoriesMap;
      return this;
    }

    public MainState build() {
      return new MainState(this);
    }
  }
}
