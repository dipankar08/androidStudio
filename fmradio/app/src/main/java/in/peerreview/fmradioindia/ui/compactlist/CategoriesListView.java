package in.peerreview.fmradioindia.ui.compactlist;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Category;
import java.util.List;
import javax.annotation.Nullable;

public class CategoriesListView extends ConstraintLayout {
  private RecyclerView mCategoriesRV;
  private CategoriesAdapter mCategoriesAdapter;
  @Nullable private Callback mCallback;

  public interface Callback {
    public void onItemClick(String id);

    public void onMoreClick(int i);
  }

  public CategoriesListView(Context context) {
    super(context);
    init();
  }

  public CategoriesListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CategoriesListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.view_composite_list, this, true);
    mCategoriesRV = findViewById(R.id.rv);
    mCategoriesAdapter =
        new CategoriesAdapter(
            getContext(),
            null,
            new CategoriesAdapter.Callback() {
              @Override
              public void onClickAllButton(int i) {
                if (mCallback != null) {
                  mCallback.onMoreClick(i);
                }
              }

              @Override
              public void onClickItem(String id) {
                if (mCallback != null) {
                  mCallback.onItemClick(id);
                }
              }
            });
    mCategoriesRV.setLayoutManager(new LinearLayoutManager(getContext()));
    mCategoriesRV.setItemAnimator(new DefaultItemAnimator());
    mCategoriesRV.setAdapter(mCategoriesAdapter);
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  public void setup(List<Category> list) {
    mCategoriesAdapter.setItems(list);
  }
}
