package in.peerreview.fmradioindia.applogic;

import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.peerreview.fmradioindia.ui.MainActivity;

public class MusicService extends MusicForegroundService {
  @Override
  protected Class getActivityClass() {
    return MainActivity.class;
  }
}
