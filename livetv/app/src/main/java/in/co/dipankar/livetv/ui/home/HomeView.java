package in.co.dipankar.livetv.ui.home;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseView;
import in.co.dipankar.livetv.base.Screen;
import in.co.dipankar.livetv.data.ChannelManager;

public class HomeView extends BaseView {

  private RecyclerView mRecyclerView;
  private TvListAdapter mTVAdapter;
  ChannelManager mChannelManager;

  public HomeView(Context context) {
    super(context);
    init(context);
  }

  public HomeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_home, this);
    mChannelManager = ChannelManager.Get();

    mRecyclerView = (RecyclerView) findViewById(R.id.rv);
    RecyclerView.LayoutManager mLayoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mTVAdapter = new TvListAdapter(getContext());
    mRecyclerView.setAdapter(mTVAdapter);
    mRecyclerView.addOnItemTouchListener(
        new RecyclerTouchListener(
            getContext(),
            mRecyclerView,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                mChannelManager.setCurrent(position);
                getNavigation().navigate(Screen.PLAYER, null);
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mTVAdapter.setItems(mChannelManager.getList());
  }
}
