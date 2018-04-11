package in.peerreview.ping.common.webrtc.fork.effect;

import android.content.Context;
import org.webrtc.Camera1Capturer;
import org.webrtc.SurfaceTextureHelper;

public class EffectCamera1Capturer extends Camera1Capturer {
  public static final String TAG = EffectCamera1Capturer.class.getSimpleName();

  private CapturerObserverProxy observer;
  private RTCVideoEffector videoEffector;

  public EffectCamera1Capturer(
      String cameraName, CameraEventsHandler eventsHandler, RTCVideoEffector effector) {
    super(cameraName, eventsHandler, false);
    videoEffector = effector;
  }

  @Override
  public void initialize(
      SurfaceTextureHelper surfaceTextureHelper,
      Context applicationContext,
      CapturerObserver originalObserver) {

    observer = new CapturerObserverProxy(surfaceTextureHelper, originalObserver, videoEffector);
    super.initialize(surfaceTextureHelper, applicationContext, observer);
  }
}
