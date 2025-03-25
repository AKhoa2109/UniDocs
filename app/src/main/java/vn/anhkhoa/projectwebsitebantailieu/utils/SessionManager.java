package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private UserResponse cachedUserResponse;
    private boolean isLoggedIn;


    // Private constructor
    private SessionManager(Context context) {
        Context appContext = context.getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Khởi tạo dữ liệu từ SharedPreferences khi tạo instance
        loadSessionFromStorage();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            // 🚨 Kiểm tra context có null không
            if (context == null) {
                throw new IllegalArgumentException("Context không được null!");
            }
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // ---------- Core Methods ----------
    private void loadSessionFromStorage() {
        isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            cachedUserResponse = new UserResponse(
                    sharedPreferences.getLong(KEY_ID, 0),
                    sharedPreferences.getString(KEY_NAME, null),
                    sharedPreferences.getString(KEY_EMAIL, null)
            );
        }
    }

    public void saveUser(UserResponse userResponse) {
        // Cập nhật dữ liệu trong RAM
        this.cachedUserResponse = userResponse;
        this.isLoggedIn = true;

        // Lưu vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ID, userResponse.getUserId());
        editor.putString(KEY_NAME, userResponse.getName());
        editor.putString(KEY_EMAIL, userResponse.getEmail());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public UserResponse getUser() {
        return cachedUserResponse;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void logout() {
        // Xóa dữ liệu trong RAM
        cachedUserResponse = null;
        isLoggedIn = false;

        // Xóa SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
