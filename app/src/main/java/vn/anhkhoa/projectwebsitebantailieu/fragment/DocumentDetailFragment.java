package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.library.foysaltech.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.library.foysaltech.smarteist.autoimageslider.SliderView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DetailPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SliderAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.CartDao;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentDocumentDetailBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentImageDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.AnimUtils;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;
import vn.anhkhoa.projectwebsitebantailieu.utils.NetworkUtil;
import vn.anhkhoa.projectwebsitebantailieu.utils.NotificationHelper;
import vn.anhkhoa.projectwebsitebantailieu.utils.NumberFormatter;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.SyncManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocumentDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocumentDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentDocumentDetailBinding binding;
    private DocumentDto documentDto;
    private CartDao cartDao;
    private SliderAdapter sliderAdapter;
    private List<DocumentImageDto> images;
    private SessionManager sessionManager;
    private NotificationDao notificationDao;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DocumentDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DocumentDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocumentDetailFragment newInstance(String param1, String param2) {
        DocumentDetailFragment fragment = new DocumentDetailFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentDocumentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = view.findViewById(R.id.fragment_document_detail_layout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            documentDto = (DocumentDto) bundle.getSerializable("document");
        }
        cartDao = new CartDao(getContext());
        sessionManager = SessionManager.getInstance(requireContext());
        images = new ArrayList<>();
        notificationDao = new NotificationDao(requireContext());
        NotificationHelper.init(requireContext());

        if(sessionManager.getUser() != null){
            handlerCartCount(sessionManager.getUser().getUserId());

        }
        getApiImageByDocumentId(documentDto.getDocId());
        getApiDocumentDetail(documentDto.getDocId());
        handlerAddToCart();

        binding.btnBack.setOnClickListener(v->{requireActivity().onBackPressed();});

        binding.imgViewCart.setOnClickListener(v->{
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openCartFragment();
            }
        });

        binding.btnChat.setOnClickListener(v->{

        });
    }

    private void getApiDocumentDetail(Long id){
        ApiService.apiService.getDocumentDetail(id).enqueue(new Callback<ResponseData<DocumentDto>>() {
            @Override
            public void onResponse(Call<ResponseData<DocumentDto>> call, Response<ResponseData<DocumentDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.d("API_RESPONSE", new Gson().toJson(response.body()));
                    ResponseData<DocumentDto> data = response.body();
                    documentDto = data.getData();
                    bindDataToView(documentDto);
                    handlerQuantity(documentDto);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<DocumentDto>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getApiImageByDocumentId(Long id){
        ApiService.apiService.getAllImageByDocumentId(id).enqueue(new Callback<ResponseData<List<DocumentImageDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentImageDto>>> call, Response<ResponseData<List<DocumentImageDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    ResponseData<List<DocumentImageDto>> data = response.body();
                    images = data.getData();
                    handlerSliderView(images);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentImageDto>>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindDataToView(DocumentDto documentDto){
        String sellPrice = CurrentFormatter.format(documentDto.getSellPrice());
        String originalPrice = CurrentFormatter.format(documentDto.getOriginalPrice()) ;
        String sold = NumberFormatter.formatterNum(documentDto.getTotalSold()) + " lượt mua";
        binding.tvProductName.setText(documentDto.getDocName());
        binding.tvSellPrice.setText(sellPrice);
        binding.tvOriginalPrice.setText(originalPrice);
        binding.tvNummBuy.setText(sold);
        DetailPagerAdapter adapter = new DetailPagerAdapter(requireActivity(),documentDto);
        binding.viewPager2.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Mô tả");
                            break;
                        case 1:
                            tab.setText("Đánh giá");
                            break;
                        case 2:
                            tab.setText("Liên quan");
                            break;
                    }
                }).attach();
    }

    private void handlerSliderView(List<DocumentImageDto> images){
        sliderAdapter = new SliderAdapter(getContext(), images);
        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.colorSecondary));
        binding.imageSlider.setIndicatorUnselectedColor(Color.BLACK);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }

    private void handlerQuantity(DocumentDto documentDto){
        int maxQuantity = documentDto.getMaxQuantity();
        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = binding.edtQuantity.getText().toString();
                Integer quantityInt = Integer.parseInt(quantity);
                if(quantityInt>=maxQuantity){
                    binding.edtQuantity.setText(String.valueOf(maxQuantity));
                    ToastUtils.show(getContext(),"Đã vượt quá số lượng tối đa");
                }
                else{
                    int newQuantity = Integer.parseInt(quantity) + 1;
                    binding.edtQuantity.setText(String.valueOf(newQuantity));
                }

            }
        });
        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = binding.edtQuantity.getText().toString();
                Integer quantityInt = Integer.parseInt(quantity);
                if(quantityInt<=0){
                    binding.edtQuantity.setText("0");
                }
                else{
                    int newQuantity = quantityInt - 1;
                    binding.edtQuantity.setText(String.valueOf(newQuantity));
                }
            }
        });
    }

    private void handlerAddToCart(){
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getUser() == null){
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).showLoginDialogOrActivity();
                    }
                }

                handlerAnimation(documentDto.getDocImageUrl());
                String quantityStr = binding.edtQuantity.getText().toString();
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(getContext(), "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
                Long userId = sessionManager.getUser().getUserId();
                Long docId = documentDto.getDocId();

                Log.d("TEST",""+cartDao.existsInCart(userId,docId));

                if(cartDao.existsInCart(userId,docId)){
                    CartDto existingCart = cartDao.getCartByUserAndDocId(userId,docId);
                    if (isOnline) {
                        CartDto toSync = new CartDto(
                                existingCart.getCartId(),quantity, userId, docId, null, existingCart.getSellPrice(), existingCart.getDocImageUrl(), false, "UPDATE", 0);
                        ApiService.apiService.addOrUpdate(toSync).enqueue(new Callback<ResponseData<CartDto>>() {
                                    @Override
                                    public void onResponse(Call<ResponseData<CartDto>> call,
                                                           Response<ResponseData<CartDto>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            CartDto updated = response.body().getData();
                                            existingCart.setQuantity(updated.getQuantity());
                                            existingCart.setSyncStatus(1);
                                            cartDao.updateCartItem(existingCart);
                                            handlerCartCount(userId);
                                            Toast.makeText(getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                                            notifyCartChange(userId, toSync.getDocName(), toSync.getQuantity());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseData<CartDto>> call, Throwable t) {
                                        Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ: " + t.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else{
                        existingCart.setQuantity(existingCart.getQuantity()+quantity);
                        existingCart.setAction("UPDATE");
                        existingCart.setSyncStatus(0);
                        Log.d("TEST",""+existingCart.getSyncStatus());
                        cartDao.updateCartItem(existingCart);
                        handlerCartCount(userId);
                        Toast.makeText(getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                        notifyCartChange(userId, existingCart.getDocName(), existingCart.getQuantity());
                    }

                }
                else{
                    CartDto newCart = new CartDto(null,quantity,userId,docId,documentDto.getDocName(),documentDto.getSellPrice(),
                            documentDto.getDocImageUrl(),false,"INSERT",0);
                    cartDao.addToCart(newCart);
                    handlerCartCount(userId);
                    if (isOnline)
                        new SyncManager().syncCarts(getContext());
                    Toast.makeText(getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    notifyCartChange(userId, newCart.getDocName(), newCart.getQuantity());
                }

            }
        });
    }

    private void handlerCartCount(Long userId){
        requireActivity().runOnUiThread(() -> {
            int cartCount = cartDao.getCountCart(userId);
            binding.tvCartBadge.setText(String.valueOf(cartCount));
        });
    }

    private void handlerAnimation(String image){
        AnimUtils.translateAnimation(binding.viewAnim, binding.btnAddToCart, binding.imgViewCart, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("Animation", "Started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("Animation", "Ended");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        },image);
    }

    private void sendServerNotification(Long userId, String title, String content){
        NotificationDto dto = new NotificationDto(null, userId, title, content, NotificationType.CART, LocalDateTime.now(), false);
        ApiService.apiService.pushLocalNotification(dto).enqueue(new Callback<ResponseData<Void>>() {
            @Override
            public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                ToastUtils.show(getContext(),"Gửi thông báo thành công");
            }

            @Override
            public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                ToastUtils.show(getContext(),"Lỗi thông báo");
            }
        });
    }

    private void notifyCartChange(Long userId, String docName, int qty) {
        String title   = (qty > 0 ? "Giỏ hàng đã cập nhật" : "Thêm vào giỏ hàng");
        String content = docName + " x" + qty;

        NotificationDto localNoti = new NotificationDto(
                null,
                userId,
                title,
                content,
                NotificationType.CART,
                LocalDateTime.now(),
                false
        );
        notificationDao.addNotification(localNoti);

        NotificationHelper.showNotification(
                requireContext(),
                NotificationHelper.Channel.CART,
                title,
                content
        );

        sendServerNotification(userId, title, content);
    }
}