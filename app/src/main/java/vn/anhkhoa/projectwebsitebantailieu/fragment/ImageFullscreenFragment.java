package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentChatBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentImageFullscreenBinding;
import vn.anhkhoa.projectwebsitebantailieu.utils.DownloadUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFullscreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFullscreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentImageFullscreenBinding binding;
    private static final String ARG_URL = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImageFullscreenFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ImageFullscreenFragment newInstance(String imageUrl) {
        ImageFullscreenFragment fragment = new ImageFullscreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageFullscreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = view.findViewById(R.id.fragment_image_fullscreen);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //load anh
        String url = getArguments().getString(ARG_URL);
        Glide.with(this).load(url).into(binding.photoView);

        // Lắng nghe sự kiện cho các nút
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.btnDownload.setOnClickListener(v -> downloadImage());
        binding.btnMore.setOnClickListener(v -> onMoreClicked(v));

    }

    private void onBackPressed() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // Tải ảnh từ URL về bộ nhớ
    private void downloadImage() {
        String url = getArguments().getString(ARG_URL);
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        DownloadUtils.downloadAndSaveImage(requireContext(), url,  fileName);
    }

    // Nút More
    private void onMoreClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_image_more);

        popupMenu.setOnMenuItemClickListener(item ->{
            if (item.getItemId() == R.id.item_view_original) {
                // Xử lý "Xem tin nhắn gốc"
                showOriginalMessage();
                return true;
            } else if (item.getItemId() == R.id.item_edit_image) {
                // Xử lý "Chỉnh sửa ảnh"
                editImage();
                return true;
            } else if (item.getItemId() == R.id.item_save_image) {
                downloadImage();
                return true;
            } else if (item.getItemId() == R.id.item_share) {
                // Xử lý "Chia sẻ"
                shareImage();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();

    }

    private void showOriginalMessage() {
        ToastUtils.show(getContext(), "show original message");
    }

    private void editImage() {
        ToastUtils.show(requireContext(),"Chỉnh sửa ảnh");
    }

    private void shareImage() {
        ToastUtils.show(requireContext(), "ShareImage");
    }
}