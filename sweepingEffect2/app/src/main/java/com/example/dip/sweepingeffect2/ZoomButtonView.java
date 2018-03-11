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
    private float centerX, centerY, mWidth, mHeight, mHeightUnit;

    private Direction mDirection = Direction.NONE;
    private Direction prevDirection = Direction.NONE;
    private Layout mLayout = Layout.VERTICAL;
    private Paint paint1, paint2, paint3;
    Paint textPaint1;

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

        setButtonSize(440, 150);

        rect1 = new RectF(); //cirlcle up
        rect2 = new RectF(); //sqr up
        rect3 = new RectF(); // middle
        rect4 = new RectF(); //rect down
        rect5 = new RectF(); //circle down


        paint1 = new Paint(); //default
        paint2 = new Paint(); //heighlight
        paint3 = new Paint(); //button

        paint1.setColor(Color.parseColor("#5f6673"));
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);

        paint2.setColor(Color.parseColor("#afb3b9"));
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAntiAlias(true);

        paint3.setColor(Color.parseColor("#ffffff"));
        paint3.setStrokeWidth(5);
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.STROKE);

        textPaint1 = new Paint();
        textPaint1.setARGB(200, 254, 0, 0);
        textPaint1.setColor(Color.parseColor("#c5c8cb"));
        textPaint1.setTextAlign(Paint.Align.CENTER);
        textPaint1.setTextSize(25);
    }

    public void setButtonSize(float height, float width){
        mHeight = height;
        mHeightUnit = height /3;
        mWidth = width;
    }

    // This method is called when the View is displayed
    protected void onDraw(Canvas canvas) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        if(mLayout == Layout.VERTICAL){
            rect3.set(
                    centerX - mWidth / 2, centerY -  mHeightUnit / 2, centerX + mWidth / 2, centerY + mHeightUnit / 2);
            rect2.set(
                    centerX - mWidth / 2, centerY - mHeightUnit, centerX + mWidth / 2, centerY - mHeightUnit / 2);
            rect4.set(
                    centerX - mWidth / 2, centerY + mHeightUnit/2, centerX + mWidth / 2, centerY + mHeightUnit);
            rect1.set(
                    centerX - mWidth / 2, centerY - mHeightUnit*3/2, centerX + mWidth / 2, centerY - mHeightUnit/2);
            rect5.set(
                    centerX - mWidth / 2, centerY + mHeightUnit/2, centerX + mWidth / 2, centerY + mHeightUnit*3/2);
        } else{
            rect3.set(
                    centerX - mHeightUnit / 2, centerY -  mWidth / 2, centerX + mHeightUnit / 2, centerY + mWidth / 2);
            rect2.set(
                    centerX - mHeightUnit, centerY - mWidth/2, centerX - mHeightUnit / 2, centerY + mWidth / 2);
            rect4.set(
                    centerX + mHeightUnit / 2, centerY - mWidth/2, centerX + mHeightUnit, centerY + mWidth / 2);
            rect1.set(
                    centerX - mHeightUnit *3/ 2, centerY - mWidth/2, centerX - mHeightUnit / 2, centerY + mWidth / 2);
            rect5.set(
                    centerX +mHeightUnit / 2, centerY - mWidth/2, centerX + mHeightUnit *3 / 2, centerY + mWidth / 2);

        }

        drawUp(canvas);
        drawDown(canvas);
        drawMiddle(canvas);
        drawLabel(canvas);
        drawIcon(canvas);
    }

    void drawMiddle(Canvas canvas) {
        canvas.drawRect(rect3, paint1);
    }
    void drawUp(Canvas canvas) {
        if (mDirection == Direction.UP) {
            canvas.drawOval(rect1, paint2);
            canvas.drawRect(rect2, paint2);
        } else {
            canvas.drawOval(rect1, paint1);
            canvas.drawRect(rect2, paint1);
        }
    }

    void drawDown(Canvas canvas) {
        if (mDirection == Direction.DOWN) {
            canvas.drawRect(rect4, paint2);
            canvas.drawOval(rect5, paint2);
        } else {
            canvas.drawRect(rect4, paint1);
            canvas.drawOval(rect5, paint1);
        }
    }

    void drawIcon(Canvas canvas){
        if(mLayout == Layout.VERTICAL){
            canvas.drawLine(centerX-15,centerY-mHeightUnit , centerX+15,centerY-mHeightUnit , paint3);
            canvas.drawLine(centerX,centerY-mHeightUnit -15 , centerX,centerY-mHeightUnit+15 , paint3);
            canvas.drawLine(centerX-15,centerY+mHeightUnit , centerX+15,centerY+mHeightUnit , paint3);
        } else{
            canvas.drawLine(centerX - mHeightUnit -15,centerY , centerX - mHeightUnit+15,centerY , paint3);
            canvas.drawLine(centerX + mHeightUnit -15,centerY , centerX + mHeightUnit+15,centerY , paint3);
            canvas.drawLine(centerX + mHeightUnit,centerY-15 , centerX + mHeightUnit,centerY+15 , paint3);
        }
    }

    void drawLabel(Canvas canvas) {
            canvas.drawText("ZOOM",centerX,centerY + 25/2,textPaint1);
    }

    void updateTouchDirection(MotionEvent event) {
        Direction direction = Direction.NONE;
        if (rect1.contains(event.getX(), event.getY()) || rect2.contains(event.getX(), event.getY())) {
            direction = Direction.UP;
        } else if (rect4.contains(event.getX(), event.getY())
                || rect5.contains(event.getX(), event.getY())) {
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
