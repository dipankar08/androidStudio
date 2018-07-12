package in.peerreview.fmradioindia.activities.radio;

import android.content.Context;

import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.fmradioindia.activities.FMRadioIndiaApplication;
import in.peerreview.fmradioindia.common.AlertUtils;


public class RadioHelper {
    public final String PREF_FLAG_RATE_DIALOG ="flag_rate_dialog";
    private final String PREF_PLAY_COUNT = "COUNT_OF_RATE_DIALOG_SHOWN";
    private Context mContext;
    private SharedPrefsUtil mSharedPrefsUtil;
    private AlertUtils mAlertUtils;

    public RadioHelper(Context context){
        mContext = context;
        mSharedPrefsUtil = SharedPrefsUtil.getInstance();
        mAlertUtils = new AlertUtils(context);
    }

    public void shouldShowRateDialog(){
        if(mSharedPrefsUtil.getBoolean(PREF_FLAG_RATE_DIALOG, Boolean.TRUE)){
            mAlertUtils.showRateAlert();
            mSharedPrefsUtil.setBoolean(PREF_FLAG_RATE_DIALOG, Boolean.FALSE);
        }
    }


    public void incrementPlayCount(){
        int play_count = mSharedPrefsUtil.getInt(PREF_PLAY_COUNT,0)+1;
        mSharedPrefsUtil.setInt(PREF_PLAY_COUNT, play_count);
        if(play_count % 10 == 0){
            shouldShowRateDialog();
        }

        if(play_count % 100 == 0){
            //shouldShowRateDialog();
        }
    }
    

    public boolean isQuickListEnabled() {
        return FMRadioIndiaApplication.Get().getGateKeeperUtils().isFeatureEnabled("GK_QUICK_LIST", false);
    }
}
