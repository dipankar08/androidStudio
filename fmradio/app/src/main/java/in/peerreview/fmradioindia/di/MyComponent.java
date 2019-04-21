package in.peerreview.fmradioindia.di;

import dagger.Component;
import in.peerreview.fmradioindia.applogic.ThreadUtils;
import in.peerreview.fmradioindia.ui.views.home.HomePresenter;
import in.peerreview.fmradioindia.ui.activity_v1.mainactivity.MainPresenter;
import in.peerreview.fmradioindia.ui.views.player.PlayerPresenter;
import in.peerreview.fmradioindia.ui.views.pref.UserPrefPresenter;
import in.peerreview.fmradioindia.ui.views.search.SearchPresenter;
import in.peerreview.fmradioindia.ui.views.splash.SplashPresenter;
import javax.inject.Singleton;

@Singleton
@Component(modules = {ContextModule.class})
public interface MyComponent {
  void inject(PlayerPresenter presenter);

  void inject(MainPresenter presenter);

  void inject(HomePresenter presenter);

  void inject(SearchPresenter searchPresenter);

  void inject(SplashPresenter splashPresenter);

  void inject(UserPrefPresenter presenter);

  void inject(ThreadUtils threadUtils);
}
