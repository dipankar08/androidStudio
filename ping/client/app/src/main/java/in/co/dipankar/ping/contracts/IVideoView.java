package in.co.dipankar.ping.contracts;

import org.webrtc.SurfaceViewRenderer;

/**
 * Created by dip on 3/10/18.
 */

public interface IVideoView {
    SurfaceViewRenderer getSelfVideoView();

    SurfaceViewRenderer getPeerVideoView();
}
