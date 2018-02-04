package com.example.dip.sweepingeffect2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.min;

public class RemoteButtonView extends View {
    public enum Direction {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT,
    };

    public enum Layout {
        VERTICAL,
        HORIZONTAL,
    };

    public interface TouchShapeViewListener {
        void onClick(RemoteButtonView.Direction direction);

        void onHoverIn(RemoteButtonView.Direction direction);

        void onHoverOut(RemoteButtonView.Direction direction);
    }

    private RectF rect;
    static final String TAG = "DIPANKAR";
    private float centerX, centerY;
    private int mRadius, mRadiousOuter, mRadiusInner, strockSize;
    private Direction mDirection = Direction.NONE;
    private Direction prevDirection = Direction.NONE;
    private Layout mLayout = Layout.HORIZONTAL;
    private Paint paint1, paint2, paint3;
    private TextPaint textPaint1, textPaint2;
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
    public void setRadius(int radiusOuter, int radiousInner){
        mRadiousOuter = radiusOuter;
        mRadiusInner = radiousInner;
    }

    void init() {
        rect = new RectF();

        // note that height == width == 2* outerradious,
        mRadiousOuter = 150;
        mRadiusInner = 60;

        mRadius = mRadiusInner +(mRadiousOuter - mRadiusInner )/2;
        strockSize = (mRadiousOuter - mRadiusInner );


        paint1 = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();

        paint1.setColor(Color.parseColor("#5f6673"));
        paint1.setStrokeWidth(strockSize);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);

        paint2.setColor(Color.parseColor("#afb3b9"));
        paint2.setStrokeWidth(strockSize);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);

        paint3.setColor(Color.parseColor("#ffffff"));
        paint3.setStrokeWidth(5);
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.STROKE);

        textPaint1 = new TextPaint();
        textPaint1.setARGB(200, 254, 0, 0);
        textPaint1.setColor(Color.parseColor("#c5c8cb"));
        textPaint1.setTextAlign(Paint.Align.CENTER);
        textPaint1.setTextSize(45);

        textPaint2 = new TextPaint();
        textPaint2.setARGB(200, 254, 0, 0);
        textPaint2.setColor(Color.parseColor("#ffffff"));
        textPaint2.setTextAlign(Paint.Align.CENTER);
        textPaint2.setTextSize(25);
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
        canvas.drawArc(rect, 0, 360, false, paint1);
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
        canvas.drawLine(centerX - mRadius -15 , centerY,centerX - mRadius + 15, centerY,paint3);
        canvas.drawLine(centerX + mRadius -15 , centerY,centerX + mRadius + 15, centerY,paint3);
        canvas.drawLine(centerX , centerY- mRadius -15,centerX, centerY- mRadius + 15,paint3);
        canvas.drawLine(centerX , centerY + mRadius -15,centerX, centerY + mRadius + 15,paint3);

        if(mLayout == Layout.HORIZONTAL){
            canvas.drawText("MOVE",canvas.getWidth() / 2,canvas.getHeight() / 2 + 25/2,textPaint2);
        } else{
            canvas.drawText("MOVE",canvas.getWidth() / 2,canvas.getHeight() / 2 + 25/2,textPaint2);
        }
    }

    void updateTouchDirection(MotionEvent event) {
        // Log.d("DIpankar",event.getX()+"::"+event.getY());
        Direction direction = Direction.NONE;
        double distance = Math.hypot(event.getX() - centerX, event.getY() - centerY);
        if (distance <mRadiousOuter && distance > mRadiusInner) {
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
            if (mDirection != Direction.NONE) {
                if(mTouchShapeViewListener != null) {
                    if (checkEnabledForDirection(mDirection)) {
                        mTouchShapeViewListener.onClick(mDirection);
                        mTouchShapeViewListener.onHoverOut(mDirection);
                    }
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
