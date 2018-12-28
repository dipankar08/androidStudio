package in.peerreview.fmradioindia.newui;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import in.peerreview.fmradioindia.applogic.DataFetcher;
import in.peerreview.fmradioindia.applogic.MusicService;
import in.peerreview.fmradioindia.model.Channel;
import in.co.dipankar.quickandorid.arch.BasePresenter;
import in.co.dipankar.quickandorid.services.Item;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPresenter extends BasePresenter {
    private DataFetcher mDataFetcher;
    private List<Channel> mChannelList;
    private List<Channel> mFullChannelList;
    private List<String> mCategories;

    private int mCurIndex = -1;
    private Handler mHandler = new Handler();

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
                                                        .setCurPage(MainState.Page.HOME)
                                                        .build());
                                    }
                                },
                                10 * 1000);
                    }

                    @Override
                    public void onError(String msg) {
                        showErrorMsg(msg);
                    }
                });
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();
        bindService();
    }

    @Override
    protected void onViewDettached() {
        super.onViewDettached();
        unBindService();
    }

    private void processChannel(List<Channel> channels) {
        mFullChannelList = channels;
        mChannelList = mFullChannelList;
        mCategories.add("All"); // index 0
        mCurIndex = 0;
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

    public void onNextClicked() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.NEXT);
        ensureSurviceReady();
        getContext().startService(mService);
    }

    public void onPrevClicked() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PREV);
        ensureSurviceReady();
        getContext().startService(mService);
    }

    public void onItemClick(int position) {
        if (mChannelList.size() < 0) {
            return;
        }
        ensureSurviceReady();
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.putExtra("index", position);
        mService.setAction(MusicForegroundService.Contracts.PLAY_RANDOM);
        getContext().startService(mService);
    }

    private boolean isServiceReady = false;

    private void ensureSurviceReady() {
        if (isServiceReady) {
            return;
        }
        Intent mService = new Intent(getContext(), MusicService.class);
        List<Item> list = new ArrayList<Item>();
        for (Channel channel : mChannelList) {
            list.add(new Item(channel.getId(), channel.getName(), channel.getUrl()));
        }
        mService.putExtra("list", (Serializable) list);
        mService.setAction(MusicForegroundService.Contracts.LOAD);
        getContext().startService(mService);
        isServiceReady = true;
    }

    public void onPlayPauseClicked() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PLAY_OR_PAUSE);
        ensureSurviceReady();
        getContext().startService(mService);
    }

    // Music player callbacks.
    private void unBindService() {
    }

    public void bindService() {
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void showErrorMsg(String msg) {
        render(new MainState.Builder().setErrorMsg(msg).build());
        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        render(new MainState.Builder().setErrorMsg("").build());
                    }
                },
                30 * 1000);
    }

    private ServiceConnection mConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MusicService.LocalBinder myLocalBinder = (MusicService.LocalBinder) iBinder;
                    MusicForegroundService musicService = myLocalBinder.getService();
                    DLog.d("Connected to bounded service");
                    musicService.setCallback(
                            new MusicForegroundService.Callback() {
                                @Override
                                public void onTryPlaying(String id, String msg) {
                                    DLog.d("Binder::onTryPlaying called");
                                    render(new MainState.Builder().setIsShowLoading(true).build());
                                }

                                @Override
                                public void onSuccess(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(true).setIsShowLoading(false).build());
                                    DLog.d("Binder::onSuccess called");
                                }

                                @Override
                                public void onResume(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(true).build());
                                    showErrorMsg(msg);
                                    DLog.d("Binder::onResume called");
                                }

                                @Override
                                public void onPause(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(false).build());
                                    showErrorMsg(msg);
                                    DLog.d("Binder::onPause called");
                                }

                                @Override
                                public void onError(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(false).setIsShowLoading(false).build());
                                    showErrorMsg(msg);
                                    DLog.d("Binder::onError called");
                                }

                                @Override
                                public void onSeekBarPossionUpdate(String id, int total, int cur) {
                                    DLog.d("Binder::onSeekBarPossionUpdate called");
                                }

                                @Override
                                public void onMusicInfo(String id, HashMap<String, Object> info) {
                                    DLog.d("Binder::onMusicInfo called");
                                }

                                @Override
                                public void onComplete(String id, String msg) {
                                    DLog.d("Binder::onComplete called");
                                }
                            });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    DLog.d("Disconnected to bounded service");
                }
            };
}
