package in.co.dipankar.livetv.ui;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import in.co.dipankar.livetv.data.Channel;
import in.co.dipankar.livetv.data.DataFetcher;
import in.co.dipankar.quickandorid.arch.BasePresenter;


public class MainPresenter extends BasePresenter {
    private DataFetcher mDataFetcher;
    private List<Channel> mChannelList;
    private int mCurIndex = -1;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean mIsShowingControl = false;
    public MainPresenter() {
        super("MainPresenter");
        render(new MainState.Builder().setIsShowLoading(true).build());
        mChannelList = new ArrayList<>();
        mDataFetcher = new DataFetcher(MyApplication.Get().getApplicationContext());
        mDataFetcher.fetchData(new DataFetcher.Callback() {
            @Override
            public void onSuccess(final List<Channel> channels) {
                if(mChannelList != null) {
                    mChannelList = channels;
                    mCurIndex = 0;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        render(new MainState.Builder().setChannel(channels).setIsShowLoading(false).build());
                    }
                }, 10*1000);

            }

            @Override
            public void onError(String msg) {
                render(new MainState.Builder().setErrorMsg(msg).setIsShowLoading(false).build());
            }
        });

        mRunnable = new Runnable() {
            @Override
            public void run() {
                hideControls();
            }
        };

    }

    public void onVideoPlayerError() {
        if(mChannelList.size() < 0){
            return;
        }
        render(new MainState.Builder().setErrorMsg("Not able to play:"+mChannelList.get(mCurIndex).getName()).build());
    }

    public void onVideoPlayerSuccess() {
        render(new MainState.Builder().setErrorMsg(null).build());
    }

    public void onTouchViewView() {
        if(mIsShowingControl){
            hideControls();
            mHandler.removeCallbacks(mRunnable);
        } else{
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
        if(mChannelList.size() < 0){
            return;
        }
        mCurIndex = (mCurIndex+1)% mChannelList.size();
        render(new MainState.Builder().setCurChannel(mChannelList.get(mCurIndex)).build());
    }

    public void onPrevClicked() {
        if(mChannelList.size() < 0){
            return;
        }
        mCurIndex --;
        if(mCurIndex < 0){
            mCurIndex = mChannelList.size() -1;
        }
        render(new MainState.Builder().setCurChannel(mChannelList.get(mCurIndex)).build());
    }

    public void onItemClick(int position) {
        if(mChannelList.size() < 0){
            return;
        }
        render(new MainState.Builder().setCurChannel(mChannelList.get(position)).build());
    }
}
