package vn.anhkhoa.projectwebsitebantailieu.utils;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import vn.anhkhoa.projectwebsitebantailieu.R;

public class LoadingDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(R.layout.dialog_loading);
        setCancelable(false);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageView imgGif = getDialog().findViewById(R.id.img_book_animation);

        // Load GIF từ raw resource
        Glide.with(this)
                .asGif()
                .load(R.raw.book_animation) // Đặt file GIF vào thư mục res/raw
                .into(imgGif);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Dọn dẹp tài nguyên khi dialog đóng
        ImageView imgGif = getDialog().findViewById(R.id.img_book_animation);
        Glide.with(this).clear(imgGif);
    }
}
