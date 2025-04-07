package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtils {
    /**
     * Tải file từ URL về thư mục Download
     *
     * @param context  Context gọi đến (nên là Activity hoặc Fragment có getContext())
     * @param url      Đường dẫn file cần tải
     * @param fileName Tên file khi lưu (ví dụ: "image.jpg", "document.pdf")
     */
    public static void downloadFile(Context context, String url, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Đang tải xuống: " + fileName);
            request.setDescription("Đang tải tệp tin từ internet...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            // Thêm header nếu cần
            request.allowScanningByMediaScanner();
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể thêm Toast hoặc log lỗi
        }
    }

    public static void downloadAndSaveImage(Context context, String imageUrl, String fileName) {
        new Thread(() -> {
            try {
                // Tải ảnh từ URL
                Bitmap bitmap = getBitmapFromUrl(imageUrl);
                if (bitmap == null) {
                    showToast(context, "Không thể tải ảnh");
                    return;
                }

                // Lưu ảnh vào thư viện, thư mục 'unidocs'
                saveImageToGallery(context, bitmap, fileName);

            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, "Lỗi khi tải ảnh");
            }
        }).start();
    }

    private static Bitmap getBitmapFromUrl(String src) throws IOException {
        URL url = new URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();

        try (InputStream input = connection.getInputStream()) {
            return BitmapFactory.decodeStream(input);
        }
    }

    private static void saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/UniDocs");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                showToast(context, "Đã lưu ảnh vào thư viện (UniDocs)");
            } catch (IOException e) {
                e.printStackTrace();
                showToast(context, "Lỗi khi lưu ảnh");
            }
        }
    }

    private static void showToast(Context context, String message) {
        android.os.Handler handler = new android.os.Handler(context.getMainLooper());
        handler.post(() -> ToastUtils.show(context, message));
    }

}
