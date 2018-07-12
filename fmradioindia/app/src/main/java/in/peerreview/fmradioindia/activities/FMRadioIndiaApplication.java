package in.peerreview.fmradioindia.activities;

import static in.peerreview.fmradioindia.common.Configuration.GATEKEEP_ENDPOINT;
import static in.peerreview.fmradioindia.common.Configuration.TELEMETRY_ENDPOINT;

import android.app.Application;
import android.content.res.Configuration;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.GateKeeperUtils;
import in.co.dipankar.quickandorid.utils.INetwork;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.co.dipankar.quickandorid.utils.TelemetryUtils;
import in.peerreview.fmradioindia.common.models.NodeManager;

public class FMRadioIndiaApplication extends Application {

  private INetwork mNetwork;
  private static FMRadioIndiaApplication mApplication;
  private NodeManager mNodeManager;
  private TelemetryUtils mTelemetryUtils;
  private GateKeeperUtils mGateKeeperUtils;
  @Override
  public void onCreate() {
    super.onCreate();
    DLog.d("Application created");
    mApplication = this;
    SharedPrefsUtil.getInstance().init(this);
  }

  public static FMRadioIndiaApplication Get() {
    return mApplication;
  }

  public INetwork getNetwork() {
    if (mNetwork == null) {
      mNetwork = new Network(getApplicationContext(), true);
      DLog.d("Netwrok is created");
    }
    return mNetwork;
  }

  public NodeManager getNodeManager() {
    if (mNodeManager == null) {
      mNodeManager = new NodeManager(getApplicationContext());
    }
    return mNodeManager;
  }

    public GateKeeperUtils getGateKeeperUtils() {
        if (mGateKeeperUtils == null) {
            mGateKeeperUtils = new GateKeeperUtils(getApplicationContext(),GATEKEEP_ENDPOINT);
        }
        return mGateKeeperUtils;
    }


    public TelemetryUtils getTelemetry() {
    if (mTelemetryUtils == null) {
      mTelemetryUtils =
          new TelemetryUtils(
              getApplicationContext(),
              TELEMETRY_ENDPOINT,
              true,
              new TelemetryUtils.Callback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFail() {}
              });
    }
    return mTelemetryUtils;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }
}
