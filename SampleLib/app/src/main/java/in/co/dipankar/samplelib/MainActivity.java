package in.co.dipankar.samplelib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import in.co.dipankar.preferenceutils.L;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    L.d("Hello Dipankar");
  }
}
