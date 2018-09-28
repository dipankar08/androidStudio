package in.co.dipankar.fmradio.ui.viewpresenter.videoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.viewpresenter.radiolist.RadioListAdapter;
import in.co.dipankar.fmradio.ui.viewpresenter.shared.RecyclerTouchListener;
import in.co.dipankar.quickandorid.utils.DLog;
import java.util.List;
import org.apache.commons.text.WordUtils;

public class VideoPlayerView extends BaseView {

  private VideoView mVideoView;
  private ImageView mPlayPause;
  private ImageView mNext;
  private ImageView mPrev;
  private TextView mTitle;

  private List<Radio> mCurList;
  private int mCurIndex;
  private RecyclerView mRecyclerView;
  private RadioListAdapter mTVAdapter;
  private ViewGroup mMenu;
  private ViewGroup mButtonHolder;

  Runnable mRunnable;
  Handler mHandler = new Handler();

  public VideoPlayerView(Context context) {
    super(context);
    init();
  }

  public VideoPlayerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    String id = getArgs().getString("ID");
    if (id != null) {
      mCurList = FmRadioApplication.Get().getRadioManager().getAllTvForId(id);
      for (int i = 0; i < mCurList.size(); i++) {
        if (mCurList.get(i).getId().equals(id)) {
          mCurIndex = i;
          break;
        }
      }
    } else {
      DLog.d("ERROR: You must pass some ID to play the song.");
      return;
    }
    mTVAdapter.setItems(mCurList);
    play();
  }

  public void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_full_video_player, this);

    mButtonHolder = findViewById(R.id.controls);

    initVideoView();
    initRV();
    initControls();
    initMenu();
  }

  private String getCurId() {
    return mCurList.get(mCurIndex).getId();
  }

  private void initVideoView() {
    mVideoView = (VideoView) findViewById(R.id.video_view);

    mVideoView.setOnErrorListener(
        new OnErrorListener() {
          @Override
          public boolean onError(Exception e) {
            FmRadioApplication.Get()
                .getRadioManager()
                .setCurrentRadio(getCurId(), RadioManager.STATE.ERROR);
            return false;
          }
        });
    mVideoView.setOnPreparedListener(
        new OnPreparedListener() {
          @Override
          public void onPrepared() {
            mVideoView.start();
            FmRadioApplication.Get()
                .getRadioManager()
                .setCurrentRadio(getCurId(), RadioManager.STATE.SUCCESS);
          }
        });

    mVideoView.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mRecyclerView.getVisibility() == VISIBLE) {
              hideControls();
              mHandler.removeCallbacks(mRunnable);
            } else {
              showControls();
              mHandler.removeCallbacks(mRunnable);
              mHandler.postDelayed(mRunnable, 10000);
            }
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

  private void initMenu() {
    mTitle = findViewById(R.id.title);
    mMenu = findViewById(R.id.tool_bar);
    ImageView mBack = findViewById(R.id.back);
    mBack.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            getNavigation().goBack();
          }
        });
    ImageView mFeb = findViewById(R.id.fev);
    mFeb.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            FmRadioApplication.Get()
                .getRadioManager()
                .toggleLove(
                    getCurId(),
                    new RadioManager.LoveCallback() {
                      @Override
                      public void isLoveMarked(boolean isMarked) {
                        mFeb.setImageResource(
                            isMarked
                                ? R.drawable.ic_love_fill_white_32
                                : R.drawable.ic_love_white_32);
                      }
                    });
          }
        });
  }

  private void initRV() {
    mRecyclerView = (RecyclerView) findViewById(R.id.rv);
    RecyclerView.LayoutManager mLayoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mTVAdapter = new RadioListAdapter(getContext(), RadioListAdapter.ItemStyle.VIDEO_PLAYER);
    mRecyclerView.setAdapter(mTVAdapter);
    mRecyclerView.addOnItemTouchListener(
        new RecyclerTouchListener(
            getContext(),
            mRecyclerView,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                mCurIndex = position;
                play();
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));
  }

  private void initControls() {
    mPlayPause = findViewById(R.id.play_pause);
    mPlayPause.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mVideoView.isPlaying()) {
              mVideoView.pause();
              mPlayPause.setImageResource(R.drawable.ic_play_white_48);
            } else {
              mVideoView.start();
              mPlayPause.setImageResource(R.drawable.ic_pasue_white_48);
            }
          }
        });

    mNext = findViewById(R.id.next);
    mNext.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            playNext();
          }
        });

    mPrev = findViewById(R.id.previous);
    mPrev.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            playPrev();
          }
        });
  }

  private void play() {
    if (mCurList == null) {
      DLog.d("mCurList is empty");
      return;
    }
    Radio r = mCurList.get(mCurIndex);
    FmRadioApplication.Get()
        .getRadioManager()
        .setCurrentRadio(getCurId(), RadioManager.STATE.TRY_PLAYING);
    mVideoView.setVideoURI(Uri.parse(r.getMediaUrl()));
    mTitle.setText(WordUtils.capitalize(r.getName()));
  }

  private void playPrev() {
    mCurIndex--;
    if (mCurIndex < 0) {
      mCurIndex = mCurList.size() - 1;
    }
    play();
  }

  private void playNext() {
    mCurIndex++;
    if (mCurIndex >= mCurList.size()) {
      mCurIndex = 0;
    }
    play();
  }

  private void showControls() {
    mRecyclerView.setVisibility(VISIBLE);
    mMenu.setVisibility(VISIBLE);
    mButtonHolder.setVisibility(VISIBLE);
  }

  private void hideControls() {
    mRecyclerView.setVisibility(INVISIBLE);
    mMenu.setVisibility(INVISIBLE);
    mButtonHolder.setVisibility(INVISIBLE);
  }
}
