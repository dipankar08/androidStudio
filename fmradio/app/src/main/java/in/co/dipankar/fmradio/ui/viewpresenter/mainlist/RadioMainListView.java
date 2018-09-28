package in.co.dipankar.fmradio.ui.viewpresenter.mainlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.viewpresenter.radiolist.RadioListAdapter;
import in.co.dipankar.fmradio.ui.viewpresenter.shared.RecyclerTouchListener;
import in.co.dipankar.quickandorid.utils.DLog;

public class RadioMainListView extends BaseView {
  private RecyclerView mRecyclerView;
  private RecyclerView mRecomenedRecyclerView;
  private RadioListAdapter mSuggestedAdapter;

  private RadioMainListAdapter mRadioMainListAdapter;
  private RadioMainListViewPresenter mRadioMainListViewPresenter;
  private SwipeRefreshLayout mSwipeRefreshLayout;

  public RadioMainListView(Context context) {
    super(context);
    init(context);
  }

  RadioManager.DataChangeCallback dataChangeCallback =
      new RadioManager.DataChangeCallback() {
        @Override
        public void onDataChanged(RadioManager.DataListType type) {
          switch (type) {
            case ALL:
              mRadioMainListAdapter.updateList(
                  FmRadioApplication.Get().getRadioManager().getCategories());
              break;
            case RECENT:
            case FEV:
              mSuggestedAdapter.setItems(FmRadioApplication.Get().getRadioManager().getSuggested());
          }
        }
      };

  public RadioMainListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public RadioMainListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(getContext()).inflate(R.layout.view_main_list, this);
    mRadioMainListViewPresenter = new RadioMainListViewPresenter();
    setPresenter(mRadioMainListViewPresenter);

    mRecomenedRecyclerView = findViewById(R.id.rv_suggested);
    mRecomenedRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    mSuggestedAdapter = new RadioListAdapter(getContext(), RadioListAdapter.ItemStyle.SUGGESTED);
    mRecomenedRecyclerView.setAdapter(mSuggestedAdapter);
    mRecomenedRecyclerView.addOnItemTouchListener(
        new RecyclerTouchListener(
            getContext(),
            mRecyclerView,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                Radio radio = mSuggestedAdapter.getItem(position);
                playGeneric(radio);
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));

    mRecyclerView = findViewById(R.id.rv);
    mRadioMainListAdapter = new RadioMainListAdapter();
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.setAdapter(mRadioMainListAdapter);
    mRecyclerView.setNestedScrollingEnabled(false);
  }

  private void playGeneric(Radio radio) {
    DLog.d("Now Playing " + radio.getName());
    Bundle bundle = new Bundle();
    bundle.putString("ID", radio.getId());
    if (radio.isVideo()) {
      FmRadioApplication.Get().getMusicController().stop();
      getNavigation().navigate(Screen.VIDEO_PLAER_SCREEN, bundle);
    } else {
      getNavigation().navigate(Screen.PLAYER_SCREEN, bundle);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    FmRadioApplication.Get().getRadioManager().addDataChangeCallback(dataChangeCallback);
    super.onDetachedFromWindow();
  }

  @Override
  protected void onAttachedToWindow() {
    if (FmRadioApplication.Get().getRadioManager() != null) {
      FmRadioApplication.Get().getRadioManager().removeDataChangeCallback(dataChangeCallback);
      mRadioMainListAdapter.updateList(FmRadioApplication.Get().getRadioManager().getCategories());
      mSuggestedAdapter.setItems(FmRadioApplication.Get().getRadioManager().getSuggested());
    }
    super.onAttachedToWindow();
  }
}
