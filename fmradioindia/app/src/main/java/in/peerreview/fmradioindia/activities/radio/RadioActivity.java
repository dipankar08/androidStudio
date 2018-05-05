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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import in.co.dipankar.quickandorid.buttonsheet.CustomButtonSheetView;
import in.co.dipankar.quickandorid.buttonsheet.SheetItem;
import in.co.dipankar.quickandorid.utils.AudioRecorderUtil;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.NotificationView;
import in.co.dipankar.quickandorid.views.StateImageButton;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.models.Node;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.peerreview.fmradioindia.receivers.AlermReceiver;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import pl.droidsonroids.gif.GifImageView;

public class RadioActivity extends AppCompatActivity implements IRadioContract.View {
  private RecyclerView mRecyclerView;
  private RVAdapter adapter;
  private ImageView play, next, prev;
  private StateImageButton fev, lock;
  private ImageView unlock;
  private GifImageView tryplayin;
  private TextView message, isplaying;
  private ViewGroup lock_screen;
  private int mCurrentSelection;
  private NotificationView mNotificationView;
  ImageButton btnNav;
  LinearLayout qab;

  private IRadioContract.Presenter mPresenter;

  NavigationView mNavigationView;;
  private DrawerLayout mDrawerLayout;
  private FrameLayout mDrawerHolder;
  private AudioRecorderUtil mAudioRecorderUtil;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_radio);
    mPresenter = new RadioPresenter(this);
    initViews();
    initNavigation();
    processIntent();
  }

    private void processIntent() {
      String startID = getIntent().getStringExtra("START_WITH");
      if(startID != null) {
          DLog.e("Asked to play with:" + startID);
          mPresenter.playById(startID);
      }
    }

    private void initViews() {
      initPlayer();
      initList();
      initSerachView();
      InitCustomButtonSheetView();
      initNotification();
      initAudioRecoder();
  }

    private void initAudioRecoder() {
        RuntimePermissionUtils.getInstance().init(this);
        mAudioRecorderUtil = new AudioRecorderUtil(this);
    }

    private void initNotification() {
      mNotificationView = (NotificationView) findViewById(R.id.noti);
    }

    private void initPlayer(){
      message = (TextView) findViewById(R.id.message);
      tryplayin = (GifImageView) findViewById(R.id.tryplaying);
      isplaying = (TextView) findViewById(R.id.isplaying);
      qab = (LinearLayout) findViewById(R.id.qab);

      play = (ImageView) findViewById(R.id.play);
      prev = (ImageView) findViewById(R.id.prev);
      next = (ImageView) findViewById(R.id.next);
      fev = (StateImageButton) findViewById(R.id.fev);
      lock = (StateImageButton) findViewById(R.id.lock);
      unlock = (ImageView) findViewById(R.id.unlock);
      lock_screen = (ViewGroup) findViewById(R.id.lock_screen);
      btnNav = (ImageButton) findViewById(R.id.btn_nav);
      btnNav.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mDrawerLayout.openDrawer(Gravity.LEFT);
                  }
              });

      play.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mPresenter.playCurrent();
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

      fev.setCallBack(
              new StateImageButton.Callback() {
                  @Override
                  public void click(boolean b) {
                      if (b) {
                          FMRadioIndiaApplication.Get().getTelemetry().markHit("click_fev");
                          mPresenter.makeCurrentFev(true);
                      } else {
                          mPresenter.makeCurrentFev(false);
                      }
                  }
              });

      lock.setCallBack(
              new StateImageButton.Callback() {
                  @Override
                  public void click(boolean b) {
                      showLockUI();
                      FMRadioIndiaApplication.Get().getTelemetry().markHit("click_lock");
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
  }
  private void initList(){
      mRecyclerView = (RecyclerView) findViewById(R.id.rv);
      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager llm = new LinearLayoutManager(this);
      adapter = new RVAdapter(null, this, mPresenter);
      mRecyclerView.setLayoutManager(llm);
      mRecyclerView.setAdapter(adapter);
      mPresenter.loadData();
      mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
      mRecyclerView.addOnItemTouchListener(
              new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                  @Override public void onItemClick(View view, int position) {
                      mPresenter.play(position);
                      mCurrentSelection = position;
                      FMRadioIndiaApplication.Get().getTelemetry().markHit("RadioItemViewHolder_onClick");
                  }

                  @Override public void onLongItemClick(View view, int position) {
                      mCustomButtonSheetView.show();
                      mCurrentSelection = position;
                      FMRadioIndiaApplication.Get().getTelemetry().markHit("RadioItemViewHolder_onLongClick");
                  }
              })
      );
  }
  private void initSerachView(){
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
  }
  private void initNavigation() {
    mNavigationView = (NavigationView) findViewById(R.id.nav_view);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerHolder = (FrameLayout) findViewById(R.id.drawer_holder);
    // assigning the listener to the NavigationView
    mNavigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

            mDrawerLayout.closeDrawers();
            return true;
          }
        });
  }

    private CustomButtonSheetView mCustomButtonSheetView;
    private void InitCustomButtonSheetView(){
        mCustomButtonSheetView = (CustomButtonSheetView)findViewById(R.id.custom_endbutton_sheetview);
        List<CustomButtonSheetView.ISheetItem> mSheetItems = new ArrayList<>();
        mSheetItems.add(
                new SheetItem(
                        102,
                        "Add to Fev",
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
                        103,
                        "Remove from Fev",
                        CustomButtonSheetView.Type.BUTTON,
                        new CustomButtonSheetView.Callback() {
                            @Override
                            public void onClick(int v) {
                                mPresenter.makeCurrentFev(false);
                            }
                        },
                        null));
        mSheetItems.add(
                new SheetItem(
                        104,
                        "Schedule auto stop",
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
                        new CharSequence[] {"30min", "1Hrs", "1hrs 30Min","2 hrs","Cancel"}));
        mSheetItems.add(
                new SheetItem(
                        105,
                        "Schedule auto start",
                        CustomButtonSheetView.Type.OPTIONS,
                        new CustomButtonSheetView.Callback() {
                            @Override
                            public void onClick(int v) {
                                switch (v) {
                                    case 0:
                                        setUpAlerm(5, mCurrentSelection);
                                        break;
                                    case 1:
                                        setUpAlerm(6, mCurrentSelection);
                                        break;
                                    case 2:
                                        setUpAlerm(7, mCurrentSelection);
                                        break;
                                    case 3:
                                        setUpAlerm(8, mCurrentSelection);
                                        break;
                                    case 4:
                                        cancelAlerm();
                                        break;
                                }
                            }
                        },
                        new CharSequence[] {"5:00 AM", "6:00AM", "7:00 AM","8 AM","Cancel"}));

        mSheetItems.add(
                new SheetItem(
                        106,
                        "Start Recoder",
                        CustomButtonSheetView.Type.BUTTON,
                        new CustomButtonSheetView.Callback() {
                            @Override
                            public void onClick(int v) {
                                startRecoding();
                            }
                        },
                        null));

        mSheetItems.add(
                new SheetItem(
                        107,
                        "Stop Recoding",
                        CustomButtonSheetView.Type.BUTTON,
                        new CustomButtonSheetView.Callback() {
                            @Override
                            public void onClick(int v) {
                                mAudioRecorderUtil.stopRecord();
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
    private void startRecodingContinue(){
        mAudioRecorderUtil.startRecord(new AudioRecorderUtil.Callback() {
            @Override
            public void onStart() {
                mNotificationView.showInfo("Recoding started");
            }

            @Override
            public void onError(String error) {
                mNotificationView.showError(error);
            }

            @Override
            public void onStop(String path) {
                mNotificationView.showError("Recoding Complete");
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {}

  void refreshList() {
    adapter.notifyDataSetChanged();
  }

  @Override
  public void updateList(List<Node> nodes) {
    adapter.update(nodes);
  }

  // private UI Methods
  @Override
  public void renderPlayUI(String msg) {
    message.setText(msg);
    play.setImageResource(R.drawable.play);
    play.setVisibility(View.VISIBLE);
    isplaying.setVisibility(View.GONE);
    tryplayin.setVisibility(View.GONE);
  }

  @Override
  public void showToast(String s) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void renderTryPlayUI(final String msg) {
    message.setText(msg);
    play.setImageResource(R.drawable.play);
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
    play.setImageResource(R.drawable.pause);
    play.setVisibility(View.VISIBLE);
    isplaying.setVisibility(View.VISIBLE);
    tryplayin.setVisibility(View.GONE);
  }

  void ShowQAB() {
    qab.setVisibility(View.VISIBLE);
    FMRadioIndiaApplication.Get().getTelemetry().markHit("ShowQAB");
  }

  void HideQAB() {
    qab.setVisibility(View.GONE);
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
        return true;
      default:
        return super.onOptionsItemSelected(menuItem);
    }
  }

  void handleFilter(final View v) {
    switch (v.getId()) {
      case R.id.qsb_recent:
        mPresenter.showRecent();
        break;
      case R.id.qsb_fev:
        mPresenter.showFeb();
        break;
      case R.id.qsb_hindi:
        mPresenter.filterByTag("hindi");
        break;
      case R.id.qsb_kolkata:
        mPresenter.filterByTag("kolkata");
        break;
      case R.id.qsb_bangaladesh:
        mPresenter.filterByTag("bangladesh");
        break;
      case R.id.qsb_clear:
        mPresenter.clearFilter();
        break;
    }
    FMRadioIndiaApplication.Get()
        .getTelemetry()
        .sendTelemetry(
            "QSB_handleFilter",
            new HashMap<String, String>() {
              {
                put("msg", "" + v.getId());
              }
            });
  }

  private CountDownTimer mAppFinishTimer;
    private void handleStop(final int rem) {
        cancelStop();
        mAppFinishTimer = new CountDownTimer(rem *60* 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                mNotificationView.showInfo("The app will close in " + sec/60 +" min and "+sec%60 + " sec.", null, -1);
            }

            @Override
            public void onFinish() {
                RadioActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        mAppFinishTimer.start();

        FMRadioIndiaApplication.Get().getTelemetry().sendTelemetry("handleStop",new HashMap<String, String>() {
            {
                put("rem", rem+"");
            }
        });
    }
    private void cancelStop(){
        if (mAppFinishTimer != null) {
            mAppFinishTimer.cancel();
            mNotificationView.hide();
        }
    }

    private void setUpAlerm(final int timeInHrs, int idx){
        final String channel_id = adapter.getItem(idx).getId();
        final String name = adapter.getItem(idx).getTitle();
        Intent intent = new Intent(this, AlermReceiver.class); // CORRECT
        intent.putExtra("START_WITH",channel_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT ); // CORRECT

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // CORRECT

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, timeInHrs);

       // alarmManager.set( AlarmManager.RTC, System.currentTimeMillis() + 20000, pendingIntent );

        alarmManager.setRepeating(AlarmManager.RTC, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        mNotificationView.showInfo("Alarm got set successfully!");
        FMRadioIndiaApplication.Get().getTelemetry().sendTelemetry("setUpAlerm",new HashMap<String, String>() {
            {
                put("timeInHrs", timeInHrs+"");
                put("name", name);
            }
        });
    }

    private void cancelAlerm(){
        Intent intent = new Intent(this, AlermReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
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
}
