package in.co.dipankar.ping.common.webrtc.fork.cameraCapture;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat.FramerateRange;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.Logging;

public class Camera1Enumerator implements CameraEnumerator {
  private static final String TAG = "Camera1Enumerator";
  private static List<List<CaptureFormat>> cachedSupportedFormats;
  private final boolean captureToTexture;

  public Camera1Enumerator() {
    this(true);
  }

  public Camera1Enumerator(boolean captureToTexture) {
    this.captureToTexture = captureToTexture;
  }

  public String[] getDeviceNames() {
    ArrayList<String> namesList = new ArrayList();

    for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
      String name = getDeviceName(i);
      if (name != null) {
        namesList.add(name);
        Logging.d("Camera1Enumerator", "Index: " + i + ". " + name);
      } else {
        Logging.e("Camera1Enumerator", "Index: " + i + ". Failed to query camera name.");
      }
    }

    String[] namesArray = new String[namesList.size()];
    return (String[]) namesList.toArray(namesArray);
  }

  public boolean isFrontFacing(String deviceName) {
    CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
    return info != null && info.facing == 1;
  }

  public boolean isBackFacing(String deviceName) {
    CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
    return info != null && info.facing == 0;
  }

  public List<CaptureFormat> getSupportedFormats(String deviceName) {
    return getSupportedFormats(getCameraIndex(deviceName));
  }

  public CameraVideoCapturer createCapturer(String deviceName, CameraEventsHandler eventsHandler) {
    return new Camera1Capturer(deviceName, eventsHandler, this.captureToTexture);
  }

  @Nullable
  private static CameraInfo getCameraInfo(int index) {
    CameraInfo info = new CameraInfo();

    try {
      Camera.getCameraInfo(index, info);
      return info;
    } catch (Exception var3) {
      Logging.e("Camera1Enumerator", "getCameraInfo failed on index " + index, var3);
      return null;
    }
  }

  static synchronized List<CaptureFormat> getSupportedFormats(int cameraId) {
    if (cachedSupportedFormats == null) {
      cachedSupportedFormats = new ArrayList();

      for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
        cachedSupportedFormats.add(enumerateFormats(i));
      }
    }

    return (List) cachedSupportedFormats.get(cameraId);
  }

  private static List<CaptureFormat> enumerateFormats(int cameraId) {
    Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + ".");
    long startTimeMs = SystemClock.elapsedRealtime();
    Camera camera = null;

    Parameters parameters;
    label94:
    {
      ArrayList var6;
      try {
        Logging.d("Camera1Enumerator", "Opening camera with index " + cameraId);
        camera = Camera.open(cameraId);
        parameters = camera.getParameters();
        break label94;
      } catch (RuntimeException var15) {
        Logging.e("Camera1Enumerator", "Open camera failed on camera index " + cameraId, var15);
        var6 = new ArrayList();
      } finally {
        if (camera != null) {
          camera.release();
        }
      }

      return var6;
    }

    ArrayList formatList = new ArrayList();

    try {
      int minFps = 0;
      int maxFps = 0;
      List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
      if (listFpsRange != null) {
        int[] range = (int[]) listFpsRange.get(listFpsRange.size() - 1);
        minFps = range[0];
        maxFps = range[1];
      }

      Iterator var19 = parameters.getSupportedPreviewSizes().iterator();

      while (var19.hasNext()) {
        Size size = (Size) var19.next();
        formatList.add(new CaptureFormat(size.width, size.height, minFps, maxFps));
      }
    } catch (Exception var14) {
      Logging.e(
          "Camera1Enumerator", "getSupportedFormats() failed on camera index " + cameraId, var14);
    }

    long endTimeMs = SystemClock.elapsedRealtime();
    Logging.d(
        "Camera1Enumerator",
        "Get supported formats for camera index "
            + cameraId
            + " done. Time spent: "
            + (endTimeMs - startTimeMs)
            + " ms.");
    return formatList;
  }

  static List<org.webrtc.Size> convertSizes(List<Size> cameraSizes) {
    List<org.webrtc.Size> sizes = new ArrayList();
    Iterator var2 = cameraSizes.iterator();

    while (var2.hasNext()) {
      Size size = (Size) var2.next();
      sizes.add(new org.webrtc.Size(size.width, size.height));
    }

    return sizes;
  }

  static List<FramerateRange> convertFramerates(List<int[]> arrayRanges) {
    List<FramerateRange> ranges = new ArrayList();
    Iterator var2 = arrayRanges.iterator();

    while (var2.hasNext()) {
      int[] range = (int[]) var2.next();
      ranges.add(new FramerateRange(range[0], range[1]));
    }

    return ranges;
  }

  static int getCameraIndex(String deviceName) {
    Logging.d("Camera1Enumerator", "getCameraIndex: " + deviceName);

    for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
      if (deviceName.equals(getDeviceName(i))) {
        return i;
      }
    }

    throw new IllegalArgumentException("No such camera: " + deviceName);
  }

  @Nullable
  static String getDeviceName(int index) {
    CameraInfo info = getCameraInfo(index);
    if (info == null) {
      return null;
    } else {
      String facing = info.facing == 1 ? "front" : "back";
      return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
    }
  }
}
