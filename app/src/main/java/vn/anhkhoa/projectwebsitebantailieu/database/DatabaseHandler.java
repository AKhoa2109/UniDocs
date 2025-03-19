package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "";
    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //truy vấn không trả kết quả Create, Insert, update, delete,..
    public void QueryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    //truy vấn có trả kết quả Select
    public Cursor GetData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery (sql, null);
    }
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
    }
    @Override
    public void onUpgrade (SQLiteDatabase sqliteDatabase, int i, int i1){
    }

}
