package in.peerreview.ping.common.webrtc.fork.cameraCapture;

import android.content.Context;
import android.media.MediaRecorder;
import javax.annotation.Nullable;
import org.webrtc.*;

public class Camera1Capturer
    extends in.peerreview.ping.common.webrtc.fork.cameraCapture.CameraCapturer {
  private final boolean captureToTexture;

  public Camera1Capturer(
      String cameraName, CameraEventsHandler eventsHandler, boolean captureToTexture) {
    super(
        cameraName,
        eventsHandler,
        new in.peerreview.ping.common.webrtc.fork.cameraCapture.Camera1Enumerator(
            captureToTexture));
    this.captureToTexture = captureToTexture;
  }

  protected void createCameraSession(
      CameraSession.CreateSessionCallback createSessionCallback,
      CameraSession.Events events,
      Context applicationContext,
      SurfaceTextureHelper surfaceTextureHelper,
      @Nullable MediaRecorder mediaRecorder,
      String cameraName,
      int width,
      int height,
      int framerate) {
    in.peerreview.ping.common.webrtc.fork.cameraCapture.Camera1Session.create(
        createSessionCallback,
        events,
        this.captureToTexture || mediaRecorder != null,
        applicationContext,
        surfaceTextureHelper,
        mediaRecorder,
        in.peerreview.ping.common.webrtc.fork.cameraCapture.Camera1Enumerator.getCameraIndex(
            cameraName),
        width,
        height,
        framerate);
  }
}
