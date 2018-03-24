package in.co.dipankar.ping.common.model;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.IRtcUser;

/**
 * Created by dip on 3/16/18.
 */

public class UserManager {
    private List<IRtcUser> loginUserList;
    private List<IRtcUser> liveUserList;
    private List<IRtcUser> recentUserList;

    public UserManager(){
        loginUserList = new ArrayList<>();
        liveUserList = new ArrayList<>();
        recentUserList = new ArrayList<>();
        createTestUser();
    }

    public  IRtcUser getLoginUser(int id){
        return loginUserList.get(id);
    }

    public  List<IRtcUser> getRecentUserList(){
        return recentUserList;
    }


    private void createTestUser() {
        // Login user
        loginUserList.add( new RtcUser("Sharuk Khan","120","https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg","https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
        loginUserList.add( new RtcUser("Prasenjit Chakraborty","121","http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg","https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
        loginUserList.add( new RtcUser("Katrina","122","https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg","https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));

        // Login user
        loginUserList.add( new RtcUser("Sharuk Khan","120","https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg","https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
        loginUserList.add( new RtcUser("Prasenjit Chakraborty","121","http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg","https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
        loginUserList.add( new RtcUser("Katrina","122","https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg","https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));


        // Login user
        recentUserList.add( new RtcUser("Sharuk Khan","120","https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg","https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
        recentUserList.add( new RtcUser("Prasenjit Chakraborty","121","http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg","https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
        recentUserList.add( new RtcUser("Katrina","122","https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg","https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));
        recentUserList.add( new RtcUser("Sharuk Khan","120","https://images-na.ssl-images-amazon.com/images/M/MV5BZDk1ZmU0NGYtMzQ2Yi00N2NjLTkyNWEtZWE2NTU4NTJiZGUzXkEyXkFqcGdeQXVyMTExNDQ2MTI@._V1_UY317_CR4,0,214,317_AL_.jpg","https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Sharukhan.jpg/1200px-Sharukhan.jpg"));
        recentUserList.add( new RtcUser("Prasenjit Chakraborty","121","http://www.prosenjit.in/yahoo_site_admin/assets/images/contact-us1.81155628_std.jpg","https://i2.wp.com/short-biography.com/wp-content/uploads/prosenjit-chatterjee/Prosenjit_Chatterjee1.jpg?w=1280&ssl=1"));
        recentUserList.add( new RtcUser("Katrina","122","https://pbs.twimg.com/profile_images/654550917778305024/hcAaURmF_400x400.jpg","https://www.indiatoday.in/movies/celebrities/story/katrina-kaif-34th-birthday-plans-jagga-jasoos-982859-2017-06-15"));


    }

}
