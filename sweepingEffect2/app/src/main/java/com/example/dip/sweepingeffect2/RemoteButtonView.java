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

import static java.lang.Math.PI;
import static java.lang.Math.atan2;

public class RemoteButtonView extends View {
    public enum Direction {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT,
    };

    public interface TouchShapeViewListener {
        void onClick(RemoteButtonView.Direction direction);

        void onHoverIn(RemoteButtonView.Direction direction);

        void onHoverOut(RemoteButtonView.Direction direction);
    }

    private RectF rect;
    static final String TAG = "DIPANKAR";
    private float centerX, centerY;
    private int mRadius, mRadiusInner, strockSize;
    private Direction mDirection = Direction.NONE;
    private Direction prevDirection = Direction.NONE;
    private Paint paint1, paint2;
    @Nullable
    private TouchShapeViewListener mTouchShapeViewListener;

    private boolean mIsEnableUpDown = true;
    private boolean mIsEnableLeftRight = true;
    // you must implement these constructors!!
    public RemoteButtonView(Context c) {
        super(c);
        init();
    }

    public RemoteButtonView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    public void setTouchShapeViewListener(TouchShapeViewListener touchShapeViewListener) {
        mTouchShapeViewListener = touchShapeViewListener;
    }

    void init() {
        rect = new RectF();
        mRadius = 130;
        strockSize = mRadius * 2 / 3;
        mRadiusInner = mRadius - strockSize;
        paint1 = new Paint();
        paint2 = new Paint();

        paint1.setColor(Color.parseColor("#5f6673"));
        paint1.setStrokeWidth(strockSize);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);

        paint2.setColor(Color.parseColor("#afb3b9"));
        paint2.setStrokeWidth(strockSize);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
    }

    // This method is called when the View is displayed
    protected void onDraw(Canvas canvas) {
        rect.set(
                getWidth() / 2 - mRadius,
                getHeight() / 2 - mRadius,
                getWidth() / 2 + mRadius,
                getHeight() / 2 + mRadius);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        drawBack(canvas);
        drawLabel(canvas);
    }

    void drawBack(Canvas canvas) {
        if (mDirection == Direction.RIGHT && checkEnabledForDirection(mDirection)) {
            canvas.drawArc(rect, -45, 90, false, paint2);
        } else {
            canvas.drawArc(rect, -45, 90, false, paint1);
        }
        if (mDirection == Direction.DOWN && checkEnabledForDirection(mDirection)) {
            canvas.drawArc(rect, 45, 90, false, paint2);
        } else {
            canvas.drawArc(rect, 45, 90, false, paint1);
        }
        if (mDirection == Direction.LEFT && checkEnabledForDirection(mDirection)) {
            canvas.drawArc(rect, 135, 90, false, paint2);
        } else {
            canvas.drawArc(rect, 135, 90, false, paint1);
        }
        if (mDirection == Direction.UP && checkEnabledForDirection(mDirection)) {
            canvas.drawArc(rect, 225, 90, false, paint2);
        } else {
            canvas.drawArc(rect, 225, 90, false, paint1);
        }
    }

    void drawLabel(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setARGB(200, 254, 0, 0);
        textPaint.setColor(Color.parseColor("#c5c8cb"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(45);
        canvas.drawText(
                "+",
                canvas.getWidth() / 2,
                canvas.getHeight() / 2 - mRadius + strockSize / 2 - 20,
                textPaint);
        canvas.drawText(
                "-",
                canvas.getWidth() / 2,
                canvas.getHeight() / 2 + mRadius + strockSize / 2 - 20,
                textPaint);
        canvas.drawText("-", canvas.getWidth() / 2 - mRadius, canvas.getHeight() / 2 + 20, textPaint);
        canvas.drawText("+", canvas.getWidth() / 2 + mRadius, canvas.getHeight() / 2 + 20, textPaint);
    }

    void updateTouchDirection(MotionEvent event) {
        // Log.d("DIpankar",event.getX()+"::"+event.getY());
        Direction direction = Direction.NONE;
        double distance = Math.hypot(event.getX() - centerX, event.getY() - centerY);
        if (distance <= (mRadius + strockSize) && distance > mRadiusInner) {
            double angle =
                    atan2(event.getY() - getHeight() / 2, event.getX() - getWidth() / 2) * 180 / PI;
            angle = (angle + 720) % 360;
            if (angle >= 45 && angle < 135) {
                direction = Direction.DOWN;
            } else if (angle >= 135 && angle < 225) {
                direction = Direction.LEFT;
            } else if (angle >= 225 && angle < 315) {
                direction = Direction.UP;
            } else {
                direction = Direction.RIGHT;
            }
        }
        mDirection = direction;
    }

    private boolean checkEnabledForDirection(Direction direction) {
        if ((direction == Direction.LEFT || direction == Direction.RIGHT)
                && mIsEnableLeftRight == false) {
            return false;
        }
        if ((direction == Direction.UP || direction == Direction.DOWN) && mIsEnableUpDown == false) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        updateTouchDirection(event);

        if (prevDirection != mDirection) {
            if (prevDirection != Direction.NONE
                    && mTouchShapeViewListener != null
                    && checkEnabledForDirection(prevDirection)) {
                mTouchShapeViewListener.onHoverOut(prevDirection);
            }
            if (mDirection != Direction.NONE
                    && mTouchShapeViewListener != null
                    && checkEnabledForDirection(mDirection)) {
                mTouchShapeViewListener.onHoverIn(mDirection);
            }
            prevDirection = mDirection;
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mDirection != Direction.NONE && mTouchShapeViewListener != null) {
                if (checkEnabledForDirection(mDirection)) {
                    mTouchShapeViewListener.onClick(mDirection);
                    mTouchShapeViewListener.onHoverOut(mDirection);
                }
                mDirection = prevDirection = Direction.NONE;
            }
            invalidate();
        }
        return true;
    }

    public void setEnableUpDownOnly() {
        mIsEnableUpDown = true;
        mIsEnableLeftRight = false;
        invalidate();
    }

    public void setEnableLeftRightOnly() {
        mIsEnableUpDown = false;
        mIsEnableLeftRight = true;
        invalidate();
    }

    public void setEnableAllControl() {
        mIsEnableUpDown = true;
        mIsEnableLeftRight = true;
        invalidate();
    }
}
