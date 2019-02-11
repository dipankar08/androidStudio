package in.peerreview.fmradioindia.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    // Add bindings for other sub-components here
}