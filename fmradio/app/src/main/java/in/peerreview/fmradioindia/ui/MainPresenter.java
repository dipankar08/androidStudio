package in.peerreview.fmradioindia.ui;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import in.peerreview.fmradioindia.applogic.DataFetcher;
import in.peerreview.fmradioindia.applogic.MusicService;
import in.peerreview.fmradioindia.applogic.TelemetryManager;
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
    private List<Channel> mSearchChannelList;
    private List<Channel> mFullChannelList;
    private List<Channel> mSuggestionChannelList;
    private List<String> mCategories;
    private TelemetryManager mTelemetryManager;
    private HashMap<String, Channel> mIdToChannelMap;

    private int mCurIndex = -1;
    private Handler mHandler = new Handler();

    public MainPresenter() {
        super("MainPresenter");
        render(new MainState.Builder().setCurPage(MainState.Page.SPASH).build());
        mSearchChannelList = new ArrayList<>();
        mSuggestionChannelList = new ArrayList<>();
        mFullChannelList = new ArrayList<>();
        mCategories = new ArrayList<>();
        mIdToChannelMap = new HashMap<>();
        mDataFetcher = new DataFetcher(MyApplication.Get().getApplicationContext());
        mDataFetcher.fetchData(
                new DataFetcher.Callback() {
                    @Override
                    public void onSuccess(final List<Channel> channels) {
                        if (mSearchChannelList != null) {
                            processChannel(channels);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        showErrorMsg(msg);
                    }
                });
        mTelemetryManager = new TelemetryManager();
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
        mSearchChannelList = mFullChannelList;
        mCategories.add("All"); // index 0
        mCurIndex = 0;
        mSuggestionChannelList = mFullChannelList.subList(0, 5);
        mSuggestionChannelList.add(new Channel("Find More..",""));
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        render(new MainState.Builder()
                            .setChannel(channels)
                            .setSuggestionList(mSuggestionChannelList)
                            .setCurPage(MainState.Page.HOME)
                            .build());
                    }
                });

        for(Channel channel: mFullChannelList){
            mIdToChannelMap.put(channel.getId(), channel);
        }
    }

    public void OnSearchQueryChanged(String s) {
        List<Channel> mSearchResult = new ArrayList<>();
        if(s == null || s.length() == 0){
            mSearchResult = mFullChannelList;
        } else {
            for (Channel c : mFullChannelList) {
                if (c.getName().toLowerCase().contains(s.toLowerCase())) {
                    mSearchResult.add(c);
                }
            }
        }
        mSearchChannelList = mSearchResult;
        render(new MainState.Builder().setChannel(mSearchResult).build());
    }

    public void onNextClicked() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_NEXT_BUTTON);
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.NEXT);
        ensureSurviceReady();
        getContext().startService(mService);
    }

    public void onPrevClicked() {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_PREV_BUTTON);
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PREV);
        ensureSurviceReady();
        getContext().startService(mService);
    }

    public void onItemClick(int position) {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_MAIN_LIST_ITEM);
        String id = mSearchChannelList.get(position).getId();
        for(int i =0 ;i<mFullChannelList.size();i++){
            if(id.equals(mFullChannelList.get(i).getId())){
                startPlayInternal(i);
                break;
            }
        }
    }

    public void onSuggestionItemClick(int position) {
        mTelemetryManager.markHit(TelemetryManager.TELEMETRY_CLICK_SUGGESTION_LIST_ITEM);
        String id = mSuggestionChannelList.get(position).getId();
        for(int i =0 ;i<mFullChannelList.size();i++){
            if(id.equals(mFullChannelList.get(i).getId())){
                startPlayInternal(i);
                break;
            }
        }
    }

    private void startPlayInternal(int position) {
        render(new MainState.Builder().setCurChannel(mFullChannelList.get(position)).build());
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
        for (Channel channel : mFullChannelList) {
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
                                    render(new MainState.Builder().setIsShowLoading(true).setCurChannel(mIdToChannelMap.get(id)).build());
                                    showErrorMsg("Hold on! Try playing "+mIdToChannelMap.get(id).getName());
                                    mTelemetryManager.dbIncrementCount(id);
                                    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_TRY_PLAYING);
                                }

                                @Override
                                public void onSuccess(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(true).setIsShowLoading(false).build());
                                    showErrorMsg("Now Playing "+mIdToChannelMap.get(id).getName());
                                    DLog.d("Binder::onSuccess called");
                                    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_SUCCESS);
                                    mTelemetryManager.dbIncrementSuccess(id);
                                    mTelemetryManager.rankUp(id);
                                }

                                @Override
                                public void onResume(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(true).build());
                                    showErrorMsg("Now playing "+mIdToChannelMap.get(id).getName());
                                    DLog.d("Binder::onResume called");
                                }

                                @Override
                                public void onPause(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(false).build());
                                    showErrorMsg(""+mIdToChannelMap.get(id).getName()+" Paused");
                                    DLog.d("Binder::onPause called");
                                }

                                @Override
                                public void onError(String id, String msg) {
                                    render(new MainState.Builder().setIsPlaying(false).setIsShowLoading(false).build());
                                    showErrorMsg(mIdToChannelMap.get(id).getName()+ " is offline.");
                                    DLog.d("Binder::onError called");
                                    mTelemetryManager.markHit(TelemetryManager.TELEMETRY_PLAYER_ERROR);
                                    mTelemetryManager.dbIncrementError(id);
                                    mTelemetryManager.rankDown(id);
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
