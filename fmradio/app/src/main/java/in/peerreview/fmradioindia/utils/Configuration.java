package in.peerreview.fmradioindia.utils;

import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;

public class Configuration {
    private static final String PREF_SHOULD_SHOW_FTUX = "PREF_SHOULD_SHOW_FTUX";

    public static boolean shouldShowFTUX() {
        boolean val = SharedPrefsUtil.getInstance().getBoolean(PREF_SHOULD_SHOW_FTUX, true);
        SharedPrefsUtil.getInstance().setBoolean(PREF_SHOULD_SHOW_FTUX, false);
        return val;
    }
}
