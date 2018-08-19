package in.co.dipankar.fmradio.ui.viewpresenter.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.service.MusicService;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.quickandorid.services.Item;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

import static android.content.Context.BIND_AUTO_CREATE;

public class FullScreenPlayerView extends BaseView implements FullScreenPlayerViewPresenter.ViewContract {
    FullScreenPlayerViewPresenter mPresenter;
    private List<Radio> mCurList;
    private int mCurIndex;
    private ImageView mBack;
    private ImageView mPrev;
    private ImageView mNext;
    private ImageView mPausePlay;
    private ProgressBar mProgressBar;
    private ImageView mImage;
    private String mID;
    private TextView mTitle;
    MusicService mMusicService = null;
    public FullScreenPlayerView(Context context) {
        super(context);
        init(context);
    }

    public FullScreenPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FullScreenPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_full_player, this);
        mPresenter = new FullScreenPlayerViewPresenter();
        setPresenter(mPresenter);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });

        mPrev = findViewById(R.id.previous);
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });

        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        mPausePlay = findViewById(R.id.play_pause);
        mPausePlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause();
            }
        });

        mImage = findViewById(R.id.image);
        mTitle = findViewById(R.id.title);
        mProgressBar = findViewById(R.id.loading);
    }

    private void playPause() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PLAY_PAUSE);
        getContext().startService(mService);
    }

    private void playNext() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.NEXT);
        getContext().startService(mService);
    }

    private void prev() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PREV);
        getContext().startService(mService);
    }

    private ServiceConnection mConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MusicForegroundService.LocalBinder myLocalBinder = (MusicForegroundService.LocalBinder) iBinder;
                    MusicForegroundService musicService = myLocalBinder.getService();
                    DLog.d("Connected to bounded service");
                    musicService.setCallback(new MusicForegroundService.Callback() {
                        @Override
                        public void onTryPlaying(String id, String msg) {
                            DLog.d("Binder::onTryPlaying called");
                            showLoading();
                            mID = id;
                            refershUI();
                        }

                        @Override
                        public void onSuccess(String id, String ms) {
                            DLog.d("Binder::onSuccess called");
                            showPause();
                        }

                        @Override
                        public void onResume(String id, String ms) {
                            DLog.d("Binder::onResume called");
                            showPause();
                        }

                        @Override
                        public void onPause(String id, String msg) {
                            DLog.d("Binder::onPause called");
                            showPlay();
                        }

                        @Override
                        public void onError(String id, String msg) {
                            Toast.makeText(getContext(), "Not able to play, Please retry.", Toast.LENGTH_SHORT);
                            DLog.d("Binder::onError called");
                            showPlay();
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
                            showPlay();
                        }
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    DLog.d("Disconnected to bounded service");
                }
            };

    private void goback() {
        getNavigation().gotoHome();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String id = getArgs().getString("ID");
        if(id!= null){
            mCurList = FmRadioApplication.Get().getRadioManager().getAllRadioForId(id);
            for(int i =0;i<mCurList.size();i++){
                if(mCurList.get(i).getId().equals(id)){
                    mCurIndex = i;
                    break;
                }
            }
        } else {
            mCurList = FmRadioApplication.Get().getRadioManager().getRadioByCategories(getArgs().getString("cat"));
            mCurIndex = getArgs().getInt("index");
        }
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, mConnection, BIND_AUTO_CREATE);
        play();
    }

    private void showLoading(){
        mPausePlay.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
    }

    private void showPlay(){
        mPausePlay.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
    }
    private void showPause(){
        mPausePlay.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
    }



    private void play() {
        if(mCurList == null){
            DLog.d("mCurList is empty");
            return;
        }

        Radio mRadio = mCurList.get(mCurIndex);
        mID = mRadio.getId();

        Intent mService = new Intent(getContext(), MusicService.class);
        List<Item> item = new ArrayList<>();
        for (Radio r: mCurList){
            item.add(new Item(r.getName(), r.getName(), r.getMediaUrl()));
        }
        mService.putExtra("ID",mCurIndex);
        mService.putExtra("LIST", (Serializable) item);
        mService.setAction(MusicForegroundService.Contracts.START);
        getContext().startService(mService);
    }

    private void refershUI(){
        if(mID == null){
            return;
        }
        Radio mRadio = FmRadioApplication.Get().getRadioManager().getRadioForId(mID);
        mTitle.setText(mRadio.getName());
        Glide.with(getContext())
                .load(mRadio.getImageUrl())
                .into(mImage);

    }

}
