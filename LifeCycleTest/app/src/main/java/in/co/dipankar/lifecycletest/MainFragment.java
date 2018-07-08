package in.co.dipankar.lifecycletest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

  private int someVarA;
  private String someVarB;

  private void log(String msg) {
    Log.d("DIPANKAR", "" + this.hashCode() + " : " + msg);
  }

  public static MainFragment newInstance(int someInt, String someTitle) {

    MainFragment fragmentDemo = new MainFragment();
    Log.d("DIPANKAR", fragmentDemo.hashCode() + " : newInstance called.  data set to bundle");
    Bundle args = new Bundle();
    args.putInt("someVarA", someInt);
    args.putString("someVarB", someTitle);
    fragmentDemo.setArguments(args);
    return fragmentDemo;
  }

  // It also return the same state.
  @Override
  public void onCreate(Bundle savedInstanceState) {
    log("onCreate called and Value is retrive from bundle.");
    super.onCreate(savedInstanceState);
    someVarA = getArguments().getInt("someVarA", 0);
    someVarB = getArguments().getString("someVarB", "");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    log("onCreateView called. Now Builidng view or influte view and return.");
    return inflater.inflate(R.layout.fragment2, parent, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    log("onViewCreated called. Now View is creatd and return saved state.");
    // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    // Add listners
  }

  // Save the state.
  @Override
  public void onSaveInstanceState(Bundle outState) {
    log("onSaveInstanceState called");
    super.onSaveInstanceState(outState);
    someVarA++;
    outState.putInt("someVarA", someVarA);
    outState.putString("someVarB", someVarB);
  }

  // Return the saved state.
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    log("onActivityCreated called");
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      someVarA = savedInstanceState.getInt("someVarA");
      someVarB = savedInstanceState.getString("someVarB");
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    log("onDestroyView called");
  }
  /**
   * Hold a reference to the parent Activity so we can report the task's current progress and
   * results. The Android framework will pass us a reference to the newly created Activity after
   * each configuration change.
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // mCallbacks = (TaskCallbacks) activity;
    log("onAttach called");
  }

  /** Set the callback to null so we don't accidentally leak the Activity instance. */
  @Override
  public void onDetach() {
    super.onDetach();
    // mCallbacks = null;
    log("onDetach called");
  }
}
