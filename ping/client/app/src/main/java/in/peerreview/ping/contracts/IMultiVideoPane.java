package in.peerreview.ping.contracts;

import org.webrtc.SurfaceViewRenderer;

/** Created by dip on 3/17/18. */
public interface IMultiVideoPane {
  public SurfaceViewRenderer getSelfView();

  public SurfaceViewRenderer getPeerView();
}
