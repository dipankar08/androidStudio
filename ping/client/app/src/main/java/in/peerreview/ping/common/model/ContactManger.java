package in.peerreview.ping.common.model;

import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ContactManger implements IContactManager {

  private List<IRtcUser> mUserList;
  private List<ICallInfo> ICallInfoList;
  private List<Callback> mCallbackList;

  public ContactManger() {
    mUserList = new ArrayList<>();
    ICallInfoList = new ArrayList<>();
    mCallbackList = new ArrayList<>();
    // createTestUser();
  }

  public List<IRtcUser> getUserList() {
    return mUserList;
  }

  public List<ICallInfo> getCallList() {
    return ICallInfoList;
  }

  private void createTestUser() {
    // Login user
    mUserList.add(
        new RtcUser(
            "Sharuk Khan",
            "120",
            "https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
    mUserList.add(
        new RtcUser(
            "Prasenjit Chakraborty",
            "121",
            "http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg",
            "https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
    mUserList.add(
        new RtcUser(
            "Katrina",
            "122",
            "https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg",
            "https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));
  }

  private boolean isUserExist(IRtcUser user) {
    for (IRtcUser user1 : mUserList) {
      if (user1.getUserId().equals(user.getUserId())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addContact(IRtcUser user) {
    if (!isUserExist(user)) {
      mUserList.add(0, user);
      for (Callback cb : mCallbackList) {
        cb.onContactListChange(mUserList);
      }
    }
  }

  @Override
  public void addCallInfo(ICallInfo callInfo) {
    ICallInfoList.add(0, callInfo);
    for (Callback cb : mCallbackList) {
      cb.onCallListChange(ICallInfoList);
    }
  }

  @Override
  public void addContactList(List<IRtcUser> userList) {
    boolean isChanged = false;
    for (IRtcUser u : userList) {
      if (!isUserExist(u)) {
        mUserList.add(0, u);
        isChanged = true;
      }
    }
    if (isChanged) {
      for (Callback cb : mCallbackList) {
        cb.onContactListChange(mUserList);
      }
    }
  }

  @Override
  public void changeOnlineState(IRtcUser user, boolean isOnline) {
    if (user == null) {
      return;
    }
    ((RtcUser) user).mOnline = isOnline;
    // TODO: We need update the state if the user is present
    boolean isPresent = false;
    boolean isChange = false;
    for (IRtcUser user1 : mUserList) {
      if (user1.getUserId().equals(user.getUserId())) {
        if (user1.isOnline() == user.isOnline()) {
          // exist and no status changes
          return;
        } else {
          mUserList.remove(user1);
          mUserList.add(user);
          isChange = true;
          isPresent = true;
          break;
        }
      }
    }
    if (isPresent == false) {
      isChange = true;
      mUserList.add(0, user);
    }
    if (isChange) {
      for (Callback cb : mCallbackList) {
        cb.onContactListChange(mUserList);
        cb.onSingleContactChange(user);
      }
    }
  }

  @Override
  public void changeOnlineState(List<IRtcUser> userList, boolean isOnline) {
    for (IRtcUser user : userList) {
      ((RtcUser) user).mOnline = isOnline;
      for (Callback cb : mCallbackList) {
        cb.onPresenceChange(user, isOnline);
      }
    }
    addContactList(userList);
  }

  @Override
  public void addCallback(Callback callback) {
    mCallbackList.add(callback);
  }

  @Override
  public void removeCallback(Callback callback) {
    for (Callback callback1 : mCallbackList) {
      if (callback1 == callback) {
        mCallbackList.remove(callback);
      }
    }
  }

  public IRtcUser getPeerUserForCall(ICallInfo call) {
    if (call.getType() == ICallInfo.CallType.INCOMMING_CALL
        || call.getType() == ICallInfo.CallType.MISS_CALL_INCOMMING) {
      return getUserFromId(call.getFrom());
    } else {
      return getUserFromId(call.getTo());
    }
  }

  private IRtcUser getUserFromId(String id) {
    for (IRtcUser user : mUserList) {
      if (user.getUserId().equals(id)) {
        return user;
      }
    }
    return null;
  }

  public IRtcUser getAnonymousUser() {
    List<String> nameList =
        Arrays.asList(
            "Rabindranath",
            "Srikanto",
            "Nazrul",
            "Ruponkar",
            "Kishore Kumar",
            "R.D Barman",
            "Lata Mangeskar ",
            "Asha Bhosle",
            "Rupom Islam",
            "Nachiketa",
            "Shilajeet_Majumdar",
            "Hematao",
            "Manna Dey");

    List<String> picList =
        Arrays.asList(
            "http://www.nobelprize.org/nobel_prizes/literature/laureates/1913/tagore_postcard.jpg",
            "http://1.bp.blogspot.com/_giEz4yFJ0-g/S-kEjQtwn7I/AAAAAAAAAdg/5bDx8Z_Bojg/s1600/pather+sathi+by+srikanto+acharya+with+dr.+rajib+chakraborty.jpg",
            "http://www.virtualbangladesh.com/wp-content/uploads/2014/11/rxlnn70l.bmp",
            "http://c.saavncdn.com/artists/Rupankar_Bagchi_500x500.jpg",
            "https://passion4pearl.files.wordpress.com/2013/05/kishore-kumar.jpg",
            "http://media2.intoday.in/indiatoday/images/stories/burman_650_062714034414.jpg",
            "http://images.mid-day.com/images/2015/sep/Lata-Mangeshkar-birthday.jpg",
            "http://fridaymoviez.com/images/news/5/680/24424/Singing-Queen-Asha-Bhosle-has-turned-78-today.jpg",
            "https://outofpandora.com/wp-content/uploads/2017/09/outofpandora-rupam-islam-800x445.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Nachiketa_Chakraborty.jpg/1200px-Nachiketa_Chakraborty.jpg",
            "https://upload.wikimedia.org/wikipedia/en/a/a6/Shilajeet_Majumdar.jpg",
            "http://www.tansen.co/wp-content/uploads/2015/08/Hemanta.jpg",
            "http://media.indiatimes.in/media/content/2013/Sep/m_1379576790_540x540.jpg");
    Random rand = new Random();
    int now = rand.nextInt(nameList.size());
    return new RtcUser(nameList.get(now), genRandomId(), picList.get(now), picList.get(now));
  }

  private String genRandomId() {
    String uuid = UUID.randomUUID().toString();
    return "uuid = " + uuid;
  }
}
