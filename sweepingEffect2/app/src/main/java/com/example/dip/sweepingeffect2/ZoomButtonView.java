package com.example.dip.sweepingeffect2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomButtonView extends View {
    public enum Direction {
        NONE,
        UP,
        DOWN,
    };

    public enum Layout {
        VERTICAL,
        HORIZONTAL,
    };

    public interface ZoomButtomViewListener {
        void onClick(ZoomButtonView.Direction direction);

        void onHoverIn(ZoomButtonView.Direction direction);

        void onHoverOut(ZoomButtonView.Direction direction);
    }

    private RectF rect1, rect2, rect3, rect4, rect5;
    static final String TAG = "DIPANKAR";
    private float centerX, centerY, mWidth, mHeight;

    private Direction mDirection = Direction.NONE;
    private Direction prevDirection = Direction.NONE;
    private Layout mLayout = Layout.VERTICAL;
    private Paint paint1, paint2;
    Paint textPaint1, textPaint2;

    @Nullable private ZoomButtomViewListener mZoomButtomViewListener;
    // you must implement these constructors!!
    public ZoomButtonView(Context c) {
        super(c);
        init();
    }

    public ZoomButtonView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    public void setZoomButtomViewListener(ZoomButtomViewListener ZoomButtomViewListener) {
        mZoomButtomViewListener = ZoomButtomViewListener;
    }

    public void setLayout(Layout layout){
        mLayout = layout;
        invalidate();
    }

    void init() {
        rect1 = new RectF();
        rect2 = new RectF();
        rect3 = new RectF();
        rect4 = new RectF();
        rect5 = new RectF();
        mWidth = 100;
        mHeight = 300;

        paint1 = new Paint();
        paint2 = new Paint();

        paint1.setColor(Color.parseColor("#5f6673"));
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);

        paint2.setColor(Color.parseColor("#afb3b9"));
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAntiAlias(true);

        textPaint2 = new Paint();
        textPaint2.setARGB(200, 254, 0, 0);
        textPaint2.setColor(Color.parseColor("#c5c8cb"));
        textPaint2.setTextAlign(Paint.Align.CENTER);
        textPaint2.setTextSize(25);
    }

    public void setHeight(int height){
        mHeight = height;
    }
    public void setWidth(int width){
        mWidth = width;
    }

    // This method is called when the View is displayed
    protected void onDraw(Canvas canvas) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        rect1.set(
                centerX - mWidth / 2, centerY - mHeight / 2 + mWidth / 2, centerX + mWidth / 2, centerY);
        rect2.set(
                centerX - mWidth / 2,
                centerY - mHeight / 2,
                centerX + mWidth / 2,
                centerY - mHeight / 2 + mWidth);
        rect3.set(
                centerX - mWidth / 2, centerY, centerX + mWidth / 2, centerY + mHeight / 2 - mWidth / 2);
        rect4.set(
                centerX - mWidth / 2,
                centerY + mHeight / 2 - mWidth,
                centerX + mWidth / 2,
                centerY + mHeight / 2);

        drawUp(canvas);
        drawDown(canvas);
        drawLabel(canvas);
    }

    void drawUp(Canvas canvas) {
        if (mDirection == Direction.UP) {
            canvas.drawRect(rect1, paint2);
            canvas.drawOval(rect2, paint2);
        } else {
            canvas.drawRect(rect1, paint1);
            canvas.drawOval(rect2, paint1);
        }
    }

    void drawDown(Canvas canvas) {
        if (mDirection == Direction.DOWN) {
            canvas.drawRect(rect3, paint2);
            canvas.drawOval(rect4, paint2);
        } else {
            canvas.drawRect(rect3, paint1);
            canvas.drawOval(rect4, paint1);
        }
    }

    void drawLabel(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setARGB(200, 254, 0, 0);
        textPaint.setColor(Color.parseColor("#c5c8cb"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);
        canvas.drawText(
                "_",
                canvas.getWidth() / 2,
                canvas.getHeight() / 2 + mHeight / 2 - mWidth / 2 - 25,
                textPaint);
        canvas.drawText(
                "+",
                canvas.getWidth() / 2,
                canvas.getHeight() / 2 - mHeight / 2 + mWidth / 2 + 25,
                textPaint);
        if(mLayout == Layout.HORIZONTAL){
            canvas.drawText("ZOOM",canvas.getWidth() / 2,canvas.getHeight() / 2 + 25/2,textPaint2);
        } else{
            canvas.save();
            canvas.rotate(90f, canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.drawText("ZOOM",canvas.getWidth() / 2 , canvas.getHeight() / 2 + 25/2, textPaint2);
            canvas.restore();
        }
    }

    void updateTouchDirection(MotionEvent event) {
        Direction direction = Direction.NONE;
        if (rect1.contains(event.getX(), event.getY()) || rect2.contains(event.getX(), event.getY())) {
            direction = Direction.UP;
        } else if (rect3.contains(event.getX(), event.getY())
                || rect4.contains(event.getX(), event.getY())) {
            direction = Direction.DOWN;
        }
        mDirection = direction;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        updateTouchDirection(event);
        if (prevDirection != mDirection) {
            if (prevDirection != Direction.NONE && mZoomButtomViewListener != null) {
                mZoomButtomViewListener.onHoverOut(prevDirection);
            }
            if (mDirection != Direction.NONE && mZoomButtomViewListener != null) {
                mZoomButtomViewListener.onHoverIn(mDirection);
            }
            prevDirection = mDirection;
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mDirection != Direction.NONE) {
                if (mZoomButtomViewListener != null) {
                    mZoomButtomViewListener.onClick(mDirection);
                    mZoomButtomViewListener.onHoverOut(mDirection);
                }
                mDirection = prevDirection = Direction.NONE;
                invalidate();
            }
        }
        return true;
    }
}
