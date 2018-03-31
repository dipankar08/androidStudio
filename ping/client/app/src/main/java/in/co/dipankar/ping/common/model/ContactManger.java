package in.co.dipankar.ping.common.model;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.ICallInfo;
import in.co.dipankar.ping.contracts.IRtcUser;

public class ContactManger implements IContactManager{

    private List<IRtcUser> mUserList;
    private List<ICallInfo> ICallInfoList;
    private List<Callback> mCallbackList;

    public ContactManger(){
        mUserList = new ArrayList<>();
        ICallInfoList = new ArrayList<>();
        mCallbackList = new ArrayList<>();
        createTestUser();
    }

    public  List<IRtcUser> getUserList(){
        return mUserList;
    }
    public  List<ICallInfo> getCallList(){
        return ICallInfoList;
    }
    private void createTestUser() {
        // Login user
        mUserList.add( new RtcUser("Sharuk Khan","120","https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg","https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
        mUserList.add( new RtcUser("Prasenjit Chakraborty","121","http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg","https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
        mUserList.add( new RtcUser("Katrina","122","https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg","https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));

        ICallInfoList.add(new CallInfo("123", ICallInfo.CallType.INCOMMING_CALL,true,"120","121","5min 10sec","10:00PM","10kB"));
        ICallInfoList.add(new CallInfo("123", ICallInfo.CallType.OUTGOING_CALL,false,"121","120","5min 11sec","11:00PM","10kB"));
        ICallInfoList.add(new CallInfo("123", ICallInfo.CallType.MISS_CALL_OUTGOING,true,"121","120","5min 12sec","12:00PM","10kB"));
        ICallInfoList.add(new CallInfo("123", ICallInfo.CallType.MISS_CALL_INCOMMING,false,"120","121","5min 13sec","13:00PM","10kB"));

    }

    private boolean isUserExist(IRtcUser user){
        for( IRtcUser user1 : mUserList){
            if(user1.getUserId().equals(user.getUserId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void addContact(IRtcUser user) {
        if(! isUserExist(user)){
            mUserList.add(user);
            for (Callback cb: mCallbackList){
                cb.onContactListChange(mUserList);
            }
        }
    }

    @Override
    public void addCallInfo(ICallInfo callInfo){
        ICallInfoList.add(0,callInfo);
        for (Callback cb: mCallbackList){
            cb.onCallListChange(ICallInfoList);
        }
    }

    @Override
    public void addContactList(List<IRtcUser> userList) {
        boolean isChanged = false;
        for( IRtcUser u: userList){
            if(! isUserExist(u)){
                mUserList.add(u);
                isChanged = true;
            }
        }
        if(isChanged){
            for (Callback cb: mCallbackList){
                cb.onContactListChange(mUserList);
            }
        }
    }

    @Override
    public void changeOnlineState(IRtcUser user, boolean isOnline) {
        ((RtcUser)user).mOnline = isOnline;
        addContact(user);
        for (Callback cb: mCallbackList){
            cb.onContactListChange(mUserList);
            cb.onSingleContactChange(user);
        }
    }

    @Override
    public void changeOnlineState(List<IRtcUser> userList, boolean isOnline) {
        for(IRtcUser user: userList){
            ((RtcUser)user).mOnline = isOnline;
            for (Callback cb: mCallbackList){
                cb.onPresenceChange(user, isOnline);
            }
        }
        addContactList(userList);
    }

    @Override
    public void addCallback(Callback callback) {
        mCallbackList.add(callback);
    }

    public IRtcUser getPeerUserForCall(ICallInfo call) {
        if(call.getType() == ICallInfo.CallType.INCOMMING_CALL || call.getType() == ICallInfo.CallType.MISS_CALL_INCOMMING){
            return getUserFromId(call.getFrom());
        } else{
            return getUserFromId(call.getTo());
        }
    }

    private IRtcUser getUserFromId(String id) {
        for(IRtcUser user : mUserList){
            if(user.getUserId().equals(id)){
                return user;
            }
        }
        return  null;
    }
}
