package in.co.dipankar.coolui.views;

/**
 * http://www.seas.upenn.edu/~cdmurphy/cis350/spring2012/android/shapes.html
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static in.co.dipankar.coolui.views.ZoomButtomView.Direction.DOWN;
import static in.co.dipankar.coolui.views.ZoomButtomView.Direction.NONE;
import static in.co.dipankar.coolui.views.ZoomButtomView.Direction.UP;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;

public class ZoomButtomView extends View{
    public enum Direction{
        NONE,
        UP,
        DOWN,
    };
    public interface ZoomButtomViewListener {
        void onClick(ZoomButtomView.Direction direction);
        void onHoverIn(ZoomButtomView.Direction direction);
        void onHoverOut(ZoomButtomView.Direction direction);
    }

    private RectF rect1, rect2, rect3, rect4;
    final static String TAG ="DIPANKAR";
    private float centerX, centerY,mWidth,mHeight;
    private int mRadius,mRadiusInner, strockSize;
    private Direction mDirection = NONE ;
    private Direction prevDirection = NONE ;
    private Paint paint1, paint2;
    @Nullable  private ZoomButtomViewListener mZoomButtomViewListener;
    // you must implement these constructors!!
    public ZoomButtomView(Context c) {
        super(c);
        init();
    }
    public ZoomButtomView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    public void setZoomButtomViewListener(ZoomButtomViewListener ZoomButtomViewListener) {
        mZoomButtomViewListener = ZoomButtomViewListener;
    }

    void init(){
        rect1 = new RectF();
        rect2 = new RectF();
        rect3 = new RectF();
        rect4 = new RectF();
        mWidth =110;
        mHeight=300;

        paint1 = new Paint();
        paint2 = new Paint();

        paint1.setColor(Color.parseColor("#999999"));
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);

        paint2.setColor(Color.parseColor("#777777"));
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAntiAlias(true);
    }

    // This method is called when the View is displayed
    protected void onDraw(Canvas canvas) {
        centerX = getWidth()/2;
        centerY = getHeight()/2;

        rect1.set(centerX - mWidth / 2, centerY - mHeight/2 + mWidth/2, centerX + mWidth / 2, centerY);
        rect2.set(centerX - mWidth / 2, centerY - mHeight/2, centerX + mWidth / 2, centerY-mHeight/2+mWidth);
        rect3.set(centerX - mWidth / 2, centerY, centerX + mWidth / 2, centerY + mHeight / 2 - mWidth/2);
        rect4.set(centerX - mWidth / 2, centerY + mHeight/2 - mWidth, centerX + mWidth / 2, centerY+mHeight/2);

        drawUp(canvas);
        drawDown(canvas);
        drawLabel(canvas);
    }

    void drawUp(Canvas canvas) {
        if(mDirection == UP) {
            canvas.drawRect(rect1, paint2);
            canvas.drawOval(rect2, paint2);
        } else {
            canvas.drawRect(rect1, paint1);
            canvas.drawOval(rect2, paint1);
        }
    }

    void drawDown(Canvas canvas) {
        if(mDirection == DOWN) {
            canvas.drawRect(rect3, paint2);
            canvas.drawOval(rect4, paint2);
        } else{
            canvas.drawRect(rect3, paint1);
            canvas.drawOval(rect4, paint1);
        }
    }

    void drawLabel(Canvas canvas){
        Paint textPaint = new Paint();
        textPaint.setARGB(200, 254, 0, 0);
        textPaint.setColor(Color.WHITE );
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);
        canvas.drawText("+", canvas.getWidth()/2, canvas.getHeight()/2 + mHeight/2 - 50  , textPaint);
        canvas.drawText("_", canvas.getWidth()/2, canvas.getHeight()/2 - mHeight/2 + 50  , textPaint);
  }

  void updateTouchDirection( MotionEvent event){
        Direction direction = NONE;
        if (rect1.contains(event.getX(),event.getY()) || rect2.contains(event.getX(),event.getY())) {
            direction = UP;
        }else if (rect3.contains(event.getX(),event.getY())|| rect4.contains(event.getX(),event.getY())) {
            direction = DOWN;
        }
        mDirection = direction;
    }

    @Override
    public boolean onTouchEvent( MotionEvent event)
    {
        updateTouchDirection(event);

        if(prevDirection != mDirection){
            if(prevDirection!= NONE && mZoomButtomViewListener != null){
                mZoomButtomViewListener.onHoverOut(prevDirection);
            }
            if(mDirection!= NONE && mZoomButtomViewListener != null){
                mZoomButtomViewListener.onHoverIn(mDirection);
            }
            prevDirection = mDirection;
            invalidate();
        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            if(mDirection != NONE){
                if(mZoomButtomViewListener != null){
                    mZoomButtomViewListener.onClick(mDirection);
                    mZoomButtomViewListener.onHoverOut(mDirection);
                }
                mDirection = prevDirection= NONE;
                invalidate();
            }
        }
        return true;
    }

}