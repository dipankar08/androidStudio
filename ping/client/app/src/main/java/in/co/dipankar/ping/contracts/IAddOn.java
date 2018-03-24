package in.co.dipankar.ping.contracts;

/**
 * Created by dip on 3/23/18.
 */

public interface IAddOn {

    // You need to add a type here
    public enum  AddonType{
        MESSEGE("messege"),
        MUSIC("music"),
        MAP("map")
        ;
        public String mType;
        AddonType(String type) {
            mType = type;
        }
    };

    // Every Addon Must Implement this
    public interface View{
        void sendData();
        void registerSignalCallback(AddonType type, SignalCallback signalCallback);
    }

    public interface SignalCallback{
        void OnReceiveData(AddonType type, String data);
    }

    // This must be
    public interface SignalHandler{

    }

}
