package in.co.dipankar.coolui.views;


        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.graphics.drawable.Drawable;
        import android.os.Build;
        import android.renderscript.Allocation;
        import android.renderscript.Element;
        import android.renderscript.RSRuntimeException;
        import android.renderscript.RenderScript;
        import android.renderscript.ScriptIntrinsicBlur;
        import android.support.v7.widget.AppCompatImageView;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.widget.ImageView;

public class BluredImageView extends AppCompatImageView {

    public static final String TAG = "BlurImageView";

    private int radius = 25;
    private int lastRadius = 25;

    private Bitmap originalBitmap;
    private Bitmap blurBitmap;

    private Paint blurPaint = null;
    private Paint originalPaint = null;
    private Matrix resizeMatrix = null;


    public BluredImageView(Context context) {
        super(context);
    }

    public BluredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BluredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        refresh();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        refresh();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        refresh();
    }

    /**
     * clear last settings for new bitmap
     */
    public void refresh() {
        radius = 25;
        lastRadius = 25;

        originalBitmap = null;
        blurBitmap = null;

        blurPaint = null;
        originalPaint = null;
        resizeMatrix = null;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }

        if (blurPaint == null) {
            originalBitmap = Bitmap.createBitmap(
                    getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            Canvas originalCanvas = new Canvas(originalBitmap);
            super.onDraw(originalCanvas);    // get originalBitmap bitmap from parent ImageView class

            blurPaint = new Paint();
            originalPaint = new Paint();
            originalPaint.setFilterBitmap(true);
            resizeMatrix = new Matrix();
        }

        if (blurBitmap == null || radius != lastRadius) {
            blurBitmap = blur(originalBitmap, radius);
        }

        canvas.drawBitmap(blurBitmap, resizeMatrix, blurPaint);
        canvas.drawBitmap(originalBitmap, resizeMatrix, originalPaint);
    }

    /**
     * Generate blurred image using RenderScript or alternative implementation (if RS is not supported)
     * @param original originalBitmap bitmap
     * @param radius    blur level
     * @return blurred bitmap
     */
    private Bitmap blur(Bitmap original, int radius) {
        if (radius == 0) {
            return original;
        }

        Bitmap bitmap = null;

        boolean noRsSupport = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bitmap = Bitmap.createBitmap(
                    original.getWidth(), original.getHeight(),
                    Bitmap.Config.ARGB_8888);

            try {
                RenderScript rs = RenderScript.create(getContext());

                Allocation allocIn = Allocation.createFromBitmap(rs, original);
                Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

                ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                        rs, Element.U8_4(rs));
                blur.setInput(allocIn);
                blur.setRadius(radius);
                blur.forEach(allocOut);

                allocOut.copyTo(bitmap);

                rs.destroy();

                noRsSupport = false;
            } catch (RSRuntimeException e) {
                Log.e(TAG, "RenderScript runtime exception. Probably GPU is not supported" + e);
            }
        }

        if (noRsSupport) {
            bitmap = noRsBlur(original, radius);
        }

        lastRadius = radius;
        blurBitmap = bitmap;

        return blurBitmap;
    }

    /**
     * Alternative implementation of blurred image generation
     * @param sentBitmap originalBitmap bitmap
     * @param radius    blur level
     * @return blurred bitmap
     */
    private Bitmap noRsBlur(Bitmap sentBitmap, int radius) {

// Stack Blur v1.0 from
// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
//
// Java Author: Mario Klingemann <mario at quasimondo.com>
// http://incubator.quasimondo.com
// created Feburary 29, 2004
// Android port : Yahel Bouaziz <yahel at kayenko.com>
// http://www.kayenko.com
// ported april 5th, 2012
// This is a compromise between Gaussian Blur and Box blur
// It creates much better looking blurs than Box Blur, but is
// 7x faster than my Gaussian Blur implementation.
//
// I called it Stack Blur because this describes best how this
// filter works internally: it creates a kind of moving stack
// of colors whilst scanning through the image. Thereby it
// just has to add one new block of color to the right side
// of the stack and remove the leftmost color. The remaining
// colors on the topmost layer of the stack are either added on
// or reduced by one, depending on if they are on the right or
// on the left side of the stack.
//
// If you are using this algorithm in your code please add
// the following line:
//
// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
// Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * Set blur level.
     * Do not use this method repeatedly on user generated events (like inside onScrollChanged) because this method is heavy (causes new bitmap generation).
     * @param radius value from 0 (sharp image) to 25 (max. blurred image)
     */
    public void setRadius(int radius) {
        this.radius = Math.max(Math.min(radius, 25), 0);

        invalidate();
    }

    /**
     * Use this function to smoothly change from sharp image to blurred image.
     * Can be used for example inside onScrollChanged or similar
     * @param focus value from 0 (sharpest image) to 1 (max. blurred image)
     */
    public void setFocus(float focus) {

        float validFocus = Math.max(Math.min(focus, 1.0f), 0);

        if (originalPaint != null) {
            originalPaint.setAlpha((int) (255-Math.min(255, (2*255*validFocus))));
        }

        if (resizeMatrix != null) {
            resizeMatrix.reset();
            resizeMatrix.postScale(1.0f+validFocus/10.0f, 1.0f+validFocus/10.0f);
            resizeMatrix.postTranslate(-0.5f * getWidth() * validFocus / 10.0f, -0.5f * getHeight() * validFocus / 10.0f);
        }

        invalidate();
    }
}