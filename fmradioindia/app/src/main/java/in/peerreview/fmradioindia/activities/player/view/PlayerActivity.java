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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/** Created by dip on 2/19/18. */
public class PlayerActivity extends AppCompatActivity
    implements IPlayerContract.View, View.OnClickListener {

  private static final String TAG = "MainActivity";
  ImageView radio;
  private IPlayerContract.Presenter presenter;

  private ImageButton btnPlay, btnNext, btnPrev, btnRefersh, btnVol, btnDownload;
  private TextView tvTitle, tvSubtitle, tvTotalDur, tvCurDuration, tvAlbumTitle;
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
    btnPlay = (ImageButton) findViewById(R.id.play);
    btnNext = (ImageButton) findViewById(R.id.next);
    btnPrev = (ImageButton) findViewById(R.id.prev);
    btnRefersh = (ImageButton) findViewById(R.id.restart);
    btnVol = (ImageButton) findViewById(R.id.vol);
    btnDownload = (ImageButton) findViewById(R.id.download);

    btnPlay.setOnClickListener(this);
    btnNext.setOnClickListener(this);
    btnPrev.setOnClickListener(this);
    btnRefersh.setOnClickListener(this);
    btnVol.setOnClickListener(this);
    btnDownload.setOnClickListener(this);

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
          presenter.play();
        }
        break;
      case R.id.next:
        presenter.playNext();
        break;
      case R.id.prev:
        presenter.playPrevious();
        break;
      case R.id.restart:
        presenter.restart();
        break;
      case R.id.vol:
        presenter.mute();
        break;
      case R.id.download:
        presenter.download();
        break;
    }
  }

  @Override
  public void showPlayUI(MusicNode musicNode) {
    btnPlay.setBackgroundResource(R.drawable.pl_play);
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
    btnPlay.setBackgroundResource(R.drawable.pl_pause);
  }

  @Override
  public void enableNext() {}

  @Override
  public void disableNext() {}

  @Override
  public void enablePrevious() {}

  @Override
  public void disablePrevious() {}

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
  public void showDownloading() {}

  @Override
  public void hideDownloading() {}

  @Override
  public void markFavorite() {}

  @Override
  public void unmarkFavorite() {}

  @Override
  public void showMute() {}

  @Override
  public void hideMute() {}

  @Override
  public void markRepeat() {}

  @Override
  public void unmarkRepeat() {}

  private String MiliToStr(int millis) {
    return String.format(
        "%dm%ds",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }
}
