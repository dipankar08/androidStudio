package in.co.dipankar.fmradio.ui.viewpresenter.player;

import java.io.Serializable;

import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.ui.base.BasePresenter;

public class FullScreenPlayerViewPresenter extends BasePresenter {

    private Radio mRadio;
    public void setRadio(Radio radio) {
        mRadio = radio;
    }

    public interface ViewContract {

    }


}
