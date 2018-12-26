package in.co.dipankar.livetv.ui;

import java.util.List;

import in.co.dipankar.livetv.data.Channel;
import in.co.dipankar.quickandorid.arch.BaseViewState;

public class MainState extends BaseViewState {
    private List<Channel> mChannel;
    private String mError;
    private Boolean isListOpen;
    private Channel mCurChannel;
    private Boolean mIsShowControl;
    private Boolean mIsShowLoading;
    private List<String> mCat;
    protected MainState(Builder builder) {
        super(builder);
        mChannel = builder.mChannel;
        mError = builder.mError;
        isListOpen = builder.isListOpen;
        mCurChannel = builder.mCurChannel;
        mIsShowControl = builder.mIsShowControl;
        mIsShowLoading = builder.mIsShowLoading;
        mCat = builder.mCat;
    }

    public String getErrorMsg() {
        return mError;
    }
    public Channel getCurChannel() {
        return mCurChannel;
    }

    public Boolean getIsListOpen() {
        return isListOpen;
    }
    public Boolean getIsShowControl() {
        return mIsShowControl;
    }
    public Boolean getIsShowLoading(){ return  mIsShowLoading;}

    public List<Channel> getChannel() {
        return mChannel;
    }
    public List<String> getCat() {
        return mCat;
    }

    public static class Builder extends BaseViewState.Builder<Builder> {
        private List<Channel> mChannel = null;
        private Channel mCurChannel;
        private String mError = null;
        private Boolean isListOpen = null;
        private Boolean mIsShowControl = null;
        private Boolean mIsShowLoading = null;
        private List<String> mCat= null;
        public Builder(){}
        public Builder setChannel(List<Channel> channel){
            mChannel=channel;
            return this;
        }
        public Builder setErrorMsg(String error){
            mError=error;
            return this;
        }
        public Builder setIsListOpen(boolean isListOpen){
            this.isListOpen = isListOpen;
            return this;
        }
        public Builder setCurChannel( Channel curChannel){
            this.mCurChannel = curChannel;
            return this;
        }
        public Builder setIsShowControl(boolean mIsShowControl){
            this.mIsShowControl =mIsShowControl;
            return this;
        }
        public Builder setIsShowLoading(boolean mIsShowLoading){
            this.mIsShowLoading = mIsShowLoading;
            return this;
        }

        public Builder setCat(List<String> mCat){
            this.mCat = mCat;
            return this;
        }
        public MainState build(){
            return new MainState(this);
        }
    }
}
