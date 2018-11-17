package in.co.dipankar.livetv.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseNavigationActivity;
import in.co.dipankar.livetv.base.Screen;
import in.co.dipankar.livetv.data.ChannelManager;
import in.co.dipankar.livetv.ui.home.HomeFragment;
import in.co.dipankar.livetv.ui.player.PlayerFragment;
import in.co.dipankar.livetv.ui.splash.SplashFragment;
import in.co.dipankar.livetv.ui.webview.MovieFragment;
import in.co.dipankar.quickandorid.utils.SimplePubSub;

public class MainActivity extends BaseNavigationActivity {

    SimplePubSub mSimplePubSub;
    ChannelManager mChannelManager;
    AudioManager mAudioManager;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
      mChannelManager = ChannelManager.Get();
      mChannelManager.init(this);
    getNavigation().navigate(Screen.SPLASH, null);

    fullscreen();
    initSimplePubSub();
      mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
      enterImmersiveMode();
      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
      wl.acquire();
  }

    private void enterImmersiveMode() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                decorView.getSystemUiVisibility()
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initSimplePubSub() {
        mSimplePubSub = new SimplePubSub(new SimplePubSub.Config() {
            @Override
            public String getURL() {
                return "ws://simplestore.dipankar.co.in:8081";
                //return "ws://192.168.1.114:8081";
            }
        });
        mSimplePubSub.addCallback(new SimplePubSub.Callback() {
            @Override
            public void onConnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"Remote Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"Remote Disconnected", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String err) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"Remote Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessage(String topic, final String args) {
                final String[] argList = args.split("#");
                final String type=argList[0];
                if(topic.equals("live_tv")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (type){
                                case "volp":
                                    mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE,
                                            AudioManager.FLAG_PLAY_SOUND);
                                    break;
                                case "voln":
                                    mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,
                                            AudioManager.FLAG_PLAY_SOUND);
                                    break;
                                case"movie":
                                    // SimplePubSub.publish("live_tv","movie#http://google.com")
                                    String url = argList[1];
                                    Bundle bundle = new Bundle();
                                    bundle.putString("URL", url);
                                    getNavigation().navigate(Screen.WEBVIEW, bundle);
                                default:
                                    mChannelManager.setAction(type);
                            }
                        }
                    });
                }
            }

            @Override
            public void onSignal(String topic, String data) {

            }
        });
        mSimplePubSub.subscribe("live_tv");
    }

    private void fullscreen() {
    int mUIFlag =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
  }

  @Override
  public Screen getHomeScreen() {
    return Screen.HOME;
  }

  @Override
  public Screen getSplashScreen() {
    return Screen.SPLASH;
  }

  @Override
  public int getContainerId() {
    return R.id.fragment_container;
  }

  @Override
  public FragmentAnimation getFragmentAnimation(Screen screen) {
    return null;
  }

  @Override
  public Fragment getFragmentForScreen(Screen screen, Bundle args) {
    switch (screen) {
      case HOME:
        return HomeFragment.getNewFragment(args);
      case SPLASH:
        return SplashFragment.getNewFragment(args);
      case PLAYER:
        return PlayerFragment.getNewFragment(args);
        case WEBVIEW:
            return MovieFragment.getNewFragment(args);
      default:
        return null;
    }
  }


    @Override
    public void onPause() {
        mSimplePubSub.disconnect();
        super.onPause();
    }

    @Override
    public void onResume() {
        mSimplePubSub.connect();
        super.onResume();
    }

    @Override
  protected void onDestroy() {
    super.onDestroy();
  }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
