package in.peerreview.ping.common.webrtc.fork.screencapture;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjection.Callback;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.view.Surface;
import javax.annotation.Nullable;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceTextureHelper.OnTextureFrameAvailableListener;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturer.CapturerObserver;
import org.webrtc.VideoFrame;
import org.webrtc.VideoFrame.Buffer;

@TargetApi(21)
public class ScreenCapturerAndroid implements VideoCapturer, OnTextureFrameAvailableListener {
  private static final int DISPLAY_FLAGS = 3;
  private static final int VIRTUAL_DISPLAY_DPI = 400;
  private final Intent mediaProjectionPermissionResultData;
  private final Callback mediaProjectionCallback;
  private int width;
  private int height;
  @Nullable private VirtualDisplay virtualDisplay;
  @Nullable private SurfaceTextureHelper surfaceTextureHelper;
  @Nullable private CapturerObserver capturerObserver;
  private long numCapturedFrames = 0L;
  @Nullable private MediaProjection mediaProjection;
  private boolean isDisposed = false;
  @Nullable private MediaProjectionManager mediaProjectionManager;

  public ScreenCapturerAndroid(
      Intent mediaProjectionPermissionResultData, Callback mediaProjectionCallback) {
    this.mediaProjectionPermissionResultData = mediaProjectionPermissionResultData;
    this.mediaProjectionCallback = mediaProjectionCallback;
  }

  private void checkNotDisposed() {
    if (this.isDisposed) {
      throw new RuntimeException("capturer is disposed.");
    }
  }

  public synchronized void initialize(
      SurfaceTextureHelper surfaceTextureHelper,
      Context applicationContext,
      CapturerObserver capturerObserver) {
    this.checkNotDisposed();
    if (capturerObserver == null) {
      throw new RuntimeException("capturerObserver not set.");
    } else {
      this.capturerObserver = capturerObserver;
      if (surfaceTextureHelper == null) {
        throw new RuntimeException("surfaceTextureHelper not set.");
      } else {
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.mediaProjectionManager =
            (MediaProjectionManager) applicationContext.getSystemService("media_projection");
      }
    }
  }

  public synchronized void startCapture(int width, int height, int ignoredFramerate) {
    this.checkNotDisposed();
    this.width = width;
    this.height = height;
    if (mediaProjectionManager != null) {
      this.mediaProjection =
          this.mediaProjectionManager.getMediaProjection(
              -1, this.mediaProjectionPermissionResultData);
      this.mediaProjection.registerCallback(
          this.mediaProjectionCallback, this.surfaceTextureHelper.getHandler());
    }
    this.createVirtualDisplay();
    this.capturerObserver.onCapturerStarted(true);
    this.surfaceTextureHelper.startListening(this);
  }

  public synchronized void stopCapture() {
    this.checkNotDisposed();
    ThreadUtils.invokeAtFrontUninterruptibly(
        this.surfaceTextureHelper.getHandler(),
        new Runnable() {
          public void run() {
            surfaceTextureHelper.stopListening();
            capturerObserver.onCapturerStopped();
            if (virtualDisplay != null) {
              virtualDisplay.release();
              virtualDisplay = null;
            }

            if (mediaProjection != null) {
              mediaProjection.unregisterCallback(mediaProjectionCallback);
              mediaProjection.stop();
              mediaProjection = null;
            }
          }
        });
  }

  public synchronized void dispose() {
    this.isDisposed = true;
  }

  public synchronized void changeCaptureFormat(int width, int height, int ignoredFramerate) {
    this.checkNotDisposed();
    this.width = width;
    this.height = height;
    if (this.virtualDisplay != null) {
      ThreadUtils.invokeAtFrontUninterruptibly(
          this.surfaceTextureHelper.getHandler(),
          new Runnable() {
            public void run() {
              virtualDisplay.release();
              createVirtualDisplay();
            }
          });
    }
  }

  private void createVirtualDisplay() {
    this.surfaceTextureHelper.getSurfaceTexture().setDefaultBufferSize(this.width, this.height);
    this.virtualDisplay =
        this.mediaProjection.createVirtualDisplay(
            "WebRTC_ScreenCapture",
            this.width,
            this.height,
            400,
            3,
            new Surface(this.surfaceTextureHelper.getSurfaceTexture()),
            (android.hardware.display.VirtualDisplay.Callback) null,
            (Handler) null);
  }

  public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
    ++this.numCapturedFrames;
    Buffer buffer =
        this.surfaceTextureHelper.createTextureBuffer(
            this.width,
            this.height,
            RendererCommon.convertMatrixToAndroidGraphicsMatrix(transformMatrix));
    VideoFrame frame = new VideoFrame(buffer, 0, timestampNs);
    this.capturerObserver.onFrameCaptured(frame);
    frame.release();
  }

  public boolean isScreencast() {
    return true;
  }

  public long getNumCapturedFrames() {
    return this.numCapturedFrames;
  }
}
