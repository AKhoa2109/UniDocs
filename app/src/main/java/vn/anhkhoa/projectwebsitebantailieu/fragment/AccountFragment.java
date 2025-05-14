package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.activity.SignIn;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentAccountBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    SessionManager session;

    private UserRegisterRequest user;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentAccountLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        session = SessionManager.getInstance(requireContext());
        getApiUserInfo(session.getUser().getUserId());

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xóa session đăng nhập
                session.logout();

                // Chuyển về LoginActivity và đóng mọi Activity trước đó
                Intent intent = new Intent(requireActivity(), SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        //mo fragment discount khi nhan btnDiscount
        binding.ivPromotionNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Context ctx = view.getContext();
//                ToastUtils.show(ctx, "btnDiscount");
//                if (ctx instanceof MainActivity) {
//                    MainActivity main = (MainActivity) ctx;
//                    DiscountFragment discountFragment = new DiscountFragment();
//                    main.showFragment(discountFragment, "discountFragment");
//                }

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openDiscountFragment();
                }
            }
        });

        binding.ivAccountDetailNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openUserDetailFragment(user);
                }
            }
        });

        // Xử lý click vào nút xem tất cả đơn hàng
        binding.ivOrderNext.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openOrderStatusFragment(OrderStatus.PENDING);
            }
        });

        // Xử lý click vào các icon trạng thái đơn hàng
        binding.ivOrderInconfim.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openOrderStatusFragment(OrderStatus.PENDING);
            }
        });

        binding.ivOrderDelivered.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openOrderStatusFragment(OrderStatus.DELIVERED);
            }
        });

        binding.ivOrderConfirmed.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openOrderStatusFragment(OrderStatus.CONFIRMED);
            }
        });

        binding.ivOrderCancel.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openOrderStatusFragment(OrderStatus.CANCELED);
            }
        });
    }

    private void getApiUserInfo(Long userId){
        ApiService.apiService.getUser(userId).enqueue(new Callback<ResponseData<UserRegisterRequest>>() {
            @Override
            public void onResponse(Call<ResponseData<UserRegisterRequest>> call, Response<ResponseData<UserRegisterRequest>> response) {
                if (response.isSuccessful() && response.body().getData() != null){
                    user = response.body().getData();
                    bindView(user);
                }
                else {
                    Log.e("AccountFragment", "Error Response: " + response.errorBody());
                    ToastUtils.show(getContext(), "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserRegisterRequest>> call, Throwable t) {
                Log.e("AccountFragment", "API Error: " + t.getMessage());
                ToastUtils.show(getContext(), "Lỗi");
            }
        });
    }

    private void bindView(UserRegisterRequest user){
        Glide.with(getContext()).load(user.getAvatar()).into(binding.civAvatar);
        binding.tvName.setText(user.getName());
        binding.tvEmail.setText(user.getEmail());
    }
}