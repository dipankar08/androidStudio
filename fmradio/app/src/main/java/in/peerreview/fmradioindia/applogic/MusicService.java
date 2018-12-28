package in.peerreview.fmradioindia.applogic;

import in.peerreview.fmradioindia.newui.MainActivity;
import in.co.dipankar.quickandorid.services.MusicForegroundService;

public class MusicService extends MusicForegroundService {
    @Override
    protected Class getActivityClass() {
        return MainActivity.class;
    }
}
