package in.co.dipankar.fmradio.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import in.co.dipankar.fmradio.ui.activities.MainActivity;
import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.co.dipankar.quickandorid.utils.DLog;

public class MusicService extends MusicForegroundService {
    @Override
    protected Class getActivityClass() {
        return MainActivity.class;
    }

}

