package in.co.dipankar.bengalisuspense.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import in.co.dipankar.bengalisuspense.R;

public class TestActivity extends AppCompatActivity {

  ImageView imageView;
  Button button;
  TimeInterpolator timeInterpolator;
  ObjectAnimator objectAnimator;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    imageView = findViewById(R.id.image);
    button = findViewById(R.id.test);
    button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            testAnimation();
          }
        });
    Spinner dropdown = findViewById(R.id.spinner1);
    String[] items =
        new String[] {
          "BounceInterpolator",
          "LinearInterpolator",
          "AccelerateInterpolator",
          "AccelerateDecelerateInterpolator",
          "AnticipateInterpolator",
          "Cancel"
        };
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
    dropdown.setAdapter(adapter);
    dropdown.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(
              AdapterView<?> parentView, View selectedItemView, int position, long id) {
            switch (position) {
              case 0:
                timeInterpolator = new BounceInterpolator();
                break;
              case 1:
                timeInterpolator = new LinearInterpolator();
                break;
              case 2:
                timeInterpolator = new AccelerateInterpolator();
                break;
              case 3:
                timeInterpolator = new AccelerateDecelerateInterpolator();
                break;
              case 4:
                timeInterpolator = new AnticipateInterpolator();
                break;
              case 5:
                timeInterpolator = null;
                break;
            }
            testAnimation();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
          }
        });

    EditText editText = findViewById(R.id.editText);

    objectAnimator =
        ObjectAnimator.ofPropertyValuesHolder(
            imageView,
            PropertyValuesHolder.ofFloat("scaleX", 1.8f),
            PropertyValuesHolder.ofFloat("scaleY", 1.8f));

    objectAnimator =
        ObjectAnimator.ofPropertyValuesHolder(
            editText, PropertyValuesHolder.ofFloat("scaleX", 2.0f));
  }

  private void testBounceAnimation() {
    Animation bounceUpDown = AnimationUtils.loadAnimation(this, R.anim.bounce_up_down);
    imageView.startAnimation(bounceUpDown);
  }

  private void testAnimation() {
    objectAnimator.cancel();

    objectAnimator.setDuration(1000);

    if (timeInterpolator != null) {
      objectAnimator.setInterpolator(timeInterpolator);
    }

    // This will make it reperated
    // objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    // Next Reperation will be in reverse order.
    // objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
    objectAnimator.start();
  }
}
