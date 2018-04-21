package in.peerreview.ping.common.model;

import android.widget.LinearLayout;

import in.peerreview.ping.activities.bell.BellInfo;
import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactManger implements IContactManager {

  final String USER_TABLE = "user_table";
  final String CALLINFO_TABLE = "callinfo_table";

  private List<IRtcUser> mUserList;
  private List<ICallInfo> mCallInfoList;
  private List<BellInfo> mBellInfoList;
  private List<Callback> mCallbackList;
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  public ContactManger() {
    mUserList = new ArrayList<>();
    mCallInfoList = new ArrayList<>();
    mCallbackList = new ArrayList<>();
    mBellInfoList = new ArrayList<>();
    test();
  }

  private void test() {
    List<String> par = new ArrayList<>();
    par.add("dutta.dipankar08@gmail.com");
    BellInfo bellInfo = new BellInfo("Dipankar",par,"Tea?","Let's fo gor a Tea","10:00","100");
    mBellInfoList.add(bellInfo);
  }

  public void restore() {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            restoreInternal();
          }
        });
  }

  private void restoreInternal() {
    mUserList = Paper.book().read(USER_TABLE);
    mCallInfoList = Paper.book().read(CALLINFO_TABLE);
    if (mUserList == null) {
      mUserList = new ArrayList<>();
    } else {
      for (IRtcUser user : mUserList) {
        user.setOnline(false);
      }
    }
    if (mCallInfoList == null) {
      mCallInfoList = new ArrayList<>();
    }
    noifyUserListChanged();
    notifyCallInfoListChanged();
  }

  public void save() {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            saveInternal();
          }
        });
  }

  private void saveInternal() {
    if (mUserList != null) {
      if (mUserList.size() > 10) {
        mUserList = mUserList.subList(0, 9);
      }
      Paper.book().write(USER_TABLE, mUserList);
    }
    if (mCallInfoList != null) {
      if (mCallInfoList.size() > 20) {
        mCallInfoList = mCallInfoList.subList(0, 19);
      }
      Paper.book().write(CALLINFO_TABLE, mCallInfoList);
    }
  }

  private void noifyUserListChanged() {
    for (Callback callback : mCallbackList) {
      callback.onContactListChange(mUserList);
    }
  }

  private void notifyCallInfoListChanged() {
    for (Callback callback : mCallbackList) {
      if (callback != null) {
        callback.onCallListChange(mCallInfoList);
      }
    }
  }

  public List<IRtcUser> getUserList() {
    return mUserList;
  }

  public List<ICallInfo> getCallList() {
    return mCallInfoList;
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
      save();
    }
  }

  @Override
  public void addCallInfo(ICallInfo callInfo) {
    mCallInfoList.add(0, callInfo);
    for (Callback cb : mCallbackList) {
      cb.onCallListChange(mCallInfoList);
    }
    save();
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
      save();
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
    if (userList == null) return;
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
    if (mCallbackList == null) {
      return;
    }
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

  public List<BellInfo> getBellInfoList(){
    return mBellInfoList;
  }
}
