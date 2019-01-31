package in.peerreview.fmradioindia.ui.player;
import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Channel;


public class PlayerState extends BaseViewState {
    public enum State{
        TRY_PLAYING,
        PLAYING,
        STOP,
        ERROR,
        PAUSE,
        RESUME
    }
    private Channel channel;
    private State state;

    public Channel getChannel() {
        return channel;
    }

    public State getState() {
        return state;
    }

    protected PlayerState(Builder builder) {
        super(builder);
        state = builder.state;
        channel = builder.channel;
    }

    public static class Builder extends BaseViewState.Builder<PlayerState.Builder> {
        private Channel channel = null;
        private State state = null;

        public Builder() {}

        public Builder setChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public Builder setState(State state ) {
            this.state = state;
            return this;
        }
        public PlayerState build() {
            return new PlayerState(this);
        }
    }

}
