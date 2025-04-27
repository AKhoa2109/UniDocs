package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ViewPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopDocumentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopDocumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopDocumentFragment extends Fragment {
    FragmentShopDocumentBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopDocumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopDocumentFragment newInstance(String param1, String param2) {
        ShopDocumentFragment fragment = new ShopDocumentFragment();
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
        binding = FragmentShopDocumentBinding.inflate(inflater, container, false);
        setupNestedViewPager();
        handlerTabLayout();
        return binding.getRoot();
    }

    private void setupNestedViewPager() {
        binding.nestedViewPager.setAdapter(new DocumentPagerAdapter(getChildFragmentManager(), getLifecycle()));

        new TabLayoutMediator(binding.nestedTabLayout, binding.nestedViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Mới nhất");
                    break;
                case 1:
                    tab.setText("Mua nhiều");
                    break;
                case 2:
                    tab.setText("Giá thấp đến cao");
                    break;
                case 3:
                    tab.setText("Giá cao đến thấp");
                    break;
            }
        }).attach();
    }

    private void handlerTabLayout(){
        binding.nestedTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.nestedViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}