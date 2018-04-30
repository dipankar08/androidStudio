package in.peerreview.fmradioindia.activities.welcome;

public interface IWelcomeContract {
  interface View {
    void gotoHome();

    void exit();

    void showPermissionDialog();
  }

  interface Presenter {
    void loadData();
  }
}
