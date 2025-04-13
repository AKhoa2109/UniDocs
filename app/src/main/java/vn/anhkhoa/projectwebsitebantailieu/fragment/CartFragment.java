package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
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
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;
import vn.anhkhoa.projectwebsitebantailieu.utils.NetworkUtil;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.SyncManager;

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

    private List<CartDto> selectedCartItem;
    DatabaseHandler databaseHandler;
    private double totalPrice = 0;

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
        sessionManager = SessionManager.getInstance(requireContext());
        databaseHandler = DatabaseHandler.getInstance(getContext());
        cartDao = new CartDao(getContext());
        initView();
        Long userId = sessionManager.getUser().getUserId();
        if (userId == null) {
            Log.d("CartFragment", "userId is null");
        }
        getApiCartByUserId(userId);
        setupSelectAll();
        handlerRemoveAll(userId);
        handlerVoucher();
        return binding.getRoot();
    }

    private void initView(){
        binding.ibRemoveAll.setVisibility(View.INVISIBLE);
        carts = new ArrayList<>();
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this);
        binding.rcCartItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcCartItem.setAdapter(cartAdapter);
    }

    private void handlerVoucher(){
        binding.imageButtonVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getContext() instanceof MainActivity){
                    ((MainActivity) getContext()).openVoucherFragment(selectedCartItem);
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
        if(isOnline){
            ApiService.apiService.getCartByUserId(userId).enqueue(new Callback<ResponseData<List<CartDto>>>() {
                @Override
                public void onResponse(Call<ResponseData<List<CartDto>>> call, Response<ResponseData<List<CartDto>>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        carts = response.body().getData();
                        cartItems.clear();
                        cartDao.deleteAllForUser(userId);
                        for(CartDto item : carts){
                            cartDao.addToCart(new CartDto(item.getCartId(),
                                    item.getQuantity(),item.getUserId(),item.getDocId(), item.getDocName(),
                                    item.getSellPrice(), item.getDocImageUrl(),false,"INSERT",isOnline ? 1 : 0));
                            cartItems.add(item);
                        }
                        cartAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<List<CartDto>>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            List<CartDto> localCarts = cartDao.getCartItemsByUser(userId);
            cartItems.clear();
            cartItems.addAll(localCarts);
            cartAdapter.notifyDataSetChanged();
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
        binding.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                binding.ibRemoveAll.setVisibility(View.INVISIBLE);
                cartAdapter.selectAllItems(isChecked);
                calculateTotalAmount();
            }
            else{
                binding.ibRemoveAll.setVisibility(View.VISIBLE);
                cartAdapter.selectAllItems(isChecked);
                calculateTotalAmount();
            }
        });
    }

    private void calculateTotalAmount() {
        totalPrice = 0;
        for(CartDto item : cartItems) {
            if(item.isSelected()) {
                totalPrice += item.getSellPrice() * item.getQuantity();
            }
        }
        binding.tvPrice.setText(CurrentFormatter.format(totalPrice));
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        CartDto item = cartItems.get(position);
        item.setSelected(isChecked);
        calculateTotalAmount();
        if (isChecked) {
            if (!selectedCartItem.contains(item)) {
                selectedCartItem.add(item);
            }
        } else {
            selectedCartItem.remove(item);
        }
    }

    @Override
    public void onQuantityChanged(int position, int quantity) {
        cartItems.get(position).setQuantity(quantity);
        calculateTotalAmount();
    }

    @Override
    public void onRemoveItem(int position) {
        boolean isOnline = NetworkUtil.isNetworkAvailable(getContext());
        Long cartItemId = cartItems.get(position).getCartId();
        if (isOnline) {
            deleteApiCartItem(cartItemId, position);
        } else {

            cartDao.deleteCartItem(cartItemId);
            cartItems.remove(position);
            cartAdapter.notifyItemRemoved(position);
            calculateTotalAmount();
            Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        syncCartWhenOnline();
    }

    private void syncCartWhenOnline() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            new SyncManager().syncCarts(getContext());
            Long userId = sessionManager.getUser().getUserId();
            if (userId != null) {
                getApiCartByUserId(userId);
            }
        }
    }

}