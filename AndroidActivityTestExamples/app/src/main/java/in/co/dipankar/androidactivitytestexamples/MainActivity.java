package in.co.dipankar.androidactivitytestexamples;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    private static final int STATIC_INTEGER_VALUE = 101 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void TestLifeCycle(View view) {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    public void testSaveAndRestore(View view) {
        Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
        startActivity(intent);
    }

    public void testPassingdata(View view) {
        Intent intent = new Intent(MainActivity.this, ForthActivity.class);
        intent.putExtra("data", 11);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    public void testReturndata(View view) {
        Intent intent = new Intent(MainActivity.this, FifthActivity.class);
        startActivity(intent);
    }

    public void testConfigChanges(View view) {
        Intent intent = new Intent(MainActivity.this, SixthActivity.class);
        startActivity(intent);
    }

    public void testRetainedActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SeventhActivity.class);
        startActivity(intent);
    }

    // All Override function.
    private void log(String msg){
        Log.d("DIPANKAR","[Thread:"+Thread.currentThread().getName()+" Obj:"+this.hashCode()+"]"+this.getClass().getSimpleName()+"::"+msg);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        log("onPostCreate called");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        log("onBackPressed called");
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        log("onPause called");
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log("onNewIntent called");
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        log("onResume called");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        log("onRestart called");
        super.onRestart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        log("onConfigurationChanged called");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        log("onDestroy called");
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        log("onPostResume called");
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        log("onStart called");
        super.onStart();
    }

    @Override
    protected void onStop() {
        log("onStop called");
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    int data1 = data.getIntExtra("data", 0);
                    log("onActivityResult called with data"+data1);
                }
                break;
            }
        }
    }
}
