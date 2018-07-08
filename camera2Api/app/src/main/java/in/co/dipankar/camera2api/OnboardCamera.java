package in.co.dipankar.camera2api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class OnboardCamera {
  private final String TAG = "OnboardCamera";
  private Context mContext;

  int mWidth = 1280;
  int mHeight = 720;
  int mYSize = mWidth * mHeight;
  int mUVSize = mYSize / 4;
  int mFrameSize = mYSize + (mUVSize * 2);

  // handler for the camera
  private HandlerThread mCameraHandlerThread;
  private Handler mCameraHandler;

  // the size of the ImageReader determines the output from the camera.
  private ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, ImageFormat.YV12, 30);

  private Surface mCameraRecieverSurface = mImageReader.getSurface();

  void OnboardCamera() {
    mImageReader.setOnImageAvailableListener(mImageAvailListener, mCameraHandler);
  }

  private byte[] tempYbuffer = new byte[mYSize];
  private byte[] tempUbuffer = new byte[mUVSize];
  private byte[] tempVbuffer = new byte[mUVSize];

  ImageReader.OnImageAvailableListener mImageAvailListener =
      new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
          // when a buffer is available from the camera
          // get the image
          Image image = reader.acquireNextImage();
          Image.Plane[] planes = image.getPlanes();

          // copy it into a byte[]
          byte[] outFrame = new byte[mFrameSize];
          int outFrameNextIndex = 0;

          ByteBuffer sourceBuffer = planes[0].getBuffer();
          sourceBuffer.get(tempYbuffer, 0, tempYbuffer.length);

          ByteBuffer vByteBuf = planes[1].getBuffer();
          vByteBuf.get(tempVbuffer);

          ByteBuffer yByteBuf = planes[2].getBuffer();
          yByteBuf.get(tempUbuffer);

          // free the Image
          image.close();
        }
      };

  private OnboardCamera() {
    mCameraHandlerThread = new HandlerThread("mCameraHandlerThread");
    mCameraHandlerThread.start();
    mCameraHandler = new Handler(mCameraHandlerThread.getLooper());
  }

  private final CameraDevice.StateCallback mDeviceStateCallback =
      new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
          // make list of surfaces to give to camera
          List<Surface> surfaceList = new ArrayList<>();
          surfaceList.add(mCameraRecieverSurface);

          try {
            camera.createCaptureSession(surfaceList, mCaptureSessionStateCallback, mCameraHandler);
          } catch (CameraAccessException e) {
            Log.e(TAG, "createCaptureSession threw CameraAccessException.", e);
          }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {}

        @Override
        public void onError(CameraDevice camera, int error) {}
      };

  private final CameraCaptureSession.StateCallback mCaptureSessionStateCallback =
      new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
          try {
            CaptureRequest.Builder requestBuilder =
                session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            requestBuilder.addTarget(mCameraRecieverSurface);
            // set to null - image data will be produced but will not receive metadata
            session.setRepeatingRequest(requestBuilder.build(), null, mCameraHandler);

          } catch (CameraAccessException e) {
            Log.e(TAG, "createCaptureSession threw CameraAccessException.", e);
          }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {}
      };

  @SuppressLint("MissingPermission")
  public boolean startProducing() {
    CameraManager cm = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    try {
      String[] cameraList = cm.getCameraIdList();
      for (String cd : cameraList) {
        // get camera characteristics
        CameraCharacteristics mCameraCharacteristics = cm.getCameraCharacteristics(cd);
        // check if the camera is in the back - if not, continue to next
        if (mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
            != CameraCharacteristics.LENS_FACING_BACK) {
          continue;
        }
        // get StreamConfigurationMap - supported image formats
        StreamConfigurationMap scm =
            mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        android.util.Size[] sizes = scm.getOutputSizes(ImageFormat.YV12);
        cm.openCamera(cd, mDeviceStateCallback, mCameraHandler);
      }

    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "CameraAccessException detected", e);
    }
    return false;
  }
}
