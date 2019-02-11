package in.peerreview.fmradioindia.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.security.Signature;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import in.peerreview.fmradioindia.applogic.ChannelManager;
import in.peerreview.fmradioindia.applogic.DataFetcher;
import in.peerreview.fmradioindia.applogic.MusicManager;
import in.peerreview.fmradioindia.applogic.StatManager;
import in.peerreview.fmradioindia.applogic.StorageManager;
import in.peerreview.fmradioindia.applogic.TelemetryManager;
import in.peerreview.fmradioindia.ui.MyApplication;
import okhttp3.OkHttpClient;

@Module
public class AppModule {
    @Provides
    Context provideContext(MyApplication application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        return okHttpClient.build();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences("ENJOY_MUSIC_PREFS", Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    MusicManager provideMusicManager(TelemetryManager t, StatManager s, ChannelManager c) {
        return new MusicManager(t,s,c );
    }

    @Singleton
    @Provides
    ChannelManager provideChannelManager(DataFetcher d, StorageManager s){
        return new ChannelManager(d, s);
    }

}
