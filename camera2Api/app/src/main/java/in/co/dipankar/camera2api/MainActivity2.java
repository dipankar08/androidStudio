package in.co.dipankar.camera2api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

/** Created by dip on 3/14/18. */
public class MainActivity2 extends AppCompatActivity {
  private static final String TAG = "AndroidCameraApi";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Camera2Api camera2Api =
        new Camera2Api(
            this,
            new Camera2Api.ICallback() {
              @Override
              public void handleRawData(byte[] rawdata) {
                // ignore
              }
            });
    camera2Api.addSelfTextureView((TextureView) findViewById(R.id.texture));
    camera2Api.startOperation();
  }
}
