package in.peerreview.fmradioindia.ui.home;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.applogic.Channel;
import java.util.List;

public class HomeState extends BaseViewState {
  private List<Channel> mChannel;

  protected HomeState(Builder builder) {
    super(builder);
    mChannel = builder.mChannel;
  }

  public List<Channel> getChannel() {
    return mChannel;
  }

  public static class Builder extends BaseViewState.Builder<Builder> {
    private List<Channel> mChannel = null;

    public Builder() {}

    public Builder setChannel(List<Channel> channel) {
      mChannel = channel;
      return this;
    }

    public HomeState build() {
      return new HomeState(this);
    }
  }
}
