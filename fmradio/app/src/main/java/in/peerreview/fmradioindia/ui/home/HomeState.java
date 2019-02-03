package in.peerreview.fmradioindia.ui.home;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Category;
import java.util.List;

public class HomeState extends BaseViewState {
  List<Category> mCategoriesMap = null;

  protected HomeState(Builder builder) {
    super(builder);
    mCategoriesMap = builder.mCategoriesMap;
  }

  public List<Category> getCategoriesMap() {
    return mCategoriesMap;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    List<Category> mCategoriesMap = null;

    public Builder() {}

    public Builder setCategoriesList(List<Category> mCategoriesMap) {
      this.mCategoriesMap = mCategoriesMap;
      return this;
    }

    public HomeState build() {
      return new HomeState(this);
    }
  }
}
