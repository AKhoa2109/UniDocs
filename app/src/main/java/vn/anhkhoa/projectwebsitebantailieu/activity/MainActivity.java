package vn.anhkhoa.projectwebsitebantailieu.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityMainBinding;
import vn.anhkhoa.projectwebsitebantailieu.fragment.AccountFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ChatFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ConversationListFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.HomeFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ShopFragment shopFragment;
    private AccountFragment accountFragment;
    private ConversationListFragment conversationListFragment;

    private OnBackPressedCallback onBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các fragment
        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        accountFragment = new AccountFragment();
        conversationListFragment = new ConversationListFragment();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thêm fragment mặc định
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, homeFragment)
                .commit();
        binding.bottomNavigationView.setOnItemSelectedListener(item->{
            int id = item.getItemId();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Ẩn tất cả các fragment trước khi hiển thị fragment được chọn
            if (homeFragment.isAdded()) transaction.hide(homeFragment);
            if (shopFragment.isAdded()) transaction.hide(shopFragment);
            if (accountFragment.isAdded()) transaction.hide(accountFragment);
            if (conversationListFragment.isAdded()) transaction.hide(conversationListFragment);

            if (id == R.id.home) {
                if (!homeFragment.isAdded()) {
                    transaction.add(R.id.frame_layout, homeFragment);
                }
                transaction.show(homeFragment);
            } else if (id == R.id.shop) {
                if (!shopFragment.isAdded()) {
                    transaction.add(R.id.frame_layout, shopFragment);
                }
                transaction.show(shopFragment);
            } else if (id == R.id.chat) {
                if (!conversationListFragment.isAdded()) {
                    transaction.add(R.id.frame_layout, conversationListFragment);
                }
                transaction.show(conversationListFragment);
            }else if (id == R.id.account) {
                if (!accountFragment.isAdded()) {
                    transaction.add(R.id.frame_layout, accountFragment);
                }
                transaction.show(accountFragment);
            }
            transaction.commit();
            return true;
        });

        setupBackPressedCallback();
    }

    private void setupBackPressedCallback() {
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                // Nếu đang ở ChatFragment, xử lý tùy chỉnh
                if (currentFragment instanceof ChatFragment) {
                    binding.bottomNavigationView.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
                } else {
                    // Mặc định: Thoát Activity nếu không có Fragment nào trong back stack
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        finish();
                    } else {
                        remove(); // Hủy callback và để hệ thống xử lý
                    }
                }
            }
        };

        // Đăng ký callback với dispatcher
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    public void openChatFragment() {
        // Tạo ChatFragment và truyền dữ liệu
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("conversationId", "1");
        chatFragment.setArguments(args);

        // Thực hiện Fragment Transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, chatFragment)
                .addToBackStack("chat") // Thêm vào Back Stack
                .commit();

        // Ẩn BottomNavigationView (tuỳ chọn)
        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    // Hủy callback khi Activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
        }
    }
}