package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.OrderHistoryViewPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentOrderStatusBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderStatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_STATUS = "status";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String initialStatus;

    private FragmentOrderStatusBinding binding;
    private final String[] tabTitles = new String[]{"Chưa xác nhận", "Đang giao", "Hoàn thành", "Đã hủy"};
    private final String[] statusValues = new String[]{"PENDING", "DELIVERED", "CONFIRMED", "CANCELED"};

    public OrderStatusFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static OrderStatusFragment newInstance(OrderStatus status) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            initialStatus = getArguments().getSerializable(ARG_STATUS).toString();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentOrderStatusBinding.inflate(inflater, container, false);
        ToastUtils.show(getContext(),""+initialStatus);
        setupViewPager();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).showBottomNavigation();
                }
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
        return binding.getRoot();
    }

    // Hàm khởi tạo viewPager
    private void setupViewPager() {
        // Thay thế adapter cũ bằng adapter mới
        binding.viewPager.setAdapter(new OrderHistoryViewPagerAdapter(
                this,
                statusValues
        ));

        // Kết nối TabLayout với ViewPager2 (giữ nguyên)
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();


        // Set initial position based on status
        if (initialStatus != null) {
            for (int i = 0; i < statusValues.length; i++) {
                if (statusValues[i].equals(initialStatus)) {
                    binding.viewPager.setCurrentItem(i, false);
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}