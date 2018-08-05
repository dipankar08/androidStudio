package in.co.dipankar.androidarchcomptestexample.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<UserEntity> getAllItems();

    @Query("SELECT * FROM users WHERE userid = :id")
    UserEntity getItembyId(String id);

    @Insert(onConflict = REPLACE)
    void insert(UserEntity entity);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertTry(UserEntity entity);


    @Update(onConflict = OnConflictStrategy.FAIL)
    int update(UserEntity entity);

    @Delete
    void delete(UserEntity entity);

    @Query("DELETE FROM users WHERE userid = :userId")
    void deleteByUserId(String userId);

    @Query("DELETE FROM users")
    public void deleteAll();
}
