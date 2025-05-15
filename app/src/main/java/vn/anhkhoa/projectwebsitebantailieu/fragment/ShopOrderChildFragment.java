package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ShopOrderAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ShopOrderChildAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopOrderChildBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopOrderChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopOrderChildFragment extends Fragment {
    private OrderStatus status;
    FragmentShopOrderChildBinding binding;
    private ShopOrderChildAdapter adapter;
    private List<OrderDtoRequest> orders;

    private SessionManager sessionManager;
    public ShopOrderChildFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ShopOrderChildFragment newInstance(OrderStatus status) {
        ShopOrderChildFragment fragment = new ShopOrderChildFragment();
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
        binding = FragmentShopOrderChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = SessionManager.getInstance(requireContext());
        setupRecyclerView();
        loadData(status,sessionManager.getUser().getUserId());
        updateEmptyState();
    }

    // Hàm khởi tạo recyclerView
    private void setupRecyclerView() {
        orders = new ArrayList<>();
        adapter = new ShopOrderChildAdapter(orders);

        // Thêm phần này ngay sau khi khởi tạo adapter
        adapter.setOnStatusChangeListener((position, newStatus) -> {
            // Cập nhật trạng thái trong danh sách local
            orders.get(position).setStatus(newStatus);

            // Cập nhật UI
            adapter.notifyItemChanged(position);

            // Gọi API cập nhật lên server (thêm phần này)
            updateOrderStatusOnServer(
                    orders.get(position).getOrderId(), // Giả sử OrderDtoRequest có trường id
                    newStatus,
                    position
            );
        });

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrders.setAdapter(adapter);
    }

    // Hàm lấy dữ liệu từ API và load recyclerView
    private void loadData(OrderStatus status,Long postId){
        ApiService.apiService.getShopOrderStatus(status,postId).enqueue(new Callback<ResponseData<List<OrderDtoRequest>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<OrderDtoRequest>>> call, Response<ResponseData<List<OrderDtoRequest>>> response) {
                if(response.isSuccessful() && response.body().getData() !=null){
                    orders.clear();
                    orders = response.body().getData();
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                }
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

    // Hàm cập nhật trạng thái đơn hàng lên API server
    private void updateOrderStatusOnServer(Long orderId, OrderStatus newStatus, int position){
        ApiService.apiService.updateOrderStatus(orderId,newStatus).enqueue(new Callback<ResponseData<Void>>() {
            @Override
            public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                if (response.isSuccessful()) {
                    ToastUtils.show(getContext(), "Cập nhật thành công");
                } else {
                    // Rollback nếu thất bại
                    orders.get(position).setStatus(orders.get(position).getStatus());
                    adapter.notifyItemChanged(position);
                    ToastUtils.show(getContext(), "Cập nhật thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                orders.get(position).setStatus(orders.get(position).getStatus());
                adapter.notifyItemChanged(position);
                ToastUtils.show(getContext(), "Lỗi kết nối");
            }
        });
    }
}