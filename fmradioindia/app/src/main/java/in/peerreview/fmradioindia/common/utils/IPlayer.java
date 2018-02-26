package in.peerreview.fmradioindia.common.utils;

/** Created by dip on 2/14/18. */
public interface IPlayer {
  void play(String title, String url);

  void stop();

  boolean isPlaying();

  void pause();

  void resume();

  void restart();

  void mute();

  void unmute();

  boolean isPaused();
}
