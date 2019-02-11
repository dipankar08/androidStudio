package in.peerreview.fmradioindia.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import in.peerreview.fmradioindia.ui.MyApplication;

@Component(modules = {AppModule.class, BuildersModule.class, AndroidInjectionModule.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(MyApplication app);
}

