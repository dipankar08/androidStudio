package in.peerreview.fmradioindia.activities.radio.view;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.List;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.radio.IRadioContract;
import in.peerreview.fmradioindia.activities.radio.model.RadioNode;
import in.peerreview.fmradioindia.activities.radio.presenter.RadioPresenter;
import in.peerreview.fmradioindia.activities.welcome.IWelcomeContract;
import in.peerreview.fmradioindia.activities.welcome.presenter.WelcomePresenter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import pl.droidsonroids.gif.GifImageView;

public class RadioActivity extends AppCompatActivity implements IRadioContract.View ,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private RecyclerView rv;
    private RVAdapter adapter;
    private ImageView play,next,prev,fev,lock,unlock;
    private GifImageView tryplayin;
    private TextView message, isplaying;
    private ViewGroup lock_screen;
    LinearLayout qab;

    private IRadioContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
            presenter = new RadioPresenter(this);
        initViews();
    }


    private void initViews() {

        message = (TextView)findViewById(R.id.message);
        tryplayin = (GifImageView)findViewById(R.id.tryplaying);
        isplaying = (TextView) findViewById(R.id.isplaying);
        qab = (LinearLayout) findViewById(R.id.qab);

        play = (ImageView)findViewById(R.id.play);
        prev = (ImageView)findViewById(R.id.prev);
        next = (ImageView)findViewById(R.id.next);
        fev = (ImageView)findViewById(R.id.fev);
        lock = (ImageView)findViewById(R.id.lock);
        unlock = (ImageView)findViewById(R.id.unlock);
        lock_screen = (ViewGroup) findViewById(R.id.lock_screen);



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.playCurrent();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.playPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.playNext();
            }
        });

        fev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.makeCurrentFev();
            }
        });
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLockUI();
            }
        });
        unlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                hideLockUI();
                return true;
            }
        });

        //Setting up the adapter
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new RVAdapter(null,this,presenter);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        presenter.loadData();
        rv.setItemAnimator(new SlideInLeftAnimator());

        //2. Displaying Serach view.
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                ShowQAB();
                Log.d("Dipankar",newText);
                presenter.filterByText(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Dipankar",query);
                presenter.filterByText(query);
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowQAB();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //Do something on collapse Searchview
                presenter.clearFilter();
                HideQAB();
                return false;
            }
        });

        //Setup Tool Bar
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */
    }

    private void setSupportActionBar(Toolbar toolbar) {

    }

    void refreshList(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateList(List<RadioNode> nodes){
        adapter.update(nodes);
    }

    //private UI Methods
    @Override
    public void renderPlayUI(String msg){
        message.setText(msg);
        play.setImageResource(R.drawable.play);
        play.setVisibility(View.VISIBLE);
        isplaying.setVisibility(View.GONE);
        tryplayin.setVisibility(View.GONE);
        fev.setVisibility(View.GONE);

    }

    @Override
    public void renderTryPlayUI(String msg){
        message.setText(msg);
        play.setImageResource(R.drawable.play);
        play.setVisibility(View.GONE);
        isplaying.setVisibility(View.GONE);
        fev.setVisibility(View.GONE);
        tryplayin.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void renderPauseUI(String msg){
        play.setImageResource(R.drawable.pause);
        play.setVisibility(View.VISIBLE);
        fev.setVisibility(View.VISIBLE);
        isplaying.setVisibility(View.VISIBLE);
        tryplayin.setVisibility(View.GONE);
    }
    
    void ShowQAB(){
        qab.setVisibility(View.VISIBLE);
    }
   
    void HideQAB(){
        qab.setVisibility(View.GONE);
        HideKeyboard();
    }
    
    void HideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

   
    void enableFeb(){
        fev.setImageResource(R.drawable.heart_active);
        fev.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }


    void disableFeb(){
        fev.setImageResource(R.drawable.heart);
        fev.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }


    void showLockUI(){
        lock_screen.setVisibility(View.VISIBLE);
        lock.setVisibility(View.GONE);
        unlock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));
    }


    void hideLockUI(){
        lock_screen.setVisibility(View.GONE);
        lock.setVisibility(View.VISIBLE);
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
                //AndroidUtils.RateIt();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String tag ="clear";
        if (id == R.id.s_live) {
            tag = "clear";
        } else if (id == R.id.s_kolkata) {
            tag = "kolkata";
        }else if (id == R.id.s_delhi) {
            tag = "delhi";
        }else if (id == R.id.s_mumbai) {
            tag = "mumbai";
        }else if (id == R.id.s_hyderabad) {
            tag = "hyderabad";
        }else if (id == R.id.s_pune) {
            tag = "pune";
        }else if (id == R.id.s_bangalore) {
            tag = "bangalore";
        }else if (id == R.id.s_chennai) {
            tag = "chennai";
        } else if (id == R.id.s_bangladesh) {
            tag = "bangladesh";
        } else if (id == R.id.s_hindi) {
            tag = "hindi";
        }else if (id == R.id.s_bangla) {
            tag = "bengali";
        }else if (id == R.id.s_tamil) {
            tag = "tamil";
        }else if (id == R.id.s_telegu) {
            tag = "telegu";
        }else if (id == R.id.s_marathi) {
            tag = "marathi";
        } else if (id == R.id.s_malayalam) {
            tag = "malayalam";
        } else if (id == R.id.s_kannada) {
            tag = "kannada";
        }
        presenter.filterByTag(tag);
        final String tag1 = tag;
        /*
        Telemetry.sendTelemetry("click_navigation",  new HashMap<String, String>(){{
            put("tag",tag1);
        }});*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
