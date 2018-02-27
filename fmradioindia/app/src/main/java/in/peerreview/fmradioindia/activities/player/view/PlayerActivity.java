package in.peerreview.fmradioindia.activities.player.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.player.IPlayerContract;
import in.peerreview.fmradioindia.activities.player.presenter.PlayerPresenter;
import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.views.DImageView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/** Created by dip on 2/19/18. */
public class PlayerActivity extends AppCompatActivity
    implements IPlayerContract.View, View.OnClickListener {

  private static final String TAG = "MainActivity";
  private IPlayerContract.Presenter presenter;

  private DImageView btnRepeat, btnVol, btnLove, btnPlay, btnNext, btnPrev, btnDownload, btnClose;
  private TextView tvTitle, tvSubtitle, tvTotalDur, tvCurDuration;
  private ImageView ivCover, ivLogo;
  private SeekBar mSeekBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new PlayerPresenter(this);
    setContentView(R.layout.activity_playlist);
    initViews();
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      Bundle bundle = getIntent().getExtras();
      ArrayList<MusicNode> arraylist = bundle.getParcelableArrayList("mylist");
      int startIndex = bundle.getInt("start", 0);
      presenter.loadAlbum(arraylist, startIndex);
      presenter.play();
    }
  }

  private void initViews() {
    btnPlay = (DImageView) findViewById(R.id.play);
    btnNext = (DImageView) findViewById(R.id.next);
    btnPrev = (DImageView) findViewById(R.id.prev);
    btnLove= (DImageView) findViewById(R.id.like);
    btnVol = (DImageView) findViewById(R.id.vol);
    btnDownload = (DImageView) findViewById(R.id.download);
    btnRepeat = (DImageView) findViewById(R.id.repeat);
    btnDownload = (DImageView) findViewById(R.id.download);
    btnClose = (DImageView) findViewById(R.id.close);

    btnPlay.setOnClickListener(this);
    btnNext.setOnClickListener(this);
    btnPrev.setOnClickListener(this);
    btnLove.setOnClickListener(this);
    btnVol.setOnClickListener(this);
    btnDownload.setOnClickListener(this);
    btnRepeat.setOnClickListener(this);
    btnDownload.setOnClickListener(this);
    btnClose.setOnClickListener(this);

    tvTitle = (TextView) findViewById(R.id.title);
    tvSubtitle = (TextView) findViewById(R.id.subtitle);
    tvAlbumTitle = (TextView) findViewById(R.id.almumtitle);
    tvTotalDur = (TextView) findViewById(R.id.total_duration);
    tvCurDuration = (TextView) findViewById(R.id.cur_duration);

    ivCover = (ImageView) findViewById(R.id.cover);
    ivLogo = (ImageView) findViewById(R.id.logo);

    mSeekBar = (SeekBar) findViewById(R.id.seekbar);
    mSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
              presenter.setSeekLocation(progress);
            }
          }
        });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.play:
        if (presenter.isPlaying()) {
          presenter.pause();
        } else {
          presenter.playOrResume();
        }
        break;
      case R.id.next:
        presenter.playNext();
        break;
      case R.id.prev:
        presenter.playPrevious();
        break;
      case R.id.like:
        boolean state = btnLove.isViewEnabled();
        btnLove.setViewEnabled(!state);
        presenter.setLikeState(!state);
        break;
      case R.id.vol:
        boolean state1 = btnVol.isViewEnabled();
        btnVol.setViewEnabled(!state1);
        presenter.setMuteState(!state1);
        break;
      case R.id.download:
        presenter.download();
        break;
      case R.id.close:
        finish();
        break;
      case R.id.repeat:
        if (btnRepeat.isViewEnabled()) {
          btnRepeat.setViewEnabled(false);
          presenter.setRepeat(false);
        } else {
          btnRepeat.setViewEnabled(true);
          presenter.setRepeat(true);
        }
        break;
    }
  }

  @Override
  public void showPlayUI(MusicNode musicNode) {
    btnPlay.setViewEnabled(true);
    Picasso.with(this).load(musicNode.getImage_url()).fit().centerCrop().into(ivCover);
    Picasso.with(this).load(musicNode.getImage_url()).into(ivLogo);
    tvTitle.setText(musicNode.getTitle());
    tvSubtitle.setText(musicNode.getSubtitle());
  }

  @Override
  public void showPauseUI(MusicNode musicNode) {
    Picasso.with(this).load(musicNode.getImage_url()).into(ivCover);
    Picasso.with(this).load(musicNode.getImage_url()).into(ivLogo);
    tvTitle.setText(musicNode.getTitle());
    tvSubtitle.setText(musicNode.getSubtitle());
    btnPlay.setViewEnabled(false);
  }

  @Override
  public void enableNext() {
    btnNext.setViewEnabled(true);
  }

  @Override
  public void disableNext() {
    btnNext.setViewEnabled(false);
  }

  @Override
  public void enablePrevious() {
    btnPrev.setViewEnabled(true);
  }

  @Override
  public void disablePrevious() {
    btnPrev.setViewEnabled(false);
  }

  @Override
  public void updateSeekBarInfo(int total, int cur) {
    tvTotalDur.setText(MiliToStr(total));
    tvCurDuration.setText(MiliToStr(cur));
    mSeekBar.setProgress((int) ((float) cur / total * 100));
  }

  @Override
  public void showLoadUI() {
    tvTitle.setText("Loading ....");
  }

  @Override
  public void hideLoadUI() {
    tvTitle.setText("");
    presenter.play();
  }

  @Override
  public void showDownloading() {
    btnDownload.setViewEnabled(true);
  }

  @Override
  public void hideDownloading() {
    btnDownload.setViewEnabled(false);
  }

  @Override
  public void markFavorite() {
    btnLove.setViewEnabled(true);
  }

  @Override
  public void unmarkFavorite() {
    btnLove.setViewEnabled(false);
  }

  @Override
  public void showMute() {
    btnVol.setViewEnabled(false);
  }

  @Override
  public void hideMute() {
    btnVol.setViewEnabled(true);
  }

  @Override
  public void markRepeat() {
    btnRepeat.setViewEnabled(true);
  }

  @Override
  public void unmarkRepeat() {
    btnRepeat.setViewEnabled(false);
  }

  private String MiliToStr(int millis) {
    return String.format(
        "%dm%ds",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }
}
