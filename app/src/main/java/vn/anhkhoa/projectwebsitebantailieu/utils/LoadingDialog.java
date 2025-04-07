package vn.anhkhoa.projectwebsitebantailieu.utils;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import java.lang.ref.WeakReference;

import vn.anhkhoa.projectwebsitebantailieu.R;

public class LoadingDialog {
    private static WeakReference<Dialog> loadingDialogRef;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final int DEFAULT_TIMEOUT = 10000;
    private static Runnable dismissRunnable;

    public static void showLoading(Context context) {
        showLoading(context, DEFAULT_TIMEOUT);
    }

    public static void showLoading(Context context, int timeoutMillis) {
        dismissLoading(); // Hủy loading trước đó nếu có

        if (context == null) return;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        loadingDialogRef = new WeakReference<>(dialog);
        dialog.show();

        dismissRunnable = () -> dismissLoading();
        handler.postDelayed(dismissRunnable, timeoutMillis);
    }

    public static void dismissLoading() {
        // Remove pending callbacks
        if (dismissRunnable != null) {
            handler.removeCallbacks(dismissRunnable);
            dismissRunnable = null;
        }

        if (loadingDialogRef != null && loadingDialogRef.get() != null) {
            Dialog dialog = loadingDialogRef.get();
            if (dialog != null && dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException e) {
                }
            }
            loadingDialogRef.clear();
            loadingDialogRef = null;
        }
    }
}
