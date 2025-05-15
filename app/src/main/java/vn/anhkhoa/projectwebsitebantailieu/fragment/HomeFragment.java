package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonConfig;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.activity.SearchActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.BannerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.CategoryAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.CartDao;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentHomeBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.NotificationHelper;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.StompManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CartDao cartDao;
    private SessionManager sessionManager;
    private Handler sliderHandler = new Handler();
    private List<String> bannerImages = new ArrayList<>();
    private Runnable sliderRunnable;
    List<DocumentDto> documentDtos = new ArrayList<>();
    List<CategoryDto> categoryDtos = new ArrayList<>();
    private Skeleton skeleton;
    private StompClient stompClient;
    private Disposable notifSub;
    private NotificationDao notificationDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = binding.getRoot();
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = SessionManager.getInstance(requireContext());
        binding.edtSearch.setHint("Tìm kiếm tài liệu...");
        binding.edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        cartDao = new CartDao(getContext());
        notificationDao = new NotificationDao(requireContext());
        connectWebSocket();
        updateBadgeCount();
        int cartCount = cartDao.getCountCart(sessionManager.getUser().getUserId());
        binding.tvCartBadge.setText(String.valueOf(cartCount));
        initBannerImages();
        setupViewPager();
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rcvDocument.setLayoutManager(linearLayoutManager);
        LinearLayoutManager  linearLayoutCateManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        binding.rcvCategory.setLayoutManager(linearLayoutCateManager);
        documentDtos = new ArrayList<>();
        callApiGetListDocument();
        callApiGetListCategory();
        handlderImgCartClick();
        handlerImgNotificationClick();
    }

    private void callApiGetListDocument(){
        ApiService.apiService.getListDocument(sessionManager.getUser().getUserId()).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                ResponseData<List<DocumentDto>> data = response.body();

                if(data == null)
                {
                    Toast.makeText(getContext(), "Dữ liệu trả về null", Toast.LENGTH_SHORT).show();
                    return;
                }
                documentDtos.clear();
                documentDtos= data.getData();
                DocumentAdapter documentAdapter = new DocumentAdapter(getContext(),documentDtos);
                binding.rcvDocument.setAdapter(documentAdapter);
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                skeleton.showOriginal();
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callApiGetListCategory(){
        ApiService.apiService.getListCategory().enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<CategoryDto>>> call, Response<ResponseData<List<CategoryDto>>> response) {
                if(response.isSuccessful()){
                    ResponseData<List<CategoryDto>> data = response.body();
                    if(data == null)
                    {
                        Toast.makeText(getContext(), "Dữ liệu trả về null", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    categoryDtos.clear();
                    categoryDtos= data.getData();
                    CategoryAdapter categoryAdapter = new CategoryAdapter(categoryDtos);
                    binding.rcvCategory.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                skeleton.showOriginal();
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initBannerImages() {
        bannerImages.clear();
        // Thêm dữ liệu ảnh mẫu (thay thế bằng dữ liệu thực tế)
        bannerImages.add("https://as2.ftcdn.net/jpg/04/32/48/41/1000_F_432484133_WnmdDXCWu7i2tR90K7flMtnJ4zOOfnM2.jpg");
        bannerImages.add("https://static.vecteezy.com/system/resources/previews/023/107/446/non_2x/promo-sale-banner-with-reading-stack-of-books-wooden-letter-tiles-school-books-pile-world-book-day-bookstore-bookshop-library-book-lover-bibliophile-education-a4-for-poster-cover-vector.jpg");
        bannerImages.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSk2tzj10CnY8tcE9svBC2VZBES3gsyZCAm8g&s");
    }

    private void setupViewPager() {
        // Khởi tạo Adapter
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        binding.viewPager.setAdapter(bannerAdapter);

        // Kết nối WormDotsIndicator với ViewPager2
        binding.dotsIndicator.attachTo(binding.viewPager);

        // Tự động chuyển slide
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // 3 giây
            }
        });

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = binding.viewPager.getCurrentItem();
                if (currentPosition == bannerImages.size() - 1) {
                    binding.viewPager.setCurrentItem(0);
                } else {
                    binding.viewPager.setCurrentItem(currentPosition + 1);
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    private void handlderImgCartClick(){
        binding.imgViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getContext() instanceof MainActivity){
                    ((MainActivity) getContext()).openCartFragment();
                }
            }
        });
    }

    private void handlerImgNotificationClick(){
        binding.imgViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getContext() instanceof MainActivity){
                    ((MainActivity) getContext()).openNotificationFragment();
                }
            }
        });
    }


    private void connectWebSocket() {
        String WS_URL = "ws://"+ApiService.ipAddress+"/ws/websocket";
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        // Lifecycle – chỉ log để debug
        Disposable lifecycleDisp = stompClient.lifecycle()
                .subscribe(evt -> {
                    if (evt.getType() == LifecycleEvent.Type.OPENED) {
                        subscribeToNotifications();
                    } else if (evt.getType() == LifecycleEvent.Type.ERROR) {
                        Log.e("Stomp", "Connection error", evt.getException());
                    }
                });
        compositeDisposable.add(lifecycleDisp);
        stompClient.connect();
    }

    private void subscribeToNotifications() {
        long userId = SessionManager.getInstance(requireContext()).getUser().getUserId();
        String topic = "/queue/notifications/" + userId;
        notifSub = stompClient.topic(topic)
                .subscribe(this::onNewNotification, throwable ->
                        Log.e("Stomp", "Subscribe error", throwable)
                );
    }

    private void onNewNotification(StompMessage topicMessage) {
        // 1. Parse payload
        NotificationDto dto = new Gson().fromJson(topicMessage.getPayload(), NotificationDto.class);

        // 2. Lưu vào SQLite
        //    - chuyển từ model của WebSocket (nếu cần map lại) sang model NotificationDto của local
        NotificationDto local = new NotificationDto(null,dto.getUserId(),dto.getTitle(),dto.getContent(),dto.getType(),dto.getCreatedAt(),false);
        notificationDao.addNotification(local);
        NotificationHelper.showNotification(
                requireContext(),
                NotificationHelper.Channel.DOWNLOAD,
                dto.getTitle(),
                dto.getContent());
        // 3. Cập nhật badge UI
        updateBadgeCount();
    }

    private void updateBadgeCount() {
        // Lấy tổng số chưa đọc
        int countUnread = notificationDao.getNotifications(sessionManager.getUser().getUserId())
                .stream()
                .filter(n -> !n.isRead())
                .mapToInt(n -> 1)
                .sum();

        // Cập nhật badge (chạy trên UI thread)
        requireActivity().runOnUiThread(() -> {
            if (countUnread > 0) {
                binding.tvNotificationBadge.setVisibility(View.VISIBLE);
                binding.tvNotificationBadge.setText(String.valueOf(countUnread));
            } else {
                binding.tvNotificationBadge.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notifSub != null && !notifSub.isDisposed()) {
            notifSub.dispose();
        }
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }


}