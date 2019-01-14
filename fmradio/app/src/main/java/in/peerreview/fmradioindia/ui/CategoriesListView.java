package in.peerreview.fmradioindia.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

public class CategoriesListView extends ConstraintLayout {
  private TextView mTitle;
  private TextView mSeeAll;
  private RecyclerView mRev;
  private CategoriesItemAdapter mSuggestionAdapter;

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

  private void init() {}
}
