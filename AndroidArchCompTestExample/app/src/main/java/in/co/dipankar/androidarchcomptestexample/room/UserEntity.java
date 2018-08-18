package in.co.dipankar.androidarchcomptestexample.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "users")
@TypeConverters(DateConverter.class)
public class UserEntity {

  @PrimaryKey
  @ColumnInfo(name = "userid")
  @NonNull
  public String mId;

  @ColumnInfo(name = "username")
  public String mUserName;

  @ColumnInfo(name = "last_update")
  public Date mDate;

  @Ignore
  public UserEntity(String userName) {
    mId = UUID.randomUUID().toString();
    mUserName = userName;
    mDate = new Date(System.currentTimeMillis());
  }

  public UserEntity(String id, String userName, Date date) {
    this.mId = id;
    this.mUserName = userName;
    this.mDate = date;
  }

  @Override
  public String toString() {
    return "(" + mId + "," + mUserName + "," + mDate + ")";
  }
}
