package in.peerreview.ping.common.signaling;

import in.peerreview.ping.contracts.ICallSignalingApi;
import java.util.List;

/** Created by dip on 4/21/18. */
public interface IDataMessage {
  List<String> getRecipents();

  ICallSignalingApi.MessageType getMessageType();

  String getRawData();
}
