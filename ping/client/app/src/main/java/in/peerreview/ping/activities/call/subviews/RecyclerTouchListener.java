package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

  public interface ClickListener {
    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
  }

  private ClickListener mClicklistener;
  private GestureDetector gestureDetector;

  public RecyclerTouchListener(
      Context context, final RecyclerView recycleView, final ClickListener clicklistener) {
    this.mClicklistener = clicklistener;
    gestureDetector =
        new GestureDetector(
            context,
            new GestureDetector.SimpleOnGestureListener() {
              @Override
              public boolean onSingleTapUp(MotionEvent e) {
                return true;
              }

              @Override
              public void onLongPress(MotionEvent e) {
                View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mClicklistener != null) {
                  mClicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                }
              }
            });
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    View child = rv.findChildViewUnder(e.getX(), e.getY());
    if (child != null && mClicklistener != null && gestureDetector.onTouchEvent(e)) {
      mClicklistener.onClick(child, rv.getChildAdapterPosition(child));
    }
    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
