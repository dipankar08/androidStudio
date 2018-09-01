package in.co.dipankar.fmradio.ui.viewpresenter.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class FullScreenPlayerView extends BaseView implements FullScreenPlayerViewPresenter.ViewContract {
    FullScreenPlayerViewPresenter mPresenter;
    private ImageView mBack;
    private ImageView mPrev;
    private ImageView mNext;
    private ImageView mPausePlay;
    private ProgressBar mProgressBar;
    private ImageView mImage;

    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mHelp;
    ImageView feb;

    private RadioManager.PlayChangeCallback changeCallback = new RadioManager.PlayChangeCallback() {
        @Override
        public void onPlayChange(Radio r, RadioManager.STATE state) {
            refershUI(r, state);
        }
    };

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

        feb = findViewById(R.id.fev);
        feb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FmRadioApplication.Get().getRadioManager().toggleLove(FmRadioApplication.Get().getRadioManager().getCurrentID(), new RadioManager.LoveCallback() {
                    @Override
                    public void isLoveMarked(boolean isMarked) {
                        feb.setImageResource(isMarked? R.drawable.ic_love_fill_white_32:R.drawable.ic_love_white_32);
                    }
                });
            }
        });

        mPrev = findViewById(R.id.previous);
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FmRadioApplication.Get().getMusicController().prev();
            }
        });

        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FmRadioApplication.Get().getMusicController().next();
            }
        });

        mPausePlay = findViewById(R.id.play_pause);
        mPausePlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FmRadioApplication.Get().getMusicController().playPause();
            }
        });

        mImage = findViewById(R.id.image);
        mTitle = findViewById(R.id.title);
        mSubTitle = findViewById(R.id.subtile);
        mHelp = findViewById(R.id.help);
        mProgressBar = findViewById(R.id.loading);
    }


    private void goback() {
        getNavigation().goBack();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FmRadioApplication.Get().getRadioManager().addPlayChangeCallback(changeCallback);
        refershUI(FmRadioApplication.Get().getRadioManager().getCurrentRadio(), FmRadioApplication.Get().getRadioManager().getState());
        if(getArgs() == null ){
            return;
        }
        String id = getArgs().getString("ID");
        if(id != null) {
            FmRadioApplication.Get().getMusicController().attach(getContext());
            FmRadioApplication.Get().getMusicController().play(id);
            setArgs(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FmRadioApplication.Get().getRadioManager().removePlayChangeCallback(changeCallback);
    }

    private void refershUI(Radio mRadio, RadioManager.STATE state){
        if(mRadio == null){
            return;
        }
        mTitle.setText(mRadio.getName());

        Glide.with(getContext())
                .load(mRadio.getImageUrl())
                .into(mImage);

        int resId = R.string.state_default;
        switch (mRadio.getState()){
            case OFFLINE:
                resId = R.string.state_offline;
                break;
            case SLOW:
                resId = R.string.state_slow;
                break;
            case REMOVED:
                resId = R.string.state_removed;
                break;
            case MOSTLY_WORKING:
                resId = R.string.state_mostlyworking;
                break;
            case LIMITED_TIME_BROADCAST:
                resId = R.string.state_limitedtime;
                break;
        }

        mHelp.setText(resId);
        mSubTitle.setText(mRadio.getCount()+" plays");

        feb.setImageResource(FmRadioApplication.Get().getRadioManager().isLove(mRadio.getId())
                ? R.drawable.ic_love_fill_white_32:R.drawable.ic_love_white_32);

        switch (state){
            case SUCCESS:
            case RESUME:
                mPausePlay.setImageResource(R.drawable.ic_pause_white_32);
                mPausePlay.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                break;
            case TRY_PLAYING:
                mPausePlay.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                break;
            case PAUSED:
            case COMPLETE:
            case NOT_PLAYING:
                mPausePlay.setImageResource(R.drawable.ic_play_white_32);
                mPausePlay.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                break;
            case ERROR:
                Toast.makeText(getContext(), "Not able to play"+mRadio.getName()+", please retry.", Toast.LENGTH_SHORT).show();
        }
    }

}
