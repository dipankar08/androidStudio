package in.peerreview.fmradioindia.ui.splash;

import in.co.dipankar.quickandorid.arch.BaseViewState;

public class SplashState extends BaseViewState {

  protected SplashState(SplashState.Builder builder) {
    super(builder);
  }

  public static class Builder extends BaseViewState.Builder<SplashState.Builder> {
    public Builder() {}

    public SplashState build() {
      return new SplashState(this);
    }
  }
}
