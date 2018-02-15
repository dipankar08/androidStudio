package in.peerreview.fmradioindia.activities.welcome;

/** Created by dip on 2/14/18. */
public interface IWelcomeContract {
  interface View {
    void gotoHome();

    void exit();
  }

  interface Presenter {
    void loadData();
  }
}
