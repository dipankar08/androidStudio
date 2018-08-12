package in.co.dipankar.fmradio.data;

import java.util.List;

import in.co.dipankar.fmradio.entity.radio.Radio;

/* This interface should define all the functinality for Radio data Operation */
public interface RadioListManager {
    interface RadioListManagerCallBack{
        void onReceived(List<Radio> radio);
        void onError(String err);
    }
    void fetchRadioList(RadioListManagerCallBack callBack);
}
