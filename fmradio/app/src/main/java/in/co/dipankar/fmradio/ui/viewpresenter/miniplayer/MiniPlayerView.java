package in.co.dipankar.fmradio.ui.viewpresenter.miniplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.entity.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.viewpresenter.player.FullScreenPlayerView;

public class MiniPlayerView extends BaseView {
    ImageView mPlayPause;
    TextView mText;
    public MiniPlayerView(Context context) {
        super(context);
        init();
    }

    public MiniPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiniPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_mini_player, this);
        mPlayPause = findViewById(R.id.play_pause);
        mPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FmRadioApplication.Get().getMusicController().playPause();
            }
        });
        mText = findViewById(R.id.text);
        mText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavigation().navigate(Screen.PLAYER_SCREEN, null);
            }
        });
    }

    private void refreshUI(Radio r, RadioManager.STATE state) {
        if(r == null) return;
        if(state == RadioManager.STATE.SUCCESS || state == RadioManager.STATE.RESUME ){
            mText.setText("Playing "+r.getName());
            mPlayPause.setImageResource(R.drawable.ic_pause_gray_32);
        } else if(state == RadioManager.STATE.PAUSED){
            mText.setText("Paused "+r.getName());
            mPlayPause.setImageResource(R.drawable.ic_play_gray_32);
        }
        else if( state == RadioManager.STATE.COMPLETE|| state == RadioManager.STATE.ERROR || state == RadioManager.STATE.NOT_PLAYING){
            mText.setText("Tap to play");
            mPlayPause.setImageResource(R.drawable.ic_stop_gray_32);
        }
        else if(state == RadioManager.STATE.TRY_PLAYING){
            mText.setText("Trying to Play "+r.getName());
            mPlayPause.setImageResource(R.drawable.ic_pause_gray_32);
        }
        else {

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FmRadioApplication.Get().getRadioManager().addPlayChangeCallback(changeCallback);
        Radio r = FmRadioApplication.Get().getRadioManager().getCurrentRadio();
        RadioManager.STATE state = FmRadioApplication.Get().getRadioManager().getState();
        refreshUI(r, state);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FmRadioApplication.Get().getRadioManager().removePlayChangeCallback(changeCallback);
    }

    private RadioManager.PlayChangeCallback changeCallback =  new RadioManager.PlayChangeCallback() {
        @Override
        public void onPlayChange(Radio r, RadioManager.STATE state) {
            refreshUI(r, state);
        }
    };
}
