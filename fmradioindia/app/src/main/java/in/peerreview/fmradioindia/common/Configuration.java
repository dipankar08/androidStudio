package in.peerreview.fmradioindia.common;

/** Created by dip on 4/29/18. */
public class Configuration {
  public static String DB_ENDPOINT = "http://simplestore.dipankar.co.in/api/nodel_bengalifm";
  public static String TELEMETRY_ENDPOINT = "http://simplestore.dipankar.co.in/api/stat_bengalifm";
  public static String GATEKEEP_ENDPOINT = "http://simplestore.dipankar.co.in/api/gk_bengalifm";

  public static String RANK_UP_URL = DB_ENDPOINT + "?_cmd=rankup&_payload=rank&id=";
  public static String RANK_DOWN_URL = DB_ENDPOINT + "?_cmd=rankdown&_payload=rank&id=";
}
