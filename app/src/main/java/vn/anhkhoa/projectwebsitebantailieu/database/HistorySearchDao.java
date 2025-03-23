package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;


public class HistorySearchDao extends BaseDao{
    // Table structure
    public static final String TABLE_NAME = "HistorySearch";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_NAME = "Name";

    // Create table SQL
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " VARCHAR(200)"+")";

    public HistorySearchDao(Context context) {
        super(context, TABLE_NAME);
    }

    public long addSearchQuery(String query) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, query);
        return insert(values);
    }

    public int deleteSearchQuery() {
        return delete(null, null);
    }

    public ArrayList<DocumentDto> getAllHistory()
    {
        ArrayList<DocumentDto> history = new ArrayList<>();
       Cursor cursor = query(new String[]{COLUMN_ID, COLUMN_NAME}, null, null, COLUMN_ID + " DESC");
       while (cursor.moveToNext()) {
           history.add(new DocumentDto(cursor.getLong(0),cursor.getString(1)));
       }
       cursor.close();
       return history;
    }

    public boolean queryExists(String query) {
        Cursor cursor = query(
                new String[]{COLUMN_ID},
                COLUMN_NAME + " = ?",
                new String[]{query},
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
