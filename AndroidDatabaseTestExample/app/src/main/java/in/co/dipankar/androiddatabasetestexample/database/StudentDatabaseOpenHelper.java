package in.co.dipankar.androiddatabasetestexample.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StudentDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Student.db";
    public static final int DATABASE_VERSION = 2;

    public StudentDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DIPANKAR","onCreate called");
        db.execSQL(StudentDatabaseContract.StudentEntry.SQL_CREATE_TABLE);
        //prrepopulate the data here.
        DatabasePopulate  dp = new DatabasePopulate(db);
        dp.insertStudent();
        //do the indexing.
        db.execSQL(StudentDatabaseContract.StudentEntry.SQL_CREATE_INDEX1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DIPANKAR","onUpgrade called");
        db.execSQL(StudentDatabaseContract.StudentEntry.SQL_DROP_TABLE);
        onCreate(db);
    }
}
