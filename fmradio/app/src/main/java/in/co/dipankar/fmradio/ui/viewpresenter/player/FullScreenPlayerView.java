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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.service.MusicService;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

public class FullScreenPlayerView extends BaseView implements FullScreenPlayerViewPresenter.ViewContract {
    FullScreenPlayerViewPresenter mPresenter;
    private List<Radio> mCurList;
    private int mCurIndex;
    private ImageView mBack;
    private ImageView mPrev;
    private ImageView mNext;
    private ImageView mPausePlay;
    private ImageView mImage;
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
                playPrev();
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
    }

    private void playNext() {
        mCurIndex ++;
        if(mCurIndex >= mCurList.size()){
            mCurIndex = 0;
        }
        play();
    }

    private void playPause() {
        Intent mService = new Intent(getContext(), MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PLAY_PAUSE);
        getContext().startService(mService);
    }


    private void playPrev() {
        mCurIndex --;
        if(mCurIndex < 0){
            mCurIndex = mCurList.size() -1;
        }
        play();
    }

    private void goback() {
        getNavigation().goBack();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCurList = FmRadioApplication.Get().getRadioManager().getRadioByCategories(getArgs().getString("cat"));
        mCurIndex = getArgs().getInt("index");
       play();
       bindService();
    }




    public void play() {
        Radio mRadio = mCurList.get(mCurIndex);

        mTitle.setText(mRadio.getName());
        Glide.with(getContext())
                .load(mRadio.getImageUrl())
                .into(mImage);

        Intent mService = new Intent(getContext(), MusicService.class);
        mService.putExtra("ID",mRadio.getName());
        mService.putExtra("NAME",mRadio.getName());
        mService.putExtra("URL",mRadio.getMediaUrl());
        mService.setAction(MusicForegroundService.Contracts.START);
        getContext().startService(mService);


    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.LocalBinder) iBinder).getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    private void bindService() {
        DLog("Bind Service called");
        getContext().bindService(new Intent(getContext(),
                MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbind() {
        DLog("UnBind Service called");
        if (mMusicService != null) {
            mMusicService.unbindService(mConnection);
        }
    }
}
