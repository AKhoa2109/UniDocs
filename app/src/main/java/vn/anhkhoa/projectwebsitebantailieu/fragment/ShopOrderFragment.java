package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayoutMediator;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ShopOrderAdapter;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopOrderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopOrderFragment extends Fragment {
    FragmentShopOrderBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopOrderFragment newInstance(String param1, String param2) {
        ShopOrderFragment fragment = new ShopOrderFragment();
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
        binding =  FragmentShopOrderBinding.inflate(inflater, container, false);
        setupNestedViewPager();
        return binding.getRoot();
    }

    private void setupNestedViewPager() {
        binding.nestedViewPager.setAdapter(new ShopOrderAdapter(this));

        new TabLayoutMediator(binding.nestedTabLayout, binding.nestedViewPager, (tab, position) -> {
            Log.d("TAB_DEBUG", "Tab position: " + position); // Thêm dòng này
            switch (position) {
                case 0:
                    tab.setText("Chưa xác nhận");
                    break;
                case 1:
                    tab.setText("Đang giao");
                    break;
                case 2:
                    tab.setText("Hoàn thành");
                    break;
                case 3:
                    tab.setText("Đã hủy");
                    break;
            }
        }).attach();

    }
}