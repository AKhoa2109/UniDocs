package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilePickerUtils {

    public static final String TAG = "FilePickerUtils";
    public static final int REQUEST_CODE_PERMISSION = 1001;
    public static final int REQUEST_CODE_PICKER = 1002;

    public static final int PICKER_TYPE_IMAGE = 1;
    public static final int PICKER_TYPE_VIDEO = 2;
    public static final int PICKER_TYPE_MULTIPLE = 3;

    public interface FilePickerCallback {
        void onFilesPicked(List<File> files, int currentPickerType);
    }

    private final Fragment fragment;
    private final FilePickerCallback callback;
    private int pickerType = -1;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    public FilePickerUtils(Fragment fragment, FilePickerCallback callback) {
        this.fragment = fragment;
        this.callback = callback;
        initLauncher();
    }

    private void initLauncher() {
        launcher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleActivityResult(result.getData());
                    }
                }
        );

        permissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean granted = true;
                    for (Boolean isGranted : result.values()) {
                        if (!isGranted) {
                            granted = false;
                            break;
                        }
                    }
                    if (granted) {
                        launchPicker();
                    } else {
                        // Xử lý khi bị từ chối quyền nếu cần
                    }
                }
        );
    }

    public static String[] storagePermissionsPre33 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storagePermissions33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return storagePermissions33;
        } else {
            return storagePermissionsPre33;
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String perm : getStoragePermissions()) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void checkPermissionAndPick(int pickerType) {
        this.pickerType = pickerType;
        if (hasStoragePermission()) {
            launchPicker();
        } else {
            permissionLauncher.launch(getStoragePermissions());
        }
    }

    private void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        switch (pickerType) {
            case PICKER_TYPE_IMAGE:
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
            case PICKER_TYPE_VIDEO:
                intent.setType("video/*");
                break;
            case PICKER_TYPE_MULTIPLE:
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
            default:
                intent.setType("*/*");
                break;
        }
        launcher.launch(Intent.createChooser(intent, "Select File"));
    }

    private void handleActivityResult(Intent data) {
        List<File> files = new ArrayList<>();
        if (data != null) { // Kiểm data không null
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    if (uri != null) { // Thêm kiểm tra uri != null
                        String path = RealPathUtil.getRealPath(fragment.requireContext(), uri);
                        if (path != null) {
                            files.add(new File(path));
                        }
                    }
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                String path = RealPathUtil.getRealPath(fragment.requireContext(), uri);
                if (path != null) {
                    files.add(new File(path));
                }
            }
        }

        if (callback != null) callback.onFilesPicked(files, pickerType);
    }
}
