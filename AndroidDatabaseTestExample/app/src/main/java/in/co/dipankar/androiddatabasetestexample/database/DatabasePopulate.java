package in.co.dipankar.androiddatabasetestexample.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DatabasePopulate {
  private SQLiteDatabase mDb;

  public DatabasePopulate(SQLiteDatabase db) {
    mDb = db;
  }

  public void insertStudent() {
    insert("DIPANKAR", 10);
    insert("DIPANKAR1", 11);
    insert("DIPANKAR2", 12);
    insert("DIPANKAR3", 10);
  }

  private void insert(String name, int age) {
    ContentValues values = new ContentValues();
    values.put(StudentDatabaseContract.StudentEntry.NAME, name);
    values.put(StudentDatabaseContract.StudentEntry.AGE, age);
    long newRowId = mDb.insert(StudentDatabaseContract.StudentEntry.TABLE_NAME, null, values);
  }
}
