package in.co.dipankar.bengalisuspense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import in.co.dipankar.bengalisuspense.models.Car;
import in.co.dipankar.bengalisuspense.models.ItemImpl;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.co.dipankar.quickandorid.views.MusicPlayerView;
import in.co.dipankar.quickandorid.views.QuickListView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<QuickListView.Item> mCurList;
    private QuickListView.Item mCurItem;

    private  QuickListView  quickListView;
    private MusicPlayerView musicPlayerView;
    private CustomFontTextView mPlayTitle;
    private View mPage1;
    private View mPage2;
    private ImageView mBackground;

    private  Network mNetwork;
    private StorageUtils mStorageUtils;

    private static final String URL="http://simplestore.dipankar.co.in/api/nodel_bengalisuspense";
    private static final String FILE_NAME ="cache.json";
    ItemManager mItemManager;
    private TextView mTitle;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    int mCurLavel = 0;
    private String mPrevKey = "Main Manu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNetwork = new Network(this, true);
        mStorageUtils = new StorageUtils(this);
        mItemManager = new ItemManager(this,mStorageUtils);
        mCurList = mItemManager.getMenu();

        initViews();
        setupOther();
        initAdds();

    }

    private void test() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonCarArray =
                "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
        try {
            List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
            listCar.size();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void initAdds() {
        MobileAds.initialize(this, Constants.getAdmobAdId());
        //MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                DLog.d("Add Clicked");
                mAdView.loadAd(new AdRequest.Builder().build());
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test id
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Prepare next add
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                handleBackInternal();
            }
        });
    }

    private void initViews() {

        quickListView= findViewById(R.id.quick_list1);
        musicPlayerView = findViewById(R.id.player);
        mPlayTitle  = findViewById(R.id.title);

        mPage1 = findViewById(R.id.page1);
        mPage2 = findViewById(R.id.page2);
        mBackground = findViewById(R.id.background);

        quickListView.init(mCurList, new QuickListView.Callback() {
            @Override
            public void onClick(QuickListView.Item item) {
                    mPrevKey = item.getTitle();
                    handleFwd(item);
            }

            @Override
            public void onLongClick(QuickListView.Item item) {

            }
        }, R.layout.item_view, QuickListView.Type.VERTICAL);


        Button back = findViewById(R.id.cv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });

        Button home = findViewById(R.id.cv_parent);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
gotoHome();
            }
        });

        mTitle = findViewById(R.id.cv_title);
    }

    private void handleFwd(QuickListView.Item item){
        mCurLavel ++;
        ItemImpl data  = (ItemImpl)item;
        switch (mCurLavel){
            case 1:
                updateList(mItemManager.getSubMenu(data.getTitle()));

                break;
            case 2:
                updatePlayer(data);
                break;
        }
    }

    private void handleBack(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            DLog.d("The interstitial wasn't loaded yet.");
            handleBackInternal();
        }

    }
    private void handleBackInternal(){
        if(mCurLavel == 0){
            return;
        }
        mCurLavel --;
        switch (mCurLavel){
            case 0:
                gotoHome();
                break;
            case 1:
                updateList(mItemManager.getSubMenu(mPrevKey));
                break;
            default:
        }
    }

    private void gotoHome(){
        mCurLavel = 0;
        updateList(mItemManager.getMenu());
    }

    private void updateList(List<QuickListView.Item> list){
        if(list != null && list.size() > 0) {
            mTitle.setText(list.get(0).getTitle());
            quickListView.updateList(list);
            handleToggle();
        }
    }

    private void updatePlayer(QuickListView.Item data1){
       ItemImpl data = (ItemImpl) data1;
        Toast.makeText(this,"Now Playing "+data.getSubTitle(), Toast.LENGTH_SHORT).show();
        mPlayTitle.setText(data.getTitle());
        DLog.d("Now playing:"+data.getUrl());
        musicPlayerView.play(data.getId(),data.getTitle(), data.getUrl());
        mCurLavel =2;
        Picasso.with(this)
                .load(data.getImageUrl())
                .fit()
                .centerCrop()
                .into(mBackground);
        handleToggle();
    }

    private  void handleToggle(){
        if(mCurLavel == 2){
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.VISIBLE);
        } else{
            mPage1.setVisibility(View.VISIBLE);
            mPage2.setVisibility(View.GONE);
        }
    }
    private void setupOther() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        handleBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            test();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if (id == R.id.reload) {
            mNetwork.retrive(URL, Network.CacheControl.GET_LIVE_ONLY, new Network.Callback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    String data = null;
                    try {
                        data = jsonObject.getJSONArray("out").toString();
                        if(data.length() < 10){
                            tryLoadLocalCopy();
                        } else {
                            mItemManager.buildAllData(data);
                            mStorageUtils.writeFileToCache(FILE_NAME, data, null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tryLoadLocalCopy();
                    };
                }

                @Override
                public void onError(String msg) {
                    tryLoadLocalCopy();
                }
            });
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void tryLoadLocalCopy(){
        mStorageUtils.readFileFromCache(FILE_NAME,  new StorageUtils.Callback() {
            @Override
            public void onSuccess(String data) {
                mItemManager.buildAllData(data);
            }

            @Override
            public void onError(String data) {
                loadDataFromAssert();
            }
        });
    }

    private void loadDataFromAssert(){
        mStorageUtils.loadJSONFromAsset(FILE_NAME, new StorageUtils.Callback() {
            @Override
            public void onSuccess(String data) {
                if(data != null){
                    mItemManager.buildAllData(data);
                } else{
                    showMessage("Local cache data is null");
                }
            }

            @Override
            public void onError(String data) {
                showMessage(data);
            }
        });
    }


    private void showMessage(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
