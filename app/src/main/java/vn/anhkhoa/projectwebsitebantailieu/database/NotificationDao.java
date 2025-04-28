package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;

public class NotificationDao extends BaseDao{
    public static final String TABLE_NAME = "Notification";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_CREATED_DATE = "CreatedDate";
    public static final String COLUMN_IS_READ = "IsRead";
    public static final String COLUMN_USER_ID = "UserId";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_CREATED_DATE + " TEXT NOT NULL, " +
                    COLUMN_IS_READ + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_USER_ID + " INTEGER NOT NULL" +
                    ");";

    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public NotificationDao(Context context) {
        super(context, TABLE_NAME);
    }

    public long addNotification(NotificationDto notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, notification.getTitle());
        values.put(COLUMN_CONTENT, notification.getContent());
        values.put(COLUMN_TYPE, notification.getType().toString());
        values.put(COLUMN_CREATED_DATE, LocalDateTime.now().format(fmt));
        values.put(COLUMN_IS_READ, 0);
        values.put(COLUMN_USER_ID, notification.getUserId());
        return insert(values);
    }

    public int deleteNotification(long notifiId) {
        String where = COLUMN_ID + " = ?";
        String[] args = new String[]{ String.valueOf(notifiId) };
        return delete(where, args);
    }

    public List<NotificationDto> getNotifications(Long userId) {
        return getNotificationsByTypeInternal(null, userId);
    }

    public List<NotificationDto> getNotificationsByType(NotificationType type, Long userId) {
        return getNotificationsByTypeInternal(type, userId);
    }


    public int getCount() {
        Cursor c = query(
                new String[]{ COLUMN_ID },
                null,
                null,
                null       
        );
        int count = c.getCount();
        c.close();
        return count;
    }

    public int getCountByTypeAndUserId(NotificationType type,Long userId) {
        String selection = COLUMN_TYPE + " = ? AND " + COLUMN_USER_ID + " = ? AND " + COLUMN_IS_READ + " = ?";
        String[] args = new String[]{ type.toString() , userId.toString(), "0"};
        Cursor c = query(
                new String[]{ COLUMN_ID },
                selection,
                args,
                null
        );
        int count = c.getCount();
        c.close();
        return count;
    }

    public boolean existsNotification(NotificationDto notification) {
        String selection = COLUMN_TITLE + " = ? AND " + COLUMN_CONTENT + " = ?" + " AND " + COLUMN_TYPE + " = ?";
        String[] args = new String[] { notification.getTitle(), notification.getContent() , notification.getType().toString()};

        Cursor cursor = query(
                new String[]{ COLUMN_ID },
                selection,
                args,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int updateIsRead(long notiId, boolean isRead) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, isRead ? 1 : 0);

        String where = COLUMN_ID + " = ?";
        String[] args = new String[]{String.valueOf(notiId)};

        return update(values, where, args);
    }

    public int markAllAsRead() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        return update(values, null, null);
    }

    private List<NotificationDto> getNotificationsByTypeInternal(NotificationType type, Long userId) {
        List<NotificationDto> list = new ArrayList<>();
        String selection = null;
        String[] args = null;
        if (type != null) {
            selection = COLUMN_TYPE + " = ? AND " + COLUMN_USER_ID + " = ?";
            args = new String[]{ type.toString() , userId.toString()};
        }
        String[] columns = new String[]{
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_CONTENT,
                COLUMN_TYPE,
                COLUMN_CREATED_DATE,
                COLUMN_IS_READ,
                COLUMN_USER_ID
        };
        String orderBy = COLUMN_CREATED_DATE + " DESC";
        Cursor c = query(columns, selection, args, orderBy);
        while (c.moveToNext()) {
            NotificationDto dto = new NotificationDto();
            dto.setNotiId(c.getLong(c.getColumnIndexOrThrow(COLUMN_ID)));
            dto.setTitle(c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE)));
            dto.setContent(c.getString(c.getColumnIndexOrThrow(COLUMN_CONTENT)));
            dto.setType(NotificationType.valueOf(
                    c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE))
            ));
            dto.setCreatedAt(LocalDateTime.parse(
                    c.getString(c.getColumnIndexOrThrow(COLUMN_CREATED_DATE)),
                    fmt
            ));
            dto.setRead(c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);
            dto.setServerId(c.getLong(c.getColumnIndexOrThrow(COLUMN_USER_ID)));
            list.add(dto);
        }
        c.close();
        return list;
    }
}
