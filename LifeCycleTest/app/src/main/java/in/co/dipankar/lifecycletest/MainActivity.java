package in.co.dipankar.lifecycletest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private LengthPicker mWidth;
  private LengthPicker mHeight;
  private TextView mArea;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mWidth = (LengthPicker) findViewById(R.id.width);
    mHeight = (LengthPicker) findViewById(R.id.height);
    mArea = (TextView) findViewById(R.id.area);

    LengthPicker.OnChangeListener listener =
        new LengthPicker.OnChangeListener() {
          @Override
          public void onChange(int length) {
            updateArea();
          }
        };
    mWidth.setOnChangeListener(listener);
    mHeight.setOnChangeListener(listener);
  }

  private void updateArea() {
    int area = mWidth.getNumInches() * mHeight.getNumInches();
    mArea.setText(area + " sq in");
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateArea();
  }
}
