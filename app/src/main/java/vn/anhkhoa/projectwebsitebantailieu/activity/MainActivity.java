package vn.anhkhoa.projectwebsitebantailieu.activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.fragment.DiscountFragment;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ActivityMainBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
import vn.anhkhoa.projectwebsitebantailieu.fragment.AccountFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.CartFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ChatFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ConversationListFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.DocumentDetailFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.HomeFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.NotificationChildFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.NotificationFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.OrderDetailFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.OrderStatusFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.PostFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.PreviewFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.SearchShopFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.UserDetailInfoFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.VoucherFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.FileDocument;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.receiver.NetworkChangeReceiver;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ShopFragment shopFragment;
    private PostFragment postFragment;
    private AccountFragment accountFragment;
    private ConversationListFragment conversationListFragment;
    private OnBackPressedCallback onBackPressedCallback;
    private NetworkChangeReceiver networkReceiver;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
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
        sessionManager = new SessionManager(this);

        databaseHandler = DatabaseHandler.getInstance(this);
        /*networkReceiver = new NetworkChangeReceiver();
        registerNetworkReceiver();*/
        // Khởi tạo các fragment
        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        postFragment = new PostFragment();
        accountFragment = new AccountFragment();
        conversationListFragment = new ConversationListFragment();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Thêm fragment mặc định
        showFragment(homeFragment, "home");

//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//
//            boolean isLoggedIn = sessionManager.isLoggedIn();
//
//            // Gọi hàm showFragment thay vì xử lý từng Fragment trực tiếp
//            if (id == R.id.home) {
//                showFragment(homeFragment, "home");
//            } else if (id == R.id.shop) {
//                showFragment(shopFragment, "shop");
//            } else if (id == R.id.post){
//                showFragment(postFragment, "post");
//            } else if (id == R.id.chat) {
//                showFragment(conversationListFragment, "chat");
//            } else if (id == R.id.account) {
//                showFragment(accountFragment, "account");
//            }
//
//            return true;
//        });
//
//        setupBackPressedCallback();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            boolean isLoggedIn = sessionManager.isLoggedIn();

            // Gọi hàm showFragment thay vì xử lý từng Fragment trực tiếp
            if (id == R.id.home) {
                showFragment(homeFragment, "home");
                return true;
            }else if (!isLoggedIn){
                // Nếu chưa đăng nhập và không phải Home, chuyển sang màn hình đăng nhập
                showLoginDialogOrActivity();
                // Giữ nguyên tab Home được chọn
                binding.bottomNavigationView.setSelectedItemId(R.id.home);
                return false;
            }

            if (id == R.id.shop) {
                showFragment(shopFragment, "shop");
            } else if (id == R.id.post){
                showFragment(postFragment, "post");
            } else if (id == R.id.chat) {
                showFragment(conversationListFragment, "chat");
            } else if (id == R.id.account) {
                showFragment(accountFragment, "account");
            }

            return true;
        });

        setupBackPressedCallback();


    }

//    private void showLoginDialogOrActivity() {
//        Intent intent = new Intent(this, SignIn.class);
//        startActivity(intent);
//    }

    //**
    public void showLoginDialogOrActivity() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng chức năng này!")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(this, SignIn.class));
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    //goi ham nay khi can show fragment
    public void showFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // 1) Ẩn tất cả các fragment đã thêm
        for (Fragment f : fm.getFragments()) {
            if (f.isAdded()) {
                ft.hide(f);
            }
        }

        // 2) Thay thế fragment nếu chưa add hoặc thay thế fragment hiện tại
        if (!fragment.isAdded()) {
            ft.add(R.id.frame_layout, fragment, tag);  // Sử dụng replace thay vì add
        } else {
            ft.show(fragment);  // Chỉ hiển thị nếu fragment đã tồn tại
        }

        // 3) Thêm vào back stack nếu cần
        ft.addToBackStack(tag);  // Thêm vào back stack để có thể quay lại fragment trước đó

        // 4) Commit
        ft.commit();
    }


//    private void setupBackPressedCallback() {
//        onBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
//                // Nếu đang ở ChatFragment, xử lý tùy chỉnh
//                if (currentFragment instanceof ChatFragment || currentFragment instanceof DocumentDetailFragment
//                    || currentFragment instanceof CartFragment) {
//                    binding.bottomNavigationView.setVisibility(View.VISIBLE);
//                    getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
//                } else {
//                    // Mặc định: Thoát Activity nếu không có Fragment nào trong back stack
//                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                        finish();
//                    } else {
//                        remove(); // Hủy callback và để hệ thống xử lý
//                    }
//                }
//            }
//        };
//
//        // Đăng ký callback với dispatcher
//        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
//    }

    //nếu fragment hiện tại là Home, Shop, Post, Chat, Account thì hiện lại thanh nav
    private void setupBackPressedCallback() {
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                // Nếu đang ở các fragment đặc biệt thì show nav
                if (currentFragment instanceof ChatFragment
                        || currentFragment instanceof DocumentDetailFragment
                        || currentFragment instanceof CartFragment) {
                    binding.bottomNavigationView.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().popBackStack();
                } else {
                    // Nếu đang ở các fragment chính (home, shop, post, account)
                    if (currentFragment instanceof HomeFragment
                            || currentFragment instanceof ShopFragment
                            || currentFragment instanceof PostFragment
                            || currentFragment instanceof AccountFragment
                            || currentFragment instanceof ConversationListFragment) {
                        binding.bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                    // Mặc định: Thoát Activity nếu không có Fragment nào trong back stack
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        finish();
                    } else {
                        remove(); // Hủy callback và để hệ thống xử lý
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    public void openChatFragment(ConversationOverviewDto conversation) {
        // Tạo ChatFragment và truyền dữ liệu
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable("conversationOverviewDto", conversation);
        chatFragment.setArguments(args);

        // Thực hiện Fragment Transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, chatFragment)
                .addToBackStack("chat")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openDiscountFragment(){
        DiscountFragment discountFragment = new DiscountFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, discountFragment)
                .addToBackStack("discountFragment")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    //** detail fragment
    public void openUserDetailFragment(UserRegisterRequest user){
        UserDetailInfoFragment userDetailInfoFragment = new UserDetailInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        userDetailInfoFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, userDetailInfoFragment)
                .addToBackStack("userDetailFragment")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        DocumentDto doc = (DocumentDto) intent.getSerializableExtra("documentDto");
        if (doc != null) {
            openProductDetailFragment(doc);
        }
    }


    public void openProductDetailFragment(DocumentDto document){
        DocumentDetailFragment documentDetailFragment = new DocumentDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("document", document);
        documentDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, documentDetailFragment)
                .addToBackStack("productDetail")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openCartFragment(){
        if(sessionManager.getUser() == null){
            showLoginDialogOrActivity();
            return;
        }
        CartFragment cartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, cartFragment)
                .addToBackStack("cart")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openVoucherFragment(List<CartDto> carts){
        VoucherFragment fragment = VoucherFragment.newInstance();
        Bundle args = new Bundle();
        args.putSerializable("cart", new ArrayList<>(carts));
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack("voucher")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openSearchShopFragment(){
        SearchShopFragment fragment = new SearchShopFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack("search_shop")
                .commit();
    }
    public void openNotificationChildFragment(List<NotificationDto> notificationDtos, NotificationType type){
        NotificationChildFragment fragment = NotificationChildFragment.newInstance();
        Bundle args = new Bundle();
        args.putSerializable("notifications", new ArrayList<>(notificationDtos));
        args.putSerializable("type", type);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack("notification_child")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openNotificationFragment(){
        NotificationFragment fragment = new NotificationFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack("notification")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openFileDocumentFragment(FileDocument fileDocument, DocumentDto documentDto){
        PreviewFragment fragment = PreviewFragment.newInstance(fileDocument, documentDto);
        Bundle args = new Bundle();
        args.putSerializable("file", fileDocument);
        args.putSerializable("document", documentDto);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack("file_document")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void openOrderStatusFragment(OrderStatus orderStatus){
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putSerializable("status", orderStatus);
        orderStatusFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, orderStatusFragment)
                .addToBackStack("orderStatusFragment")
                .commit();
    }

    public void openOrderDetailFragment(OrderDtoRequest orderDtoRequest){
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", orderDtoRequest);
        orderDetailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, orderDetailFragment)
                .addToBackStack("orderDetailFragment")
                .commit();

        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    //hien thi nav
    public void showBottomNavigation(){
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }


    // Hủy callback khi Activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
        }
        /*unregisterReceiver(networkReceiver);*/
    }
}