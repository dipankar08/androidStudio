package in.co.dipankar.androiddatabasetestexample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import in.co.dipankar.androiddatabasetestexample.database.StudentDatabaseContract;
import in.co.dipankar.androiddatabasetestexample.database.StudentDatabaseOpenHelper;

public class MainActivity extends AppCompatActivity {

    StudentDatabaseOpenHelper mStudentDatabaseOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStudentDatabaseOpenHelper = new StudentDatabaseOpenHelper(this);
    }

    public void getAllEntry(View view) {
        SQLiteDatabase  db = mStudentDatabaseOpenHelper.getReadableDatabase();

        final String[] col = {
                StudentDatabaseContract.StudentEntry._ID,
                StudentDatabaseContract.StudentEntry.AGE,
                StudentDatabaseContract.StudentEntry.NAME
        };

        final Cursor cursor = db.query(StudentDatabaseContract.StudentEntry.TABLE_NAME,
                col, null, null, null, null,null);
        print(cursor);
        cursor.close();

    }

    private void print(Cursor cursor) {
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(
                    StudentDatabaseContract.StudentEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(
                    StudentDatabaseContract.StudentEntry.NAME));
            int age = cursor.getInt(cursor.getColumnIndex(
                    StudentDatabaseContract.StudentEntry.AGE));
            Log.d("DIPANKAR","Id: "+id+" Student # Name:"+name+" Age:"+age);
        }
        Log.d("DIPANKAR","\n\n\n");
    }

    public void filterQuery(View view) {
        SQLiteDatabase  db = mStudentDatabaseOpenHelper.getReadableDatabase();

        final String[] col = {
                StudentDatabaseContract.StudentEntry._ID,
                StudentDatabaseContract.StudentEntry.AGE,
                StudentDatabaseContract.StudentEntry.NAME
        };

        String selection = StudentDatabaseContract.StudentEntry._ID + " > ?";
        String[] selectionArgs = {Integer.toString(2)};


        final Cursor cursor = db.query(StudentDatabaseContract.StudentEntry.TABLE_NAME,
                col, selection, selectionArgs, null, null,null);
        print(cursor);
        cursor.close();

    }

    public void sortQuery(View view) {
        SQLiteDatabase  db = mStudentDatabaseOpenHelper.getReadableDatabase();

        final String[] col = {
                StudentDatabaseContract.StudentEntry._ID,
                StudentDatabaseContract.StudentEntry.AGE,
                StudentDatabaseContract.StudentEntry.NAME
        };

        String noteOrderBy = StudentDatabaseContract.StudentEntry.AGE + " DESC," + StudentDatabaseContract.StudentEntry.NAME;


        final Cursor cursor = db.query(StudentDatabaseContract.StudentEntry.TABLE_NAME,
                col, null, null, null, null,noteOrderBy,"10");
        print(cursor);
        cursor.close();
    }

    public void insertData(View view) {
        ContentValues values = new ContentValues();
        values.put(StudentDatabaseContract.StudentEntry.NAME, "SUBHANKAR");
        values.put(StudentDatabaseContract.StudentEntry.AGE, 100);
        long newRowId = mStudentDatabaseOpenHelper.getWritableDatabase().insert(StudentDatabaseContract.StudentEntry.TABLE_NAME,
                null, values);
        Log.d("DIPANKAR","Insert Done:"+newRowId);
        values = new ContentValues();
        values.put(StudentDatabaseContract.StudentEntry.NAME, "RAM");
        values.put(StudentDatabaseContract.StudentEntry.AGE, 10);
        newRowId = mStudentDatabaseOpenHelper.getWritableDatabase().insertOrThrow(StudentDatabaseContract.StudentEntry.TABLE_NAME,
                null, values);
        Log.d("DIPANKAR","Insert Done:"+newRowId);
    }

    public void updateData(View view) {
        ContentValues values = new ContentValues();
        values.put(StudentDatabaseContract.StudentEntry.AGE, "110");
        String selection = StudentDatabaseContract.StudentEntry.NAME + " LIKE ?";
        String[] selectionArgs = { "DIP%" };
        int deletedRows = mStudentDatabaseOpenHelper.getWritableDatabase().
                update(StudentDatabaseContract.StudentEntry.TABLE_NAME,values, selection, selectionArgs);
        Log.d("DIPANKAR","update Done:"+deletedRows);
    }

    public void delData(View view) {
        String selection = StudentDatabaseContract.StudentEntry.NAME + " LIKE ?";
        String[] selectionArgs = { "DIP%" };
        int deletedRows = mStudentDatabaseOpenHelper.getWritableDatabase().
                delete(StudentDatabaseContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
        Log.d("DIPANKAR","Delete Done:"+deletedRows);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStudentDatabaseOpenHelper.close();
    }

    public void getCount(View view) {
        String selection = StudentDatabaseContract.StudentEntry.NAME+" LIKE ? AND "+ StudentDatabaseContract.StudentEntry.AGE+" = ?";
        String[] selectionArgs = {"DIP", "110"};
        String tableName = StudentDatabaseContract.StudentEntry.TABLE_NAME;
        Cursor c = mStudentDatabaseOpenHelper.getReadableDatabase().query(tableName, null, selection, selectionArgs, null, null, null);
        int result = c.getCount();
        c.close();
        Log.d("DIPANKAR","Count Done:"+result);
    }
}
