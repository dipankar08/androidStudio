package in.peerreview.ping.activities.bell;

import com.google.gson.Gson;

import java.util.List;

import in.peerreview.ping.common.signaling.IDataMessage;
import in.peerreview.ping.contracts.ICallSignalingApi;

/**
 * Created by dip on 4/21/18.
 */

public class BellInfo implements IDataMessage{
    String mAuthor;

    public String getmAuthor() {
        return mAuthor;
    }

    public List<String> getmParticepents() {
        return mParticepents;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public String getmId() {
        return mId;
    }

    List<String> mParticepents;
    String mTitle;
    String mMessage;
    String mTimeStamp;
    String mId;

    public BellInfo(String mAuthor, List<String> mParticepents, String mTitle, String mMessage, String mTimeStamp, String mId) {
        this.mAuthor = mAuthor;
        this.mParticepents = mParticepents;
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mTimeStamp = mTimeStamp;
        this.mId = mId;
    }

    public void setParticepents(List<String> mParticepents) {
        this.mParticepents = mParticepents;
    }

    @Override
    public List<String> getRecipents() {
        return mParticepents;
    }

    @Override
    public ICallSignalingApi.MessageType getMessageType() {
        return ICallSignalingApi.MessageType.BELL;
    }

    @Override
    public String getRawData() {
        Gson gson = new Gson();
        return gson.toJson(this).toString();
    }
}
