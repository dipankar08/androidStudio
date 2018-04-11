package in.peerreview.ping.common.webrtc;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import org.webrtc.SurfaceViewRenderer;

/** Created by dip on 3/24/18. */
public class ExtendedSurfaceView extends SurfaceViewRenderer {
  public ExtendedSurfaceView(Context context) {
    super(context);
    init();
  }

  public ExtendedSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {}
  /*
      @Override
      protected void dispatchDraw(Canvas canvas) {
          Path clipPath = new Path();
          //TODO: define the circle you actually want
          clipPath.addCircle((float) dpToPx(100), (float) dpToPx(100), (float) dpToPx(100), Path.Direction.CW);

          Paint paint = new Paint();
          paint.setAntiAlias(true);
          paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

          canvas.clipPath(clipPath);
          canvas.drawPath(clipPath, paint);
          //this.setZOrderOnTop(true);
          super.dispatchDraw(canvas);
      }
  */
  public int dpToPx(int dp) {
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    return px;
  }
}
