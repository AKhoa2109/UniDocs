package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vn.anhkhoa.projectwebsitebantailieu.R;

public class ToastUtils {
    /**
     * hien thi toast tuy chinh
     * @param context
     * @param message
     */
    public static void show(Context context, String message) {
        // 1. Tạo layout tùy chỉnh cho Toast
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom_layout, null);
        // 2. Tìm và thay đổi nội dung của TextView
        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);
        // 3. Tạo Toast với layout tùy chỉnh
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }
}
