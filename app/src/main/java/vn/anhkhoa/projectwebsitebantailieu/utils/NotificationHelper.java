package vn.anhkhoa.projectwebsitebantailieu.utils;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import vn.anhkhoa.projectwebsitebantailieu.R;

public class NotificationHelper {
    public enum Channel {
        CART(
                "cart_channel",
                "Cart Notifications",
                "Thông báo khi thêm sản phẩm vào giỏ hàng",
                R.drawable.cart_notifi
        ),
        ORDER(
                "order_channel",
                "Order Notifications",
                "Thông báo về trạng thái đơn hàng",
                R.drawable.order_notifi
        );

        private final String id;
        private final String name;
        private final String description;
        private final int iconRes;

        Channel(String id, String name, String description, @DrawableRes int iconRes) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.iconRes = iconRes;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getIconRes() { return iconRes; }
    }


    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;

            for (Channel channel : Channel.values()) {
                NotificationChannel nc = new NotificationChannel(
                        channel.getId(),
                        channel.getName(),
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                nc.setDescription(channel.getDescription());
                manager.createNotificationChannel(nc);
            }
        }
    }

    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    public static void showNotification(Context context,
                                        Channel channel,
                                        String title,
                                        String content) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                Log.w("NotificationHelper", "Missing POST_NOTIFICATIONS permission");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel.getId())
                .setSmallIcon(channel.getIconRes())
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManagerCompat.from(context)
                    .notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException ex) {

            Log.e("NotificationHelper", "Failed to post notification", ex);
        }
    }
}

