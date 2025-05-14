package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentOrderDetailBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OrderDetailDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment {
    FragmentOrderDetailBinding binding;

    private OrderDtoRequest orderDtoRequest;
    private OrderDetailDtoRequest orderDetailDtoRequest;

    private SessionManager sessionManager;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        binding =  FragmentOrderDetailBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderDtoRequest = (OrderDtoRequest) bundle.getSerializable("order");
        }

        getAPIOrderDetail(orderDtoRequest.getOrderId(),orderDtoRequest.getDocId(),sessionManager.getUser().getUserId());

        binding.ivBack.setOnClickListener(v -> {

            binding.getRoot().postDelayed(() -> {

                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack();


                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showBottomNavigation();
                }
            }, 50);
        });

        binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentDto documentDto = new DocumentDto(orderDtoRequest.getDocId());
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).openProductDetailFragment(documentDto);
                }
            }
        });

        return binding.getRoot();
    }

    private void getAPIOrderDetail(Long orderId, Long docId, Long userId){
        ApiService.apiService.getOrderDetail(orderId,docId,userId).enqueue(new Callback<ResponseData<OrderDetailDtoRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<OrderDetailDtoRequest>> call, Response<ResponseData<OrderDetailDtoRequest>> response) {
                if(response.isSuccessful() && response.body().getData() != null){
                    orderDetailDtoRequest = response.body().getData();
                    bindView(orderDetailDtoRequest);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OrderDetailDtoRequest>> call, Throwable t) {

            }
        });
    }

    private void bindView(OrderDetailDtoRequest orderDetailDtoRequest){
        binding.tvOrderStatus.setText("Đơn hàng "+setStatus(orderDetailDtoRequest.getOrderStatus()));
        binding.tvDocName.setText(orderDetailDtoRequest.getDocName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime orderAt = orderDetailDtoRequest.getOrderAt();
        binding.tvOrderDate.setText(orderAt.format(formatter));
        binding.tvName.setText(orderDetailDtoRequest.getName());
        binding.tvPhone.setText(orderDetailDtoRequest.getPhone());
        binding.tvAddress.setText(orderDetailDtoRequest.getAddress());
    }

    private String setStatus(OrderStatus status){
        if(status == OrderStatus.CANCELED)
            return "đã hủy";
        else if(status == OrderStatus.CONFIRMED)
            return "đã hoàn thành";
        else if(status == OrderStatus.DELIVERED)
            return "đang giao";
        else
            return "chưa xác nhận";
    }
}