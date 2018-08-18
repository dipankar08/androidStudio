package in.co.dipankar.androiddatabasetestexample.database;

import android.provider.BaseColumns;

public class StudentDatabaseContract {
  private StudentDatabaseContract() {}; // private

  public static final class StudentEntry implements BaseColumns {
    public static final String TABLE_NAME = "students";
    public static final String NAME = "name";
    public static final String AGE = "age";

    // CREATE TABLE course_info (course_id, course_title)
    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY, "
            + NAME
            + " TEXT UNIQUE NOT NULL, "
            + AGE
            + " NUMBER NOT NULL)";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // CREATE INDEX course_info_index1 ON course_info (course_title)
    public static final String INDEX1 = TABLE_NAME + "_index1";
    public static final String SQL_CREATE_INDEX1 =
        "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME + "(" + NAME + ")";
  }
  // you must have define other table here.

}
