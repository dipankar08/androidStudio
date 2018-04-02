package in.co.dipankar.ping.common.webrtc.fork.cameraCapture;

import android.content.Context;
import android.media.MediaRecorder;

import org.webrtc.*;



import javax.annotation.Nullable;
import android.content.Context;
import android.media.MediaRecorder;
import javax.annotation.Nullable;
import in.co.dipankar.ping.common.webrtc.fork.cameraCapture.CameraSession.CreateSessionCallback;
import in.co.dipankar.ping.common.webrtc.fork.cameraCapture.CameraSession.Events;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;

public class Camera1Capturer extends CameraCapturer {
    private final boolean captureToTexture;

    public Camera1Capturer(String cameraName, CameraEventsHandler eventsHandler, boolean captureToTexture) {
        super(cameraName, eventsHandler, new Camera1Enumerator(captureToTexture));
        this.captureToTexture = captureToTexture;
    }

    protected void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, @Nullable MediaRecorder mediaRecorder, String cameraName, int width, int height, int framerate) {
       Camera1Session.create(createSessionCallback, events, this.captureToTexture || mediaRecorder != null, applicationContext, surfaceTextureHelper, mediaRecorder, Camera1Enumerator.getCameraIndex(cameraName), width, height, framerate);
    }
}
