package in.co.dipankar.ping.common.signaling;

import org.webrtc.SurfaceViewRenderer;

/**
 * Created by dip on 3/10/18.
 */

public interface IVideoView {
    SurfaceViewRenderer getSelfVideoView();

    SurfaceViewRenderer getPeerVideoView();
}
