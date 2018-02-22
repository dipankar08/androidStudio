package in.co.dipankar.coolui.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import in.co.dipankar.coolui.R;
import in.co.dipankar.coolui.views.RemoteButtonView;
import in.co.dipankar.coolui.views.ZoomButtomView;

public class MainActivity extends AppCompatActivity {
  private RemoteButtonView remoteButtonView;
  private ZoomButtomView zoomButtomView;
  private ImageButton imageButton;
  private LinearLayout expandView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.touch_shape_view);

    imageButton = findViewById(R.id.hide);
    expandView = findViewById(R.id.expand_view);
    remoteButtonView = findViewById(R.id.touchshapeview);
    zoomButtomView = findViewById(R.id.zoom_btn);

    remoteButtonView.setTouchShapeViewListener(
        new RemoteButtonView.TouchShapeViewListener() {
          @Override
          public void onClick(RemoteButtonView.Direction direction) {
            Log.d("DIPANKAR", "onclick:" + direction.name());
          }

          @Override
          public void onHoverIn(RemoteButtonView.Direction direction) {
            Log.d("DIPANKAR", "onHoverIn:" + direction.name());
          }

          @Override
          public void onHoverOut(RemoteButtonView.Direction direction) {
            Log.d("DIPANKAR", "onHoverOut:" + direction.name());
          }
        });

    imageButton.setOnClickListener(
        new View.OnClickListener() {
          @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
          @Override
          public void onClick(View v) {
            if (expandView.getVisibility() == View.GONE) {

              expandView.setVisibility(View.VISIBLE);
              imageButton.setBackground(getResources().getDrawable(R.drawable.hide));
            } else {
              expandView.setVisibility(View.GONE);
              /*
              expandView.animate()
                      .translationY(expandView.getHeight())
                      .alpha(0.0f)
                      .setDuration(300)
                      .setListener(new AnimatorListenerAdapter() {
                          @Override
                          public void onAnimationEnd(Animator animation) {
                              super.onAnimationEnd(animation);
                              expandView.setVisibility(View.GONE);
                          }
                      }); */
              imageButton.setBackground(getResources().getDrawable(R.drawable.show));
            }
          }
        });

    zoomButtomView.setZoomButtomViewListener(
        new ZoomButtomView.ZoomButtomViewListener() {
          @Override
          public void onClick(ZoomButtomView.Direction direction) {
            Log.d("DIPANKAR", "Zoom: onclick:" + direction.name());
          }

          @Override
          public void onHoverIn(ZoomButtomView.Direction direction) {
            Log.d("DIPANKAR", "Zoom onHoverIn:" + direction.name());
          }

          @Override
          public void onHoverOut(ZoomButtomView.Direction direction) {
            Log.d("DIPANKAR", " Zoom onHoverOut:" + direction.name());
          }
        });
  }
}
