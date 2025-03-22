package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public abstract class BaseDao {
    protected DatabaseHandler dbHandler;
    protected String tableName;

    public BaseDao(Context context, String tableName) {
        this.dbHandler = DatabaseHandler.getInstance(context);
        this.tableName = tableName;
    }

    protected long insert(ContentValues values) {
        return dbHandler.insert(tableName, values);
    }

    protected int update(ContentValues values, String whereClause, String[] whereArgs) {
        return dbHandler.update(tableName, values, whereClause, whereArgs);
    }

    protected int delete(String whereClause, String[] whereArgs) {
        return dbHandler.delete(tableName, whereClause, whereArgs);
    }

    protected Cursor query(String[] columns, String selection,
                           String[] selectionArgs, String orderBy) {
        return dbHandler.query(tableName, columns, selection, selectionArgs, orderBy);
    }
}
