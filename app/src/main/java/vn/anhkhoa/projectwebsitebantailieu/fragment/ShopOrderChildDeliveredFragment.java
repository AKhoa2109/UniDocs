package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ShopOrderChildAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopOrderChildDeliveredBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.OrderViewModel;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopOrderChildDeliveredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopOrderChildDeliveredFragment extends Fragment {
    FragmentShopOrderChildDeliveredBinding binding;
    private OrderStatus status;
    private ShopOrderChildAdapter adapter;
    private List<OrderDtoRequest> orders;

    private SessionManager sessionManager;
    private OrderViewModel vm;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopOrderChildDeliveredFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ShopOrderChildDeliveredFragment newInstance(OrderStatus status) {
        ShopOrderChildDeliveredFragment fragment = new ShopOrderChildDeliveredFragment();
        Bundle args = new Bundle();
        args.putSerializable("status",status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = (OrderStatus) getArguments().getSerializable("status");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopOrderChildDeliveredBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("CHILD_FRAGMENT", "Status received: " + status);
        vm = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());
        vm.getReloadTrigger().observe(getViewLifecycleOwner(), unused -> {
            loadData(status, sessionManager.getUser().getUserId());
        });

        setupRecyclerView();
        loadData(status, sessionManager.getUser().getUserId());
        updateEmptyState();
    }


    // Hàm khởi tạo recyclerView
    private void setupRecyclerView() {
        orders = new ArrayList<>();
        adapter = new ShopOrderChildAdapter(getContext(),orders);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvOrders.setAdapter(adapter);
    }

    // Hàm lấy dữ liệu từ API và load recyclerView
    private void loadData(OrderStatus status, Long postId) {
        ApiService.apiService.getShopOrderStatus(status, postId).enqueue(new Callback<ResponseData<List<OrderDtoRequest>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<OrderDtoRequest>>> call, Response<ResponseData<List<OrderDtoRequest>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    orders.clear();
                    orders.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    orders.clear();
                    ToastUtils.show(getContext(), "Không có dữ liệu");
                }
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<ResponseData<List<OrderDtoRequest>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi kết nối");
            }
        });
    }

    // Hàm cập nhật view nếu không có dữ liệu
    private void updateEmptyState() {
        if (orders.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvOrders.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvOrders.setVisibility(View.VISIBLE);
        }
    }


}