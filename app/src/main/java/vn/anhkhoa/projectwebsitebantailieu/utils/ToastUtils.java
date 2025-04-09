package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vn.anhkhoa.projectwebsitebantailieu.R;

public class ToastUtils {

    // Gọi lại phương thức chính với màu mặc định là null
    public static void show(Context context, String message) {
        show(context, message, null);
    }

    /**
     * hien thi toast tuy chinh có màu
     * @param context
     * @param message
     */
    public static void show(Context context, String message, String colorHex) {
        // 1. Tạo layout tùy chỉnh cho Toast
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom_layout, null);
        // 2. Tìm và thay đổi nội dung của TextView
        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

        // 3. Nếu có tham số màu hex, thay đổi màu nền của Toast
        if (colorHex != null && !colorHex.isEmpty()) {
            try {
                int backgroundColor = Color.parseColor(colorHex);
                // Tạo ShapeDrawable để bo góc và thay đổi màu nền
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(backgroundColor);  // Thay đổi màu nền
                drawable.setCornerRadius(16);

                // Áp dụng drawable này làm nền của layout
                layout.setBackground(drawable);
            } catch (IllegalArgumentException e) {
                // Nếu mã hex không hợp lệ, giữ nguyên màu nền mặc định
                e.printStackTrace();
            }
        }

        // 3. Tạo Toast với layout tùy chỉnh
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }
}
