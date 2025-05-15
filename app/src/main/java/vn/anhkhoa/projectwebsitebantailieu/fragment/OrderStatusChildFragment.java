package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.OrderHistoryAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.api.RetrofitClient;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentOrderStatusChildBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.OrderViewModel;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderStatusChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderStatusChildFragment extends Fragment implements OrderHistoryAdapter.OnOrderClickListener {
    private FragmentOrderStatusChildBinding binding;
    private OrderHistoryAdapter adapter;
    private SessionManager sessionManager;

    private OrderViewModel vm;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STATUS = "status";

    // TODO: Rename and change types of parameters
    private OrderStatus status;

    public OrderStatusChildFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param status The status of the orders to display.
     * @return A new instance of fragment OrderStatusChildFragment.
     */
    public static OrderStatusChildFragment newInstance(OrderStatus status) {
        OrderStatusChildFragment fragment = new OrderStatusChildFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = (OrderStatus) getArguments().getSerializable(ARG_STATUS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderStatusChildBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());
        vm.getReloadTrigger().observe(getViewLifecycleOwner(), unused -> {
            loadOrders(sessionManager.getUser().getUserId(),status);
        });
        setupRecyclerView();
        loadOrders(sessionManager.getUser().getUserId(), status);
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new OrderHistoryAdapter(this);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrders.setAdapter(adapter);
    }

    private void loadOrders(Long userId, OrderStatus status) {
        // TODO: Replace with actual user ID from your authentication system

        ApiService.apiService.getOrdersByStatus(userId,status).enqueue(new Callback<ResponseData<List<OrderDtoRequest>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<OrderDtoRequest>>> call, Response<ResponseData<List<OrderDtoRequest>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDtoRequest> orders = response.body().getData();
                    if (orders != null && !orders.isEmpty()) {
                        adapter.setOrders(orders);
                        binding.tvEmpty.setVisibility(View.GONE);
                        binding.rvOrders.setVisibility(View.VISIBLE);
                    } else {
                        showEmptyView();
                    }
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<OrderDtoRequest>>> call, Throwable t) {
                ToastUtils.show(getContext(),"Lỗi");
            }
        });

    }

    private void showEmptyView() {
        binding.tvEmpty.setVisibility(View.VISIBLE);
        binding.rvOrders.setVisibility(View.GONE);
    }

    @Override
    public void onDetailClick(OrderDtoRequest order) {
        // TODO: Implement navigation to order detail screen
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).openOrderDetailFragment(order);
        }
        Toast.makeText(getContext(), "View details for order: " + order.getOrderId(), 
                     Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBuyAgainClick(OrderDtoRequest order) {
        // TODO: Implement buy again functionality
        DocumentDto documentDto = new DocumentDto(order.getDocId());
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).openProductDetailFragment(documentDto);
        }
        Toast.makeText(getContext(), "Buy again: " + order.getDocName(), 
                     Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelClick(OrderDtoRequest order)
    {
        OrderStatus newStatus = OrderStatus.CANCELED;
        OrderStatus oldStatus = order.getStatus();
        if (newStatus == oldStatus) return;
        order.setStatus(newStatus);

        ApiService.apiService.updateOrderStatus(order.getOrderId(), newStatus).enqueue(new Callback<ResponseData<Void>>() {
            @Override
            public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                if (response.isSuccessful()) {
                    ToastUtils.show(getContext(), "Cập nhật thành công");
                    OrderViewModel vm = new ViewModelProvider((FragmentActivity) requireContext())
                            .get(OrderViewModel.class);
                    vm.triggerReload();
                } else {
                    order.setStatus(oldStatus);
                    ToastUtils.show(getContext(), "Cập nhật thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                order.setStatus(oldStatus);
                ToastUtils.show(getContext(), "Lỗi kết nối");
            }
        });
    }
}