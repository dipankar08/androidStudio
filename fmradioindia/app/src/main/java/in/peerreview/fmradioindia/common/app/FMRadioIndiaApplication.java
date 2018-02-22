package in.peerreview.fmradioindia.common.app;

import static in.peerreview.fmradioindia.common.utils.Configuration.GATEKEEPER_ENDPOINT;
import static in.peerreview.fmradioindia.common.utils.Configuration.TELEMETRY_ENDPOINT;
import static in.peerreview.fmradioindia.common.utils.Configuration.TEST_NETWORK_DATA;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import in.peerreview.fmradioindia.common.utils.DiskSearchUtils;
import in.peerreview.fmradioindia.common.utils.FileDownloadUtils;
import in.peerreview.fmradioindia.common.utils.GateKeeperUtils;
import in.peerreview.fmradioindia.common.utils.Network;
import in.peerreview.fmradioindia.common.utils.Player;
import in.peerreview.fmradioindia.common.utils.TelemetryUtils;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

public class FMRadioIndiaApplication extends Application {
  // Called when the application is starting, before any other application objects have been
  // created.
  // Overriding this method is totally optional!

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "FMRadioIndiaApplication::OnCreate called");
    init();
  }

  private void init() {
    if (mIsInit == true) {
      return;
    }
    Network.getInstance().init(getApplicationContext());
    GateKeeperUtils.getInstance().init(getApplicationContext(), GATEKEEPER_ENDPOINT);
    TelemetryUtils.getInstance().init(getApplicationContext(), TELEMETRY_ENDPOINT);
    Network.getInstance().init(getApplicationContext());

    // test();
    mIsInit = true;
  }

  // Called by the system when the device configuration changes while your component is running.
  // Overriding this method is totally optional!
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "FMRadioIndiaApplication::onConfigurationChanged called");
  }

  // This is called when the overall system is running low on memory,
  // and would like actively running processes to tighten their belts.
  // Overriding this method is totally optional!
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Log.d("DIPANKAR", "FMRadioIndiaApplication::onLowMemory called");
  }

  // Private functions
  private void test() {
    Log.d(TAG, "GK Test:" + GateKeeperUtils.getInstance().isFeatureEnabled("new_theme1", false));
    Log.d(TAG, "GK Test:" + GateKeeperUtils.getInstance().getRemoteSetting("new_theme2", "data"));

    FileDownloadUtils.getInstance()
        .download(
            "http://cloud3.raag.me/Bengali/Jaani%20Dekha%20Hobe-(Shreya%20Ghoshal)/Jaani%20Dekha%20Hobe%20(Female)-Shreya%20Ghoshal[48]::Raag.Me::.mp3",
            new FileDownloadUtils.IFIleDownloadCallback() {

              @Override
              public void onComplete(String result) {
                Log.d(TAG, "FileDownloadUtils Test onComplete:" + result);
              }

              @Override
              public void onProgress(float percentage) {
                Log.d(TAG, "FileDownloadUtils Test onProgress:" + percentage);
              }

              @Override
              public void onError(String msg) {
                Log.d(TAG, "FileDownloadUtils Test onError :" + msg);
              }
            });

    DiskSearchUtils.getInstance()
        .search(
            ".mp3",
            new DiskSearchUtils.IDiskSearchCallback() {
              @Override
              public void onComplete(List<HashMap<String, String>> result) {
                Log.d(TAG, "DiskSearch Test onComplete:" + result.size());
                Log.d(TAG, "DiskSearch Test onComplete:" + result.toString());
              }

              @Override
              public void onError(String msg) {
                Log.d(TAG, "DiskSearch Test onError:" + msg);
              }
            });

    // test
    TelemetryUtils.getInstance()
        .sendTelemetry(
            "SampleTag",
            new HashMap<String, String>() {
              {
                put("key1", "value1");
              }
            });

    Network.getInstance()
        .send(
            TEST_NETWORK_DATA,
            new HashMap<String, String>() {
              {
                put("_cmd", "insert");
                put("key1", "value1");
                put("count", "1");
              }
            },
            new Network.INetworkCallback() {
              @Override
              public void onSuccess(JSONObject jsonObject) {
                Log.d(TAG, "Network Test onSuccess:" + jsonObject.toString());
              }

              @Override
              public void onError(String msg) {
                Log.d(TAG, "Network Test onError:" + msg);
              }
            });

    Network.getInstance()
        .send(
            TEST_NETWORK_DATA,
            new HashMap<String, String>() {
              {
                put("_cmd", "increment");
                put("id", "5a88a93c1c761b54036e6c55");
                put("_payload", "counter");
              }
            },
            new Network.INetworkCallback() {
              @Override
              public void onSuccess(JSONObject jsonObject) {
                Log.d(TAG, "Network Test onSuccess:" + jsonObject.toString());
              }

              @Override
              public void onError(String msg) {
                Log.d(TAG, "Network Test onError:" + msg);
              }
            });

    // test case for player
    Player player =
        new Player(
            new Player.IPlayerCallback() {
              @Override
              public void onTryPlaying(String msg) {
                Log.d(TAG, "Player Test onTryPlaying:" + msg);
              }

              @Override
              public void onSuccess(String msg) {
                Log.d(TAG, "Player Test onSuccess:" + msg);
              }

              @Override
              public void onResume(String msg) {}

              @Override
              public void onPause(String msg) {}

              @Override
              public void onMusicInfo(HashMap<String, Object> info) {
                Log.d(TAG, "Player Test onMusicInfo:" + info.toString());
              }

              @Override
              public void onSeekBarPossionUpdate(int total, int cur) {
                Log.d(TAG, "Player Test onSeekBarPossionUpdate:" + cur);
              }

              @Override
              public void onError(String msg) {
                Log.d(TAG, "Player Test onError:" + msg);
              }

              @Override
              public void onComplete(String msg) {
                Log.d(TAG, "Player Test onComplete:" + msg);
              }
            });

    player.play(
        "Raaz",
        "https://d.wload.vc/files/sfd215/107124/Yahan%20Pe%20Sab%20Shanti%20Hai(WapKing).mp3");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    player.seekTo(18600);
  }

  // private
  private boolean mIsInit = false;
  private final String TAG = "DIPANKAR";
}
