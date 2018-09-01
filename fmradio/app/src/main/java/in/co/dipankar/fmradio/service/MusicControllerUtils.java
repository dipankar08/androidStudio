package in.co.dipankar.fmradio.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.data.radio.RadioManager;
import in.co.dipankar.quickandorid.services.Item;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

import static android.content.Context.BIND_AUTO_CREATE;

public class MusicControllerUtils {

    Context mContext;
    private String mID;
    RadioManager mRadioManager;
    public MusicControllerUtils(Context context){
        mContext = context;
        mRadioManager = FmRadioApplication.Get().getRadioManager();
    }
    public void attach(Context context){
        mContext = context;

    }
    public void detach(){

    }

    public void playPause(){
        Intent mService = new Intent(mContext, MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PLAY_PAUSE);
        mContext.startService(mService);
    }
    public void next(){
        Intent mService = new Intent(mContext, MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.NEXT);
        mContext.startService(mService);
    }

    public void prev(){
        Intent mService = new Intent(mContext, MusicService.class);
        mService.setAction(MusicForegroundService.Contracts.PREV);
        mContext.startService(mService);
    }

    public void stop(){

    }

    public void play(String id){
        if(id == null) {
            return;
        }

        if(mRadioManager.getCurrentRadio() != null && mRadioManager.getCurrentRadio().getId().equals(id)){
            if(mRadioManager.getState() == RadioManager.STATE.RESUME || mRadioManager.getState() == RadioManager.STATE.SUCCESS){
                // already playing..
                return;
            }
        }

        int mCurIndex = 0;
        List<Radio> mCurList = mRadioManager.getAllRadioForId(id);
        if(mCurList == null) {
               return;
        }
        for(int i =0;i<mCurList.size();i++){
            if(mCurList.get(i).getId().equals(id)){
                mCurIndex = i;
                break;
            }
        }
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.bindService(intent, mConnection, BIND_AUTO_CREATE);

        Intent mService = new Intent(mContext, MusicService.class);
        List<Item> item = new ArrayList<>();
        for (Radio r: mCurList){
            item.add(new Item(r.getId(), r.getName(), r.getMediaUrl()));
        }
        mService.putExtra("INDEX",mCurIndex);
        mService.putExtra("LIST", (Serializable) item);
        mService.setAction(MusicForegroundService.Contracts.START);
        mContext.startService(mService);
    }

    private ServiceConnection mConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MusicForegroundService.LocalBinder myLocalBinder = (MusicForegroundService.LocalBinder) iBinder;
                    MusicForegroundService musicService = myLocalBinder.getService();
                    DLog.d("Connected to bounded service");
                    musicService.setCallback(new MusicForegroundService.Callback() {
                        @Override
                        public void onTryPlaying(String id, String msg) {
                            DLog.d("Binder::onTryPlaying called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.TRY_PLAYING);
                        }

                        @Override
                        public void onSuccess(String id, String ms) {
                            DLog.d("Binder::onSuccess called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.SUCCESS);
                        }

                        @Override
                        public void onResume(String id, String ms) {
                            DLog.d("Binder::onResume called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.RESUME);
                        }

                        @Override
                        public void onPause(String id, String msg) {
                            DLog.d("Binder::onPause called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.PAUSED);
                        }

                        @Override
                        public void onError(String id, String msg) {
                            DLog.d("Binder::onError called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.ERROR);
                        }

                        @Override
                        public void onSeekBarPossionUpdate(String id, int total, int cur) {
                            DLog.d("Binder::onSeekBarPossionUpdate called");
                        }

                        @Override
                        public void onMusicInfo(String id, HashMap<String, Object> info) {
                            DLog.d("Binder::onMusicInfo called");
                        }

                        @Override
                        public void onComplete(String id, String msg) {
                            DLog.d("Binder::onComplete called");
                            FmRadioApplication.Get().getRadioManager().setCurrentRadio(id, RadioManager.STATE.COMPLETE);
                        }
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    DLog.d("Disconnected to bounded service");
                }
            };
}
