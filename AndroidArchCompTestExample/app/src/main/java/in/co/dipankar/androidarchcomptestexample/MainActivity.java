package in.co.dipankar.androidarchcomptestexample;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import in.co.dipankar.androidarchcomptestexample.room.UserDatabase;
import in.co.dipankar.androidarchcomptestexample.room.UserEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private LCAComponents mLCAComponents;
  private MutableLiveData<List<User>> userLiveData = new MutableLiveData<>();

  private SharedViewModel mSharedViewModel;

  private UserDatabase mUserDatabase;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ////////////  Life Cycle Component /////////////
    mLCAComponents = new LCAComponents(this, getLifecycle());

    /////////  LiveData Examples ////////////////////////////
    userLiveData.observe(
        this,
        new Observer<List<User>>() {
          @Override
          public void onChanged(@Nullable List<User> users) {
            Log.d("DIPANKAR", " Live Data Chnaged: Name:" + users.get(0).name);
          }
        });

    ///////////  ViewModel using Live Data ////////////////
    mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
    Log.d("DIPANKAR", "View Model restore hash:" + mSharedViewModel.hashCode());
    mSharedViewModel
        .getSelected()
        .observe(
            this,
            new Observer<User>() {

              @Override
              public void onChanged(@Nullable User user) {
                Log.d("DIPANKAR", " Live Data Chnaged: Name:" + user.name);
              }
            });

    //////  ViewModel + LiveData + Room ////////////////
    mUserDatabase = UserDatabase.getAppDatabase(getApplicationContext());
  }

  public void testLCA(View view) {}

  public void testViewModel(View view) {
    final List<User> newList = new ArrayList<>();
    newList.add(new User("DIP"));
    userLiveData.setValue(newList);
    // userLiveData.postValue(newList); Not works
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                // userLiveData.setValue(newList); <<< Would be crashed
                userLiveData.postValue(newList);
              }
            })
        .start();
  }

  public void testLiveData(View view) {}

  public void insertRoom(View view) {
    mUserDatabase.movieDao().insert(new UserEntity("1", "Dipankar", new Date()));
    mUserDatabase.movieDao().insert(new UserEntity("2", "Dipankar", new Date()));
    mUserDatabase.movieDao().insert(new UserEntity("3", "Dipankar", new Date()));
  }

  public void deleteRoom(View view) {
    mUserDatabase.movieDao().deleteByUserId("1");
  }

  public void updateRoom(View view) {
    int res = mUserDatabase.movieDao().update(new UserEntity("1", "Dipankar123", new Date()));
    Log.d("DIPANKAR", "Room::update count -> " + res);
  }

  public void getRoom(View view) {
    if (mUserDatabase.movieDao().getAllItems() == null) {
      Log.d("DIPANKAR", "Room::get -> 0");
      return;
    }
    for (UserEntity userEntity : mUserDatabase.movieDao().getAllItems()) {
      Log.d("DIPANKAR", "Room Get: " + userEntity.toString());
    }
  }

  public void insertOrUpdate(View view) {
    // insert of update
    upsert(new UserEntity("1", "Dipankar123", new Date()));
  }

  public void upsert(UserEntity entity) {
    try {
      mUserDatabase.movieDao().insertTry(entity);
      Log.d("DIPANKAR", "Room::insert done");
    } catch (SQLiteConstraintException exception) {
      mUserDatabase.movieDao().update(entity);
      Log.d("DIPANKAR", "Room::insert fail and update done -> 0");
    }
  }
}
