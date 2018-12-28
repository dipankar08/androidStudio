package in.co.dipankar.livetv.ui;

import android.os.Handler;
import in.co.dipankar.livetv.data.Channel;
import in.co.dipankar.livetv.data.DataFetcher;
import in.co.dipankar.quickandorid.arch.BasePresenter;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter {
  private DataFetcher mDataFetcher;
  private List<Channel> mChannelList;
  private List<Channel> mFullChannelList;
  private List<String> mCategories;
  private int mCurIndex = -1;
  private Handler mHandler = new Handler();
  private Runnable mRunnable;
  private boolean mIsShowingControl = false;
  private final Runnable hideRun =
      new Runnable() {
        @Override
        public void run() {
          render(new MainState.Builder().setIsListOpen(false).build());
        }
      };

  public MainPresenter() {
    super("MainPresenter");
    render(new MainState.Builder().setIsShowLoading(true).build());
    mChannelList = new ArrayList<>();
    mFullChannelList = new ArrayList<>();
    mCategories = new ArrayList<>();
    mDataFetcher = new DataFetcher(MyApplication.Get().getApplicationContext());
    mDataFetcher.fetchData(
        new DataFetcher.Callback() {
          @Override
          public void onSuccess(final List<Channel> channels) {
            if (mChannelList != null) {
              processChannel(channels);
            }
            mHandler.postDelayed(
                new Runnable() {
                  @Override
                  public void run() {
                    render(
                        new MainState.Builder()
                            .setChannel(channels)
                            .setCat(mCategories)
                            .setIsShowLoading(false)
                            .build());
                  }
                },
                10 * 1000);
          }

          @Override
          public void onError(String msg) {
            render(new MainState.Builder().setErrorMsg(msg).setIsShowLoading(false).build());
          }
        });

    mRunnable =
        new Runnable() {
          @Override
          public void run() {
            hideControls();
          }
        };
  }

  private void processChannel(List<Channel> channels) {
    mFullChannelList = channels;
    mChannelList = mFullChannelList;
    mCategories.add("All"); // index 0
    for (Channel c : channels) {
      if (mCategories.contains(c.getCategories())) {
        continue;
      }
      mCategories.add(c.getCategories());
    }
    mCurIndex = 0;
  }

  public void OnRecyclerViewTouch() {
    mHandler.removeCallbacks(mRunnable);
    mHandler.postDelayed(mRunnable, 5000);
  }

  public void onVideoPlayerError() {
    if (mChannelList.size() < 0) {
      return;
    }
    render(
        new MainState.Builder()
            .setErrorMsg("Not able to play:" + mChannelList.get(mCurIndex).getName())
            .build());
  }

  public void onVideoPlayerSuccess() {
    render(new MainState.Builder().setErrorMsg(null).build());
  }

  public void onTouchViewView() {
    if (mIsShowingControl) {
      hideControls();
      mHandler.removeCallbacks(mRunnable);
    } else {
      showControls();
      mHandler.removeCallbacks(mRunnable);
      mHandler.postDelayed(mRunnable, 5000);
    }
  }

  private void showControls() {
    render(new MainState.Builder().setIsShowControl(true).build());
    mIsShowingControl = true;
  }

  private void hideControls() {
    render(new MainState.Builder().setIsShowControl(false).build());
    mIsShowingControl = false;
  }

  public void onNextClicked() {
    if (mChannelList.size() < 0) {
      return;
    }
    mCurIndex = (mCurIndex + 1) % mChannelList.size();
    render(new MainState.Builder().setCurChannel(mChannelList.get(mCurIndex)).build());
  }

  public void onPrevClicked() {
    if (mChannelList.size() < 0) {
      return;
    }
    mCurIndex--;
    if (mCurIndex < 0) {
      mCurIndex = mChannelList.size() - 1;
    }
    render(new MainState.Builder().setCurChannel(mChannelList.get(mCurIndex)).build());
  }

  public void onItemClick(int position) {
    if (mChannelList.size() < 0) {
      return;
    }
    render(new MainState.Builder().setCurChannel(mChannelList.get(position)).build());
  }

  public void OnSpinnerSelect(int position) {
    List<Channel> selected = new ArrayList<>();
    if (position == 0) {
      selected = mFullChannelList;
    } else {
      for (Channel c : mFullChannelList) {
        if (c.getCategories().equals(mCategories.toArray()[position])) {
          selected.add(c);
        }
      }
    }
    mChannelList = selected;
    render(new MainState.Builder().setChannel(selected).build());
  }
}
