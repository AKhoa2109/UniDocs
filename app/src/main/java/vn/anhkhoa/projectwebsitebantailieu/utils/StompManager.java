package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;

public class StompManager {
    private static StompClient sClient;
    private static final String TAG = "StompManager";

    public static void connect(String websocketUrl) {
        if (sClient != null && sClient.isConnected()) return;
        sClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl);
        sClient.connect();
    }

    /**
     * Subscribe vào queue riêng của user hiện tại
     * @param userId : id của bạn
     */
    public static Disposable subscribeToNotifications(long userId, OnNotificationListener listener) {
        String topic = "/queue/notifications/" + userId;
        return sClient.topic(topic)
                .subscribe(message -> {
                    String payload = message.getPayload();
                    NotificationDto dto = new Gson().fromJson(payload, NotificationDto.class);
                    listener.onNotification(dto);
                }, throwable -> Log.e(TAG, "Error on STOMP", throwable));
    }

    public interface OnNotificationListener {
        void onNotification(NotificationDto dto);
    }
}
