package in.co.dipankar.fmradio.service;

import in.co.dipankar.fmradio.ui.activities.MainActivity;
import in.co.dipankar.quickandorid.services.MusicForegroundService;

public class MusicService extends MusicForegroundService {
  @Override
  protected Class getActivityClass() {
    return MainActivity.class;
  }
}
