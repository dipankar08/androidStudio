package in.peerreview.ping.activities.bell;

/** Created by dip on 4/21/18. */
public interface IBell {
  public interface View {

    void showIncoming(String title, String subtitle);

    void showSent(String title, String subtitle, boolean result);
  }

  public interface Presenter {

    void accept();

    void reject();

    void resend();

    void finish();
  }
}
