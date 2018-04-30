package in.peerreview.fmradioindia.activities.radio;

import android.content.Context;
import android.os.Bundle;
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
import in.co.dipankar.quickandorid.views.StateImageButton;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.models.Node;
import java.util.HashMap;
import java.util.List;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import pl.droidsonroids.gif.GifImageView;

public class RadioActivity extends AppCompatActivity implements IRadioContract.View {
  private RecyclerView rv;
  private RVAdapter adapter;
  private ImageView play, next, prev;
  private StateImageButton fev, lock;
  private ImageView unlock;
  private GifImageView tryplayin;
  private TextView message, isplaying;
  private ViewGroup lock_screen;
  ImageButton btnNav;
  LinearLayout qab;

  private IRadioContract.Presenter presenter;

  NavigationView mNavigationView;;
  private DrawerLayout mDrawerLayout;
  private FrameLayout mDrawerHolder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_radio);
    presenter = new RadioPresenter(this);
    initViews();
    initNavigation();
  }

  private void initViews() {

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
            presenter.playCurrent();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_play");
          }
        });

    prev.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            presenter.playPrevious();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_prev");
          }
        });

    next.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            presenter.playNext();
            FMRadioIndiaApplication.Get().getTelemetry().markHit("click_next");
          }
        });

    fev.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            if (b) {
              FMRadioIndiaApplication.Get().getTelemetry().markHit("click_fev");
              presenter.makeCurrentFev(true);
            } else {
              presenter.makeCurrentFev(false);
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

    // Setting up the adapter
    rv = (RecyclerView) findViewById(R.id.rv);
    rv.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    adapter = new RVAdapter(null, this, presenter);
    rv.setLayoutManager(llm);
    rv.setAdapter(adapter);
    presenter.loadData();
    rv.setItemAnimator(new SlideInLeftAnimator());

    // 2. Displaying Serach view.
    SearchView searchView = (SearchView) findViewById(R.id.searchView);
    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextChange(String newText) {
            ShowQAB();
            presenter.filterByText(newText);
            return true;
          }

          @Override
          public boolean onQueryTextSubmit(String query) {
            presenter.filterByText(query);
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
            presenter.clearFilter();
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
            presenter.filterByTag(tag);
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
        presenter.showRecent();
        break;
      case R.id.qsb_fev:
        presenter.showFeb();
        break;
      case R.id.qsb_hindi:
        presenter.filterByTag("hindi");
        break;
      case R.id.qsb_kolkata:
        presenter.filterByTag("kolkata");
        break;
      case R.id.qsb_bangaladesh:
        presenter.filterByTag("bangladesh");
        break;
      case R.id.qsb_clear:
        presenter.clearFilter();
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
}
