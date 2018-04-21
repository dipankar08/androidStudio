package in.peerreview.ping.common.signaling;

import java.time.chrono.MinguoEra;
import java.util.List;

import in.peerreview.ping.contracts.ICallSignalingApi;

/**
 * Created by dip on 4/21/18.
 */

public interface IDataMessage {
     List<String> getRecipents();
     ICallSignalingApi.MessageType getMessageType();
     String getRawData();
}
