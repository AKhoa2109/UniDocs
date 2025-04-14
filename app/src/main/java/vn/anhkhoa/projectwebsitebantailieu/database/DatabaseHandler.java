package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "databaseSQLite.db";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHandler instance;

    // constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton instance
    public static synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistorySearchDao.CREATE_TABLE);
        db.execSQL(CartDao.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS HistorySearch");*/
    }

    //region CRUD Operations
    public long insert(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.update(table, values, whereClause, whereArgs);
        } finally {
            db.close();
        }
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(table, whereClause, whereArgs);
        } finally {
            db.close();
        }
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                table,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );
    }
    //endregion

    // Transaction Support
    public void executeTransaction(Runnable operations) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            operations.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
