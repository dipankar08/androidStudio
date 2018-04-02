package in.co.dipankar.ping.common.webrtc.fork.effect;

/**
 * Created by dip on 4/2/18.
 */
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraVideoCapturer;

public class EffectCamera1Enumerator extends Camera1Enumerator {

    private RTCVideoEffector videoEffector;

    public EffectCamera1Enumerator(RTCVideoEffector effector) {
        super();
        videoEffector = effector;
    }

    @Override
    public CameraVideoCapturer createCapturer(
            String deviceName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        return new EffectCamera1Capturer(deviceName, eventsHandler, videoEffector);
    }
}
