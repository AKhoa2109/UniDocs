package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {
    private DateTimeUtils() {}
    public static String formatTime(LocalDateTime dateTime) {
        if(dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
            return dateTime.format(formatter);
        }
        return "";
    }
}
