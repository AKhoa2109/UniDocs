package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.CartAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.CartDao;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentCartBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.DiscountType;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.CreateOrderFromCartRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.CartViewModel;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;
import vn.anhkhoa.projectwebsitebantailieu.utils.NetworkUtil;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;
import com.google.gson.Gson;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements CartAdapter.Listener{
    private FragmentCartBinding binding;
    private CartDao cartDao;
    private CartAdapter cartAdapter;
    private List<CartDto> carts;
    private SessionManager sessionManager;
    private List<CartDto> cartItems;
    private CartViewModel viewModel;
    DatabaseHandler databaseHandler;
    private DiscountDto currentVoucher = null;
    private double totalPrice;
    private CompoundButton.OnCheckedChangeListener selectAllListener =
            (buttonView, isChecked) -> {
                cartAdapter.selectAllItems(isChecked);
                if (isChecked) {
                    viewModel.setSelectedCartItems(new ArrayList<>(cartItems));
                } else {
                    viewModel.clearSelectedCartItems();
                }
                calculateTotalAmount();
            };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        binding =  FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (viewModel == null) {
            viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        }
        sessionManager = SessionManager.getInstance(requireContext());
        databaseHandler = DatabaseHandler.getInstance(getContext());

        cartDao = new CartDao(getContext());
        initView();
        getVoucherBackData();


        Long userId = sessionManager.getUser().getUserId();
        if (userId == null) {
            Log.d("CartFragment", "userId is null");
        }
        getApiCartByUserId(userId);
        setupSelectAll();
        handlerRemoveAll(userId);
        handlerVoucher();
    }

    private void initView(){
        binding.ibRemoveAll.setVisibility(View.INVISIBLE);
        carts = new ArrayList<>();
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this);
        binding.rcCartItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcCartItem.setAdapter(cartAdapter);
        Random random = new Random();

        binding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy danh sách items đã chọn từ ViewModel
                List<CartDto> selectedItems = viewModel.getSelectedCartItems().getValue();
                
                if (selectedItems == null || selectedItems.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo danh sách cartIds từ selectedItems
                List<Long> cartIds = selectedItems.stream()
                        .map(CartDto::getCartId)
                        .collect(Collectors.toList());

                // Lấy voucher đã chọn (nếu có)
                AtomicReference<DiscountDto> selectedVoucher = new AtomicReference<>();
                if (!binding.tvVoucherName.getText().toString().isEmpty()) {
                    getParentFragmentManager().setFragmentResultListener("voucherResult", getViewLifecycleOwner(), (requestKey, result) -> {
                        if (result != null && result.containsKey("selectedDiscount")) {
                            selectedVoucher.set((DiscountDto) result.getSerializable("selectedDiscount"));
                        }
                    });
                }

                // Tạo request object
                CreateOrderFromCartRequest request = new CreateOrderFromCartRequest(
                    cartIds,
                    selectedVoucher.get() != null ? selectedVoucher.get().getDiscountId() : null,
                    sessionManager.getUser().getUserId()
                );

                // Hiển thị loading
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnBuy.setEnabled(false);

                // Gọi API createOrderFromCart
                ApiService.apiService.createPayment(request).enqueue(new Callback<ResponseData<Map<String, String>>>() {
                    @Override
                    public void onResponse(Call<ResponseData<Map<String, String>>> call, Response<ResponseData<Map<String, String>>> response) {
                        // Ẩn loading
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnBuy.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, String> data = response.body().getData();
                            String paymentUrl = data.get("paymentUrl");
                            
                            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                // Mở trang thanh toán
                                openInCustomTab(view, paymentUrl);
                            } else {
                                Toast.makeText(getContext(), "Không lấy được URL thanh toán", Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            String errorMessage = "Đặt hàng thất bại";
//                            if (response.errorBody() != null) {
//                                try {
//                                    ResponseData<?> errorResponse = new Gson().fromJson(
//                                        response.errorBody().string(),
//                                        ResponseData.class
//                                    );
//                                    errorMessage = errorResponse.getMessage();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            ToastUtils.show(requireActivity(), "Đặt hàng thất bại !");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<Map<String, String>>> call, Throwable t) {
                        // Ẩn loading
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnBuy.setEnabled(true);
                        
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //btnback
        binding.btnBack.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });
    }
    // Mở link bằng Chrome Custom Tabs
    private void openInCustomTab(View view, String url) {
        Context ctx = view.getContext();
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build();
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(url));
    }

    private void handlerVoucher(){
        binding.imageButtonVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CartDto> selected = viewModel.getSelectedCartItems().getValue();
                if (selected == null || selected.isEmpty()){
                    ToastUtils.show(getContext(), "Cần chọn ít nhất 1 sản phẩm");
                    return;
                }
                else{
                    if(getContext() instanceof MainActivity){
                        ((MainActivity) getContext()).openVoucherFragment(selected);
                    }
                }
            }
        });
    }

    private void handlerRemoveAll(Long userId){
        boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
        binding.ibRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline){
                    cartDao.deleteAllForUser(userId);
                    ApiService.apiService.deleteAllCart(userId).enqueue(new Callback<ResponseData<Void>>() {
                        @Override
                        public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                            if(response.isSuccessful()){
                                cartItems.clear();
                                calculateTotalAmount();
                                cartAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    cartDao.deleteAllForUser(userId);
                    cartItems.clear();
                    calculateTotalAmount();
                    cartAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getApiCartByUserId(Long userId){
        boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
        List<CartDto> selectedItems = viewModel.getSelectedCartItems().getValue();
        if(isOnline){
            ApiService.apiService.getCartByUserId(userId).enqueue(new Callback<ResponseData<List<CartDto>>>() {
                @Override
                public void onResponse(Call<ResponseData<List<CartDto>>> call, Response<ResponseData<List<CartDto>>> response) {
                    if (!isAdded()) return;
                    if(response.isSuccessful() && response.body() != null){
                        if (viewModel == null) {
                            viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
                        }
                        // Lấy danh sách selected từ ViewModel
                        Set<Long> selectedIds = new HashSet<>();
                        if (selectedItems != null) {
                            for (CartDto item : selectedItems) {
                                selectedIds.add(item.getCartId());
                            }
                        }

                        // Làm mới cartItems
                        cartItems.clear();
                        cartDao.deleteAllForUser(userId);
                        carts = response.body().getData();
                        for (CartDto item : carts) {
                            boolean isSelected = selectedIds.contains(item.getCartId());
                            item.setSelected(isSelected);
                            cartDao.addToCart(new CartDto(item.getCartId(),
                                    item.getQuantity(), item.getUserId(), item.getDocId(), item.getDocName(),
                                    item.getSellPrice(), item.getDocImageUrl(), isSelected, "INSERT", isOnline ? 1 : 0));
                            cartItems.add(item);
                        }
                        cartAdapter.notifyDataSetChanged();
                        calculateTotalAmount();

                        // Cập nhật lại selectedCartItems trong ViewModel
                        List<CartDto> newSelected = new ArrayList<>();
                        for (CartDto item : cartItems) {
                            if (item.isSelected()) {
                                newSelected.add(item);
                            }
                        }
                        viewModel.setSelectedCartItems(newSelected);
                        binding.ibRemoveAll.setVisibility(newSelected.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                        updateSelectAllCheckbox();
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<List<CartDto>>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            // OFFLINE: same idea
            List<CartDto> local = cartDao.getCartItemsByUser(userId);
            cartItems.clear();

            List<CartDto> oldSelected = viewModel.getSelectedCartItems().getValue();
            Set<Long> selIds = new HashSet<>();
            for (CartDto s : oldSelected) selIds.add(s.getCartId());

            for (CartDto item : local) {
                boolean wasSel = selIds.contains(item.getCartId());
                item.setSelected(wasSel);
                cartItems.add(item);
            }
            cartAdapter.notifyDataSetChanged();

            List<CartDto> newSel = new ArrayList<>();
            for (CartDto it : cartItems) {
                if (it.isSelected()) newSel.add(it);
            }
            viewModel.setSelectedCartItems(newSel);
            calculateTotalAmount();
            binding.ibRemoveAll.setVisibility(newSel.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            binding.cbSelect.setChecked(newSel.size() == cartItems.size() && !cartItems.isEmpty());
        }

    }

    private void deleteApiCartItem(Long itemId, int position) {
        ApiService.apiService.deleteCartItem(itemId).enqueue(new Callback<ResponseData<Void>>() {
            @Override
            public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    calculateTotalAmount();
                    cartDao.deleteCartPermanently(itemId);
                    Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Xóa thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupSelectAll() {
        binding.cbSelect.setOnCheckedChangeListener(null);
        boolean allSelected = !cartItems.isEmpty() &&
                viewModel.getSelectedCartItems().getValue().size() == cartItems.size();
        binding.cbSelect.setChecked(allSelected);
        binding.cbSelect.setOnCheckedChangeListener(selectAllListener);
    }

    private void updateSelectAllCheckbox() {
        binding.cbSelect.setOnCheckedChangeListener(null);
        boolean allSelected = !cartItems.isEmpty() &&
                viewModel.getSelectedCartItems().getValue().size() == cartItems.size();
        binding.cbSelect.setChecked(allSelected);
        binding.cbSelect.setOnCheckedChangeListener(selectAllListener);
    }

    private void calculateTotalAmount() {
        // 1. Tính tổng giá cơ bản
        double baseTotal = 0;
        List<CartDto> selected = viewModel.getSelectedCartItems().getValue();
        if (selected != null) {
            for (CartDto c : selected) {
                baseTotal += c.getSellPrice() * c.getQuantity();
            }
        }

        // 2. Hiển thị tổng giá sản phẩm
        binding.tvPrice.setText(CurrentFormatter.format(baseTotal));

        // 3. Áp dụng voucher (nếu có)
        if (currentVoucher != null) {
            double discountValue = currentVoucher.getDiscountValue();
            double discountAmount;
            if (currentVoucher.getDiscountType() == DiscountType.PERCENT) {
                // % discount
                discountAmount = baseTotal * discountValue / 100;
            } else {
                // fixed discount
                discountAmount = discountValue;
            }
            // đảm bảo không âm
            discountAmount = Math.min(discountAmount, baseTotal);
            binding.tvDiscount.setText(CurrentFormatter.format(discountAmount));

            double finalTotal = baseTotal - discountAmount;
            binding.tvTotalPrice.setText(CurrentFormatter.format(finalTotal));
        } else {
            // không có voucher
            binding.tvDiscount.setText(CurrentFormatter.format(0));
            binding.tvTotalPrice.setText(CurrentFormatter.format(baseTotal));
        }
    }


    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        CartDto item = cartItems.get(position);
        item.setSelected(isChecked);
        if (isChecked) {
            viewModel.addSelectedCartItem(item);
        } else {
            viewModel.removeSelectedCartItem(item);
        }
        calculateTotalAmount();  // luôn cập nhật
    }


    @Override
    public void onQuantityChanged(int position, int quantity) {
        // Lấy thông tin sản phẩm trong giỏ hàng tại vị trí được chọn
        CartDto cartItem = cartItems.get(position);
        // Cập nhật số lượng mới
        cartItem.setQuantity(quantity);
        
        // Kiểm tra kết nối mạng
        boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
        if (isOnline) {
            // Gọi API cập nhật số lượng giỏ hàng
            ApiService.apiService.updateCart(cartItem).enqueue(new Callback<ResponseData<CartDto>>() {
                @Override
                public void onResponse(Call<ResponseData<CartDto>> call, Response<ResponseData<CartDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lấy dữ liệu giỏ hàng đã được cập nhật từ server
                        CartDto updatedCart = response.body().getData();
                        if (updatedCart != null) {
                            // Cập nhật vào cơ sở dữ liệu local
                            cartDao.updateCartItem(updatedCart);
                            // Cập nhật giao diện người dùng
                            cartItems.set(position, updatedCart);
                            cartAdapter.notifyItemChanged(position);
                            // Tính toán lại tổng tiền
                            calculateTotalAmount();
                        }
                    } else {
                        // Nếu API call thất bại, khôi phục lại số lượng cũ
                        cartItem.setQuantity(cartItem.getQuantity());
                        cartAdapter.notifyItemChanged(position);
                        Toast.makeText(getContext(), "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<CartDto>> call, Throwable t) {
                    // Nếu có lỗi kết nối, khôi phục lại số lượng cũ
                    cartItem.setQuantity(cartItem.getQuantity());
                    cartAdapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    calculateTotalAmount();
                }
            });
        } else {
            // Chế độ offline - chỉ cập nhật cơ sở dữ liệu local
            cartDao.updateCartItem(cartItem);
            cartAdapter.notifyItemChanged(position);
            calculateTotalAmount();
        }
    }

    @Override
    public void onRemoveItem(int position) {
        boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
        Long cartItemId = cartItems.get(position).getCartId();
        CartDto removedItem = cartItems.get(position);
        if (viewModel == null) {
            viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        }
        if (isOnline) {
            deleteApiCartItem(cartItemId, position);
        } else {
            cartDao.deleteCartItem(cartItemId);
            cartItems.remove(position);
            cartAdapter.notifyItemRemoved(position);
            calculateTotalAmount();
            Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
        }
        viewModel.removeSelectedCartItem(removedItem);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void getVoucherBackData(){
        getParentFragmentManager().setFragmentResultListener("voucherResult", getViewLifecycleOwner(), (requestKey, result) -> {
            // Lấy voucher
            DiscountDto selectedVoucher = (DiscountDto) result.getSerializable("selectedDiscount");
            // Lấy lại list CartDto đã chọn
            List<CartDto> returnedSelected = (List<CartDto>) result.getSerializable("selectedCartItems");
//****
            Double price = 0.0;

            if (returnedSelected != null) {
                // Cập nhật ViewModel
                viewModel.setSelectedCartItems(returnedSelected);

                Set<Long> selIds = returnedSelected.stream()
                        .map(CartDto::getCartId)
                        .collect(Collectors.toSet());
                for (CartDto item : cartItems) {
                    item.setSelected(selIds.contains(item.getCartId()));
                }

                for(CartDto item : returnedSelected){
                    price += item.getSellPrice() * item.getQuantity();
                }

                cartAdapter.notifyDataSetChanged();
                calculateTotalAmount();
                // Cập nhật nút Remove All / Select All nếu cần
                binding.ibRemoveAll.setVisibility(returnedSelected.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                binding.cbSelect.setChecked(returnedSelected.size() == cartItems.size() && !cartItems.isEmpty());
            }

            if (selectedVoucher != null) {
                binding.tvVoucherName.setText(selectedVoucher.getDiscountName());
                applyVoucher(selectedVoucher, price);
                calculateTotalAmount();
            }
        });
    }

    private void applyVoucher(DiscountDto voucher, Double totalPrice) {
        double discountValue = voucher.getDiscountValue();
        double totalDiscountValue = totalPrice * discountValue / 100;
        double finalTotal = 0;

        if (voucher.getDiscountType() == DiscountType.PERCENT) {
            binding.tvDiscount.setText(CurrentFormatter.format(totalDiscountValue));
            finalTotal = totalPrice - totalDiscountValue;
        } else {
            binding.tvDiscount.setText(CurrentFormatter.format(discountValue));
            finalTotal = totalPrice - discountValue;
        }

        binding.tvTotalPrice.setText(CurrentFormatter.format(Math.max(finalTotal, 0)));
    }
}