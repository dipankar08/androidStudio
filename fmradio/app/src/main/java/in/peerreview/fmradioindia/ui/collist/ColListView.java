package in.peerreview.fmradioindia.ui.collist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.common.RecyclerTouchListener;
import java.util.List;

public class ColListView extends LinearLayout {
  RecyclerView mRV;
  Callback mCallback;
  ColListAdapter mColListAdapter;
  List<Channel> mItemList;

  public interface Callback {
    void onClick(String id);
  }

  public ColListView(Context context) {
    super(context);
    init();
  }

  public ColListView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ColListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public ColListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.view_cal_list, this, true);

    mRV = findViewById(R.id.rv);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    mRV.setLayoutManager(layoutManager);
    mRV.setItemAnimator(new DefaultItemAnimator());
    mColListAdapter = new ColListAdapter(getContext());
    mRV.setAdapter(mColListAdapter);
    mRV.addOnItemTouchListener(
        new RecyclerTouchListener(
            getContext(),
            mRV,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                if (mCallback != null && mItemList != null && position < mItemList.size()) {
                  mCallback.onClick(mItemList.get(position).getId());
                }
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));
  }

  public void setData(List<Channel> list, Callback callback) {
    mColListAdapter.setItems(list);
    mCallback = callback;
  }
}
