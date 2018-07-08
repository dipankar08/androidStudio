package in.co.dipankar.bengalisuspense.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SampleView1 extends LinearLayout {
  public SampleView1(Context context) {
    super(context);
    init(context);
  }

  public SampleView1(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public SampleView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {

    setBackgroundColor(Color.GRAY);
    setGravity(Gravity.CENTER);
    TextView tv1 = new TextView(context);
    tv1.setText("HELLO DIPANKAR");
    addView(tv1);
  }
}
