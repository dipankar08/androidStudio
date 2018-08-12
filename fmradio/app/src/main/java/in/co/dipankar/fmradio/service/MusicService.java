package in.co.dipankar.fmradio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import in.co.dipankar.fmradio.ui.activities.MainActivity;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

public class MusicService extends MusicForegroundService {
    private IBinder mBinder = new MyBinder();
    @Override
    protected Class getActivityClass() {
        return MainActivity.class;
    }

    @Override
    public IBinder onBind(Intent intent) {
        DLog.d("MusicService::onBind called");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        DLog.d("MusicService::onRebind called");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DLog.d("MusicService::onUnBind called");
        return true;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}

