package in.co.dipankar.androidarchcomptestexample.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase db;
    public abstract UserDao movieDao();

    public static UserDatabase getAppDatabase(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, "movie-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }

    public static void destroyInstance() {
        db = null;
    }
}