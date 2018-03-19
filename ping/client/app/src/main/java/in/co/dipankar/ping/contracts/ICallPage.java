package in.co.dipankar.ping.contracts;

/**
 * Created by dip on 3/17/18.
 */

public interface ICallPage {

    public enum PageViewType{
        LANDING,
        INCOMMING,
        OUTGOING,
        ONGOING,
        ENDED
    }
    public interface IView{

        void switchToView(PageViewType incomming);
        void showNetworkNotification(String process, String s);
        void updateEndView(String msg);
    }

    public interface IPresenter{

        void endCall();


        void startAudio(IRtcUser peer);

        void startVideo(IRtcUser peer);

        void acceptCall();

        void rejectCall();

        void toggleVideo(boolean isOn);

        void toggleCamera(boolean isOn);

        void toggleAudio(boolean isOn);
    }
}
