package in.peerreview.fmradioindia.activities.radio;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import in.co.dipankar.quickandorid.buttonsheet.CustomButtonSheetView;
import in.co.dipankar.quickandorid.buttonsheet.SheetItem;
import in.co.dipankar.quickandorid.utils.AndroidUtils;
import in.co.dipankar.quickandorid.utils.AudioRecorderUtil;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.MusicPlayerView;
import in.co.dipankar.quickandorid.views.NotificationView;
import in.co.dipankar.quickandorid.views.QuickListView;
import in.co.dipankar.quickandorid.views.SegmentedControl;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.CommonIntent;
import in.peerreview.fmradioindia.common.models.Node;
import in.peerreview.fmradioindia.receivers.AlermReceiver;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import pl.droidsonroids.gif.GifImageView;

public class RadioActivity extends AppCompatActivity implements IRadioContract.View {
  private ImageView play, next, prev;
  private ImageButton fev, lock, record;
  private ImageView unlock;
  private GifImageView tryplayin;
  private TextView message, isplaying;
  private ViewGroup lock_screen;

  private NotificationView mNotificationView;

  private QuickListView mQuickListView;
  private QuickListView mRadioListView;

  private View mRecordView;
  private ImageButton mRecordViewCloseBtn;
  private MusicPlayerView mMusicPlayerView;

  private String mCurSelectId;
  LinearLayout qab;

  private IRadioContract.Presenter mPresenter;
  private AudioRecorderUtil mAudioRecorderUtil;

  private CountDownTimer mRecordTimer =
      new CountUpTimer(1000) {
        @Override
        public void onTick(String time) {
          mNotificationView.updateText("Recording " + time);
        }

        @Override
        public void onFinish(int sec) {}
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
      setTheme(R.style.ActivityThemeDark);
      DLog.e("Now Dark theme");
    } else {
      setTheme(R.style.ActivityThemeLight);
      DLog.e("Now Light theme");
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_radio);
    mPresenter = new RadioPresenter(this);
    initViews();
    // initNavigation();
    processIntent();
  }

  private void processIntent() {
    String startID = getIntent().getStringExtra("START_WITH");
    if (startID != null) {
      DLog.e("Asked to play with:" + startID);
      mPresenter.playById(startID);
    }
  }

  private void initViews() {
    initQuickListView();
    initPlayer();
    initList();
    initSerachView();
    InitCustomButtonSheetView();
    initToolBar();
    initNotification();
    initAudioRecoder();
    intRecordView();
  }

  private void intRecordView() {
    mRecordView = findViewById(R.id.record_view_holder);
    mRecordViewCloseBtn = (ImageButton) findViewById(R.id.record_view_close);
    mMusicPlayerView = (MusicPlayerView) findViewById(R.id.record_player_view);
    mRecordViewCloseBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            hideRecordView();
          }
        });
    mRecordView.setOnTouchListener(
        new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            return true;
          }
        });
  }

  private void initQuickListView() {
    mQuickListView = (QuickListView) findViewById(R.id.quicklistview);
    List<QuickListView.Item> items1 = new ArrayList<>();
    for (Node node : FMRadioIndiaApplication.Get().getNodeManager().getSuggested()) {
      items1.add((QuickListView.Item) node);
    }
    mQuickListView.init(
        items1,
        new QuickListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.setCurNodeID(id);
            mPresenter.playById(id);
          }

          @Override
          public void onLongClick(String id) {
            mPresenter.setCurNodeID(id);
            mCustomButtonSheetView.show();
          }
        },
        R.layout.item,
        QuickListView.Type.HORIZONTAL);
  }

  private void initToolBar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            handleNavigationClicked(item);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
          }
        });
  }

  private void handleNavigationClicked(MenuItem item) {
    int id = item.getItemId();
    String tag = "clear";
    if (id == R.id.s_live) {
      tag = "clear";
    } else if (id == R.id.s_kolkata) {
      tag = "kolkata";
    } else if (id == R.id.s_delhi) {
      tag = "delhi";
    } else if (id == R.id.s_mumbai) {
      tag = "mumbai";
    } else if (id == R.id.s_hyderabad) {
      tag = "hyderabad";
    } else if (id == R.id.s_pune) {
      tag = "pune";
    } else if (id == R.id.s_bangalore) {
      tag = "bangalore";
    } else if (id == R.id.s_chennai) {
      tag = "chennai";
    } else if (id == R.id.s_bangladesh) {
      tag = "bangladesh";
    } else if (id == R.id.s_hindi) {
      tag = "hindi";
    } else if (id == R.id.s_bangla) {
      tag = "bengali";
    } else if (id == R.id.s_tamil) {
      tag = "tamil";
    } else if (id == R.id.s_telegu) {
      tag = "telegu";
    } else if (id == R.id.s_marathi) {
      tag = "marathi";
    } else if (id == R.id.s_malayalam) {
      tag = "malayalam";
    } else if (id == R.id.s_kannada) {
      tag = "kannada";
    }
    mPresenter.filterByTag(tag);
    final String tag1 = tag;

    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .sendTelemetry(
            "click_navigation",
            new HashMap<String, String>() {
              {
                put("tag", tag1);
              }
            });
  }

  private void initAudioRecoder() {
    RuntimePermissionUtils.getInstance().init(this);
    mAudioRecorderUtil = new AudioRecorderUtil(this);
  }

  private void initNotification() {
    mNotificationView = (NotificationView) findViewById(R.id.noti);
  }

  private void initPlayer() {
    message = (TextView) findViewById(R.id.message);
    tryplayin = (GifImageView) findViewById(R.id.tryplaying);
    isplaying = (TextView) findViewById(R.id.isplaying);
    qab = (LinearLayout) findViewById(R.id.qab);

    play = (ImageView) findViewById(R.id.play);
    prev = (ImageView) findViewById(R.id.prev);
    next = (ImageView) findViewById(R.id.next);
    fev = (ImageButton) findViewById(R.id.fev);
    lock = (ImageButton) findViewById(R.id.lock);
    unlock = (ImageView) findViewById(R.id.unlock);
    record = (ImageButton) findViewById(R.id.record);
    lock_screen = (ViewGroup) findViewById(R.id.lock_screen);

    play.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.playPause();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_play");
          }
        });

    prev.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.playPrevious();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_prev");
          }
        });

    next.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPresenter.playNext();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_next");
          }
        });

    fev.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_fev");
            mPresenter.onFevClicked();
          }
        });

    lock.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showLockUI();
            setImageSrcWithAnimation(lock, R.drawable.ic_lock_off);
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_lock");
          }
        });

    lock_screen.setOnTouchListener(
        new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            return true;
          }
        });

    unlock.setOnLongClickListener(
        new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            hideLockUI();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_unlock");
            return true;
          }
        });
    record.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!mAudioRecorderUtil.isRecording()) {
              startRecoding();
            } else {
              mAudioRecorderUtil.stopRecord();
            }
          }
        });
  }

  @Override
  public void onUpdateFevButtonState(boolean isFev) {
    if (isFev) {
      setImageSrcWithAnimation(fev, R.drawable.ic_love_on);
      mNotificationView.showSuccess("Added this channel to Feb");
    } else {
      setImageSrcWithAnimation(fev, R.drawable.ic_love_off);
      mNotificationView.showSuccess("Removed this channel to Feb");
    }
  }

  @Override
  public void notifyError(String s) {
    notifyErrorInternal(s);
  }

  private void notifyErrorInternal(final String s) {
    runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            mNotificationView.showError(s);
          }
        });
  }

  @Override
  public void notifyInfo(String s) {
    notifyErrorInternal(s);
  }

  private void notifyInfoInternal(final String s) {
    runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            mNotificationView.showInfo(s);
          }
        });
  }

  private void initList() {
    mRadioListView = (QuickListView) findViewById(R.id.radiolistview);
    List<QuickListView.Item> items1 = new ArrayList<>();
    mRadioListView.init(
        items1,
        new QuickListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.playById(id);
            FMRadioIndiaApplication.Get().getTelemetry().markHit("RadioItemViewHolder_onClick");
          }

          @Override
          public void onLongClick(String id) {
            mCustomButtonSheetView.show();
            mCurSelectId = id;
            FMRadioIndiaApplication.Get().getTelemetry().markHit("RadioItemViewHolder_onLongClick");
          }
        },
        R.layout.item_radio,
        QuickListView.Type.VERTICAL);

    mPresenter.loadData();
    // mRecyclerView.setItemAnimator(new SlideInLeftAnimator());

  }

  private void initSerachView() {
    SearchView searchView = (SearchView) findViewById(R.id.searchView);
    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextChange(String newText) {
            ShowQAB();
            mPresenter.filterByText(newText);
            return true;
          }

          @Override
          public boolean onQueryTextSubmit(String query) {
            mPresenter.filterByText(query);
            return true;
          }
        });
    searchView.setOnSearchClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ShowQAB();
          }
        });
    searchView.setOnCloseListener(
        new SearchView.OnCloseListener() {
          @Override
          public boolean onClose() {
            // Do something on collapse Searchview
            mPresenter.clearFilter();
            HideQAB();
            return false;
          }
        });

    SegmentedControl segmentedControl = (SegmentedControl) findViewById(R.id.qab);
    segmentedControl.setCallback(
        new SegmentedControl.Callback() {

          @Override
          public void onClicked(int id) {
            switch (id) {
              case 0:
                mPresenter.filterByTag("bengali");
                FMRadioIndiaApplication.Get().getTelemetry().markHit("qab_bengali");
                break;
              case 1:
                mPresenter.filterByTag("hindi");
                FMRadioIndiaApplication.Get().getTelemetry().markHit("qab_hindi");
                break;
              case 2:
                mPresenter.filterByTag("kolkata");
                FMRadioIndiaApplication.Get().getTelemetry().markHit("qab_kolkata");
                break;
              case 3:
                mPresenter.filterByTag("bangladesh");
                FMRadioIndiaApplication.Get().getTelemetry().markHit("qab_bangladesh");
                break;
            }
            HideKeyboard();
          }
        });
  }

  private CustomButtonSheetView mCustomButtonSheetView;

  private void InitCustomButtonSheetView() {
    mCustomButtonSheetView = (CustomButtonSheetView) findViewById(R.id.custom_endbutton_sheetview);
    List<CustomButtonSheetView.ISheetItem> mSheetItems = new ArrayList<>();

    mSheetItems.add(
        new SheetItem(
            103,
            "Open tutorial",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                CommonIntent.startTutorialActivity(RadioActivity.this, null);
              }
            },
            null));

    mSheetItems.add(
        new SheetItem(
            102,
            "add/remove feb",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                mPresenter.makeCurrentFev(true);
              }
            },
            null));

    mSheetItems.add(
        new SheetItem(
            104,
            "Select auto-stop duration",
            CustomButtonSheetView.Type.OPTIONS,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                switch (v) {
                  case 0:
                    handleStop(30);
                    break;
                  case 1:
                    handleStop(60);
                    break;
                  case 2:
                    handleStop(90);
                    break;
                  case 3:
                    handleStop(120);
                    break;
                  case 4:
                    cancelStop();
                    break;
                }
              }
            },
            new CharSequence[] {"30min", "1Hrs", "1hrs 30Min", "2 hrs", "Cancel"}));
    mSheetItems.add(
        new SheetItem(
            105,
            "Select auto-start time",
            CustomButtonSheetView.Type.OPTIONS,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                switch (v) {
                  case 0:
                    setUpAlerm(5, mCurSelectId);
                    break;
                  case 1:
                    setUpAlerm(6, mCurSelectId);
                    break;
                  case 2:
                    setUpAlerm(7, mCurSelectId);
                    break;
                  case 3:
                    setUpAlerm(8, mCurSelectId);
                    break;
                  case 4:
                    cancelAlerm();
                    break;
                }
              }
            },
            new CharSequence[] {"5:00 AM", "6:00AM", "7:00 AM", "8 AM", "Cancel"}));

    mSheetItems.add(
        new SheetItem(
            103,
            "Change theme",
            CustomButtonSheetView.Type.BUTTON,
            new CustomButtonSheetView.Callback() {
              @Override
              public void onClick(int v) {
                changeTheme();
              }
            },
            null));

    mCustomButtonSheetView.addMenu(mSheetItems);
    mCustomButtonSheetView.hide();
  }

  private void startRecoding() {
    RuntimePermissionUtils.getInstance()
        .askPermission(
            new String[] {
              Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            },
            new RuntimePermissionUtils.CallBack() {
              @Override
              public void onSuccess() {
                startRecodingContinue();
              }

              @Override
              public void onFail() {
                FMRadioIndiaApplication.Get()
                    .getTelemetry()
                    .markHit("exit_for_RuntimePermissionUtils_deny");
                mNotificationView.showError("You must give permission to Recording");
              }
            });
  }

  private void startRecodingContinue() {
    mAudioRecorderUtil.startRecord(
        new AudioRecorderUtil.Callback() {
          @Override
          public void onStart() {
            setImageSrcWithAnimation(record, R.drawable.ic_record_on);
            mNotificationView.ask(
                "Recoding started. Please keep quite",
                new NotificationView.AnswerCallback() {
                  @Override
                  public void onAccept() {
                    mAudioRecorderUtil.cancelRecord();
                  }

                  @Override
                  public void onReject() {
                    mAudioRecorderUtil.stopRecord();
                  }
                },
                "Cancel",
                "Stop Record");
            mRecordTimer.start();
          }

          @Override
          public void onError(String error) {
            setImageSrcWithAnimation(record, R.drawable.ic_record_off);
            mNotificationView.showError(error);
            mRecordTimer.cancel();
          }

          @Override
          public void onStop(String path) {
            setImageSrcWithAnimation(record, R.drawable.ic_record_off);
            mNotificationView.showSuccess("Recoding Complete!");
            showRecordView();
            mMusicPlayerView.play("0", path, path);
            mRecordTimer.cancel();
          }
        });
  }

  @Override
  public void updateList(List<Node> nodes) {
    List<QuickListView.Item> items1 = new ArrayList<>();
    for (Node node : nodes) {
      items1.add((QuickListView.Item) node);
    }
    mRadioListView.updateList(items1);
  }

  // private UI Methods
  @Override
  public void renderPlayUI(String msg) {
    message.setText(msg);
    play.setImageResource(R.drawable.pause);
    play.setVisibility(View.VISIBLE);
    isplaying.setVisibility(View.VISIBLE);
    tryplayin.setVisibility(View.GONE);
  }

  @Override
  public void showToast(String s) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void updateQuickList(List<Node> mNodes) {
    if (mNodes == null) {
      return;
    }
    List<QuickListView.Item> items1 = new ArrayList<>();
    for (Node node : FMRadioIndiaApplication.Get().getNodeManager().getSuggested()) {
      items1.add((QuickListView.Item) node);
    }
    if (mQuickListView != null) {
      mQuickListView.updateList(items1);
    }
  }

  @Override
  public void renderTryPlayUI(final String msg) {
    message.setText(msg);
    play.setVisibility(View.GONE);
    isplaying.setVisibility(View.GONE);
    tryplayin.setVisibility(View.VISIBLE);
    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .sendTelemetry(
            "renderTryPlayUI",
            new HashMap<String, String>() {
              {
                put("msg", msg);
              }
            });
  }

  @Override
  public void renderPauseUI(String msg) {
    message.setText(msg);
    play.setImageResource(R.drawable.play);
    play.setVisibility(View.VISIBLE);
    isplaying.setVisibility(View.GONE);
    tryplayin.setVisibility(View.GONE);
  }

  void ShowQAB() {
    mQuickListView.setVisibility(View.GONE);
    qab.setVisibility(View.VISIBLE);
    FMRadioIndiaApplication.Get().getTelemetry().markHit("ShowQAB");
  }

  void HideQAB() {
    qab.setVisibility(View.GONE);
    mQuickListView.setVisibility(View.VISIBLE);
    HideKeyboard();
  }

  void HideKeyboard() {
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  void showLockUI() {
    lock_screen.setVisibility(View.VISIBLE);
    unlock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
  }

  void hideLockUI() {
    lock_screen.setVisibility(View.GONE);
    lock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
  }

  // Activity Override methods
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.rate:
        FMRadioIndiaApplication.Get().getTelemetry().markHit("click_rate_app");
        AndroidUtils.RateIt(this);
        return true;
      default:
        return super.onOptionsItemSelected(menuItem);
    }
  }

  private CountDownTimer mAppFinishTimer;

  private void handleStop(final int rem) {
    cancelStop();
    mAppFinishTimer =
        new CountDownTimer(rem * 60 * 1000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
            long sec = millisUntilFinished / 1000;
            mNotificationView.showError(
                "The app will close in " + sec / 60 + " min and " + sec % 60 + " sec.", null, -1);
          }

          @Override
          public void onFinish() {
            RadioActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
          }
        };
    mAppFinishTimer.start();

    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .sendTelemetry(
            "handleStop",
            new HashMap<String, String>() {
              {
                put("rem", rem + "");
              }
            });
  }

  private void cancelStop() {
    if (mAppFinishTimer != null) {
      mAppFinishTimer.cancel();
      mNotificationView.hide();
    }
  }

  private void setUpAlerm(final int timeInHrs, String channel_id) {
    final String name = mPresenter.getItembyID(channel_id).getTitle();
    Intent intent = new Intent(this, AlermReceiver.class); // CORRECT
    intent.putExtra("START_WITH", channel_id);
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT); // CORRECT

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // CORRECT

    Calendar time = Calendar.getInstance();
    time.set(Calendar.HOUR_OF_DAY, timeInHrs);

    // alarmManager.set( AlarmManager.RTC, System.currentTimeMillis() + 20000, pendingIntent );

    alarmManager.setRepeating(
        AlarmManager.RTC, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    mNotificationView.showInfo("Alarm got set successfully!");
    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .sendTelemetry(
            "setUpAlerm",
            new HashMap<String, String>() {
              {
                put("timeInHrs", timeInHrs + "");
                put("name", name);
              }
            });
  }

  private void cancelAlerm() {
    Intent intent = new Intent(this, AlermReceiver.class);
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    try {
      am.cancel(pendingIntent);
      mNotificationView.showInfo("Alarm got canceled successfully!");
    } catch (Exception e) {
      mNotificationView.showError("Not able to cancel Alarm!");
      DLog.e("AlarmManager update was not canceled. " + e.toString());
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults) {
    RuntimePermissionUtils.getInstance()
        .onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void setImageSrcWithAnimation(ImageButton btn, int id) {
    if (btn != null) {
      btn.setImageResource(id);
      btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }
  }

  private abstract class CountUpTimer extends CountDownTimer {
    private static final long INTERVAL_MS = 1000;
    private final long duration;

    protected CountUpTimer(long durationMs) {
      super(durationMs, INTERVAL_MS);
      this.duration = durationMs;
    }

    public abstract void onTick(String time);

    public abstract void onFinish(int sec);

    @Override
    public void onTick(long msUntilFinished) {
      int second = (int) ((duration - msUntilFinished) / 1000);
    }

    @Override
    public void onFinish() {
      onFinish((int) duration / 1000);
    }
  }

  private void showRecordView() {
    mRecordView.setVisibility(View.VISIBLE);
  }

  private void hideRecordView() {
    mRecordView.setVisibility(View.GONE);
  }

  private void changeTheme() {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      DLog.e("Now Dark theme");
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      DLog.e("Now Light theme");
    }
    Intent i = new Intent(getApplicationContext(), RadioActivity.class);
    startActivity(i);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    finish();
  }
}
