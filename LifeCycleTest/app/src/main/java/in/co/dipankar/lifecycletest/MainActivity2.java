package in.co.dipankar.lifecycletest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    // Please ensure that we are not duplicate the creation of View.
    MainFragment fragmentDemo =
        (MainFragment) getSupportFragmentManager().findFragmentByTag("Example");
    if (fragmentDemo == null) {
      fragmentDemo = MainFragment.newInstance(5, "my title");
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.your_placeholder, (Fragment) fragmentDemo, "MyTag");
      ft.commit();
    }

    if (savedInstanceState == null) {

    } else {
      int postion =
          savedInstanceState.getInt(
              "position"); // You can retrieve any piece of data you've saved through
      // onSaveInstanceState()

      MainFragment example_fragment =
          (MainFragment) getSupportFragmentManager().findFragmentByTag("Example");

      // update the fragment so that the application retains its state
      // example_fragment.setPosition(position); //This method is just an example
    }
  }
}
