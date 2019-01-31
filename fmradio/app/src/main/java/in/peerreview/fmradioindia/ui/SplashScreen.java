package in.peerreview.fmradioindia.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;

public class SplashScreen extends ConstraintLayout {
  private TextView mVersion;

  public SplashScreen(Context context) {
    super(context);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.activity_splash, this, true);
    mVersion = findViewById(R.id.version);
    Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
    mVersion.setText(Utils.getVersionString());
  }
}
