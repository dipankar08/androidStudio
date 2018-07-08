package in.co.dipankar.camera2api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Api {
  private static final String TAG = "DIPANKAR";
  Context mContext;
  ICallback mCallback;

  private SurfaceTexture mSurfaceTexture;

  public interface ICallback {
    void handleRawData(byte[] rawdata);
  }

  public Camera2Api(Context context, ICallback callback) {
    mContext = context;
    mCallback = callback;
  }

  // 2. Serface Listner - Ready Open the Camera..
  private TextureView.SurfaceTextureListener mSurfaceTextureListener =
      new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
          setupCamera(width, height);
          connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
          return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
      };

  // Callback1: Called when image avilable
  ImageReader.OnImageAvailableListener mImageAvailable =
      new ImageReader.OnImageAvailableListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onImageAvailable(ImageReader reader) {
          Image image = null;
          try {
            image = reader.acquireLatestImage();
            if (image != null) {
              ByteBuffer buffer = image.getPlanes()[0].getBuffer();
              byte[] bytes = new byte[buffer.capacity()];
              buffer.get(bytes, 0, bytes.length);
              image.close();
              mCallback.handleRawData(bytes);
            }
          } catch (Exception e) {
            Log.w("DIPANKAR --> ", e.getMessage());
            e.printStackTrace();
          }
        }
      };

  // 4. Capturing - Request - seesion and callback.
  ImageReader mImageReader;
  TextureView mSelfTextureView;
  protected CameraCaptureSession mCameraCaptureSessions;
  protected CaptureRequest.Builder mCaptureRequestBuilder;

  protected void updatePreview() {
    if (null == cameraDevice) {
      Log.e(TAG, "updatePreview error, return");
    }
    mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    try {
      mCameraCaptureSessions.setRepeatingRequest(
          mCaptureRequestBuilder.build(), null, mBackgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void startPreview() {
    if (cameraDevice == null) {
      return;
    }

    if (mSelfTextureView != null && !mSelfTextureView.isAvailable()) {
      Log.d(TAG, "mSelfTextureView Not avilable");
    }
    try {
      mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      List surfaces = new ArrayList<>();

      // Surface 1A
      if (mSelfTextureView != null) {
        SurfaceTexture surfaceTexture = mSelfTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        surfaces.add(previewSurface);
        mCaptureRequestBuilder.addTarget(previewSurface);
      }
      // Sarface 1B

      if (mSurfaceTexture != null) {
        Surface previewSurface = new Surface(mSurfaceTexture);
        surfaces.add(previewSurface);
        mCaptureRequestBuilder.addTarget(previewSurface);
      }
      // Surface 2

      if (mImageReader != null) {
        Surface readerSurface = mImageReader.getSurface();
        surfaces.add(readerSurface);
        mCaptureRequestBuilder.addTarget(readerSurface);
      }

      cameraDevice.createCaptureSession(
          surfaces,
          new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
              mCameraCaptureSessions = cameraCaptureSession;
              updatePreview();
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
              Log.w(TAG, "Create capture session failed");
            }
          },
          mBackgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  // 6. Update the review

  // Threads
  private Handler mBackgroundHandler;
  private HandlerThread mBackgroundThread;

  private void startBackgroundThread() {
    mBackgroundThread = new HandlerThread("Camera Background");
    mBackgroundThread.start();
    mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
  }

  private void stopBackgroundThread() {
    mBackgroundThread.quitSafely();
    try {
      mBackgroundThread.join();
      mBackgroundThread = null;
      mBackgroundHandler = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // --------Setup camera ---
  private Size mPreviewSize;
  private int mTotalRotation;
  protected CameraDevice cameraDevice;

  private void setupCamera(int width, int height) {
    CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    try {
      for (String cameraId : cameraManager.getCameraIdList()) {
        CameraCharacteristics cameraCharacteristics =
            cameraManager.getCameraCharacteristics(cameraId);
        if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
            == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }
        StreamConfigurationMap map =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        int deviceOrientation =
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
        boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
        int rotatedWidth = width;
        int rotatedHeight = height;
        if (swapRotation) {
          rotatedWidth = height;
          rotatedHeight = width;
        }
        mPreviewSize =
            chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);

        mImageReader =
            ImageReader.newInstance(
                mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 20);
        mImageReader.setOnImageAvailableListener(mImageAvailable, mBackgroundHandler);

        // mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth,
        // rotatedHeight);
        // mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth,
        // rotatedHeight);
        // mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(),
        // ImageFormat.JPEG, 1);
        // mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

        mCameraId = cameraId;
        return;
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private static SparseIntArray ORIENTATIONS = new SparseIntArray();

  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 0);
    ORIENTATIONS.append(Surface.ROTATION_90, 90);
    ORIENTATIONS.append(Surface.ROTATION_180, 180);
    ORIENTATIONS.append(Surface.ROTATION_270, 270);
  }

  private static int sensorToDeviceRotation(
      CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
    int sensorOrienatation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
    deviceOrientation = ORIENTATIONS.get(deviceOrientation);
    return (sensorOrienatation + deviceOrientation + 360) % 360;
  }

  private static class CompareSizeByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
      return Long.signum(
          (long) (lhs.getWidth() * lhs.getHeight()) - (long) (rhs.getWidth() * rhs.getHeight()));
    }
  }

  private static Size chooseOptimalSize(Size[] choices, int width, int height) {
    List<Size> bigEnough = new ArrayList<Size>();
    for (Size option : choices) {
      if (option.getHeight() == option.getWidth() * height / width
          && option.getWidth() >= width
          && option.getHeight() >= height) {
        bigEnough.add(option);
      }
    }
    if (bigEnough.size() > 0) {
      return Collections.min(bigEnough, new CompareSizeByArea());
    } else {
      return choices[0];
    }
  }

  // Step 2: Connecting Camera ..
  private String mCameraId;
  private CameraDevice.StateCallback mCameraDeviceStateCallback =
      new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
          cameraDevice = camera;
          startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
          camera.close();
          cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
          camera.close();
          cameraDevice = null;
        }
      };

  private void connectCamera() {
    CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(
                (Activity) mContext, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
          cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
        } else {
          Toast.makeText(mContext, "Video app required access to camera", Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  // APIs
  public void startOperation() {
    startBackgroundThread();
    startBackgroundThread();
    if (mSelfTextureView != null) {
      if (mSelfTextureView.isAvailable()) {
        setupCamera(mSelfTextureView.getWidth(), mSelfTextureView.getHeight());
        connectCamera();
      } else {
        mSelfTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
      }
    } else {
      setupCamera(mSelfTextureView.getWidth(), mSelfTextureView.getHeight());
      connectCamera();
    }
  }

  public void addSelfSurfaceTexture(SurfaceTexture surfaceTexture) {
    mSurfaceTexture = surfaceTexture;
  }

  public void addSelfTextureView(TextureView textureView) {
    mSelfTextureView = textureView;
    mSelfTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
  }
}
