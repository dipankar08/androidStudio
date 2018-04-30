package in.peerreview.ping.contracts;

import in.peerreview.ping.BuildConfig;

/** Created by dip on 3/13/18. */
public final class Configuration {

  public static String HOST = BuildConfig.DEBUG ? "192.168.1.110" : "ping.dipankar.co.in";

  public static String SIGNALING_ENDPOINT = "http://" + HOST + ":7000";
  public static final String SERVER_ENDPOINT = "http://" + HOST + ":7001";
}
