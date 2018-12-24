package in.co.dipankar.livetv.newUI;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication mMyApplication;

    public static Application Get() {
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        mMyApplication = this;
        super.onCreate();
    }

}
