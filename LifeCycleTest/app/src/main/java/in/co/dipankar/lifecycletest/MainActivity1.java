package in.co.dipankar.lifecycletest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity1 extends AppCompatActivity {

  // These variable are destroyed along with Activity
  private int someVarA;
  private String someVarB;

  private TextView t;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);
    Button b = findViewById(R.id.button);
    t = findViewById(R.id.text);
    b.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            someVarA++;
            update();
          }
        });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("someVarA", someVarA);
    outState.putString("someVarB", someVarB);
    Log.d("DIPANKAR", "onSaveInstanceState called for " + this.hashCode());
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    someVarA = savedInstanceState.getInt("someVarA");
    someVarB = savedInstanceState.getString("someVarB");
    Log.d("DIPANKAR", "onRestoreInstanceState called for " + this.hashCode());
    update();
  }

  private void update() {
    t.setText("Count:" + someVarA);
  }
}
