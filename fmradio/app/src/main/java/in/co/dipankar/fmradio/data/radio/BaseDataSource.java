package in.co.dipankar.fmradio.data.radio;

import java.util.List;
import in.co.dipankar.fmradio.entity.radio.Radio;

public abstract class BaseDataSource {
    public interface Callback{
        void onSuccess(List<Radio> list);
        void onFail(String error);
    }
    public abstract void fetch(Callback callback);
}
