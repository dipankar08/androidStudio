package in.peerreview.fmradioindia.ui.home;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.compactlist.CategoriesListView;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import javax.annotation.Nullable;

public class HomeScreen extends ConstraintLayout implements BaseView<HomeState> {
  private CategoriesListView mCategoriesList;
  private HomePresenter mPresenter;
  @Nullable private Callback mCallback;
  private TextView mSerachBox;
    public interface Callback {
    void onSearchClick();
  }

  public HomeScreen(Context context) {
    super(context);
    init();
  }

  public HomeScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HomeScreen(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.screen_home, this, true);
    mCategoriesList = findViewById(R.id.categories_list);
      mSerachBox = findViewById(R.id.search_box);
    mCategoriesList.addCallback(
        new CategoriesListView.Callback() {
          @Override
          public void onItemClick(String id) {
            mPresenter.onItemClick(id);
          }

          @Override
          public void onMoreClick(int i) {
            if (mCallback != null) {
              mCallback.onSearchClick();
            }
          }
        });
      mSerachBox.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              if(mCallback != null){
                  mCallback.onSearchClick();
              }
          }
      });
    mPresenter = new HomePresenter();
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  @Override
  public void render(final HomeState state) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (state.getCategoriesMap() != null) {
                  mCategoriesList.setup(state.getCategoriesMap());
                }
              }
            });
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mPresenter.attachView(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    mPresenter.detachView();
    super.onDetachedFromWindow();
  }
}
