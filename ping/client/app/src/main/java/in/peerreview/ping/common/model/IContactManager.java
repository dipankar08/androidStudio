package in.peerreview.ping.common.model;

import in.peerreview.ping.activities.bell.BellInfo;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;
import java.util.List;

public interface IContactManager {
  void addContact(IRtcUser user);

  void addCallInfo(ICallInfo callInfo);

  void addContactList(List<IRtcUser> userList);

  void changeOnlineState(IRtcUser user, boolean isOnline);

  void changeOnlineState(List<IRtcUser> userList, boolean isOnline);

  void addCallback(Callback mContactManagerCallback);

  void removeCallback(Callback mContactMangerCallback);


  public interface Callback {
    void onContactListChange(List<IRtcUser> userList);

    void onSingleContactChange(IRtcUser user);

    void onPresenceChange(IRtcUser user, boolean isOnline);

    void onCallListChange(List<ICallInfo> mCallInfo);
  }
}
