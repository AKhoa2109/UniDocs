package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ViewPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchDocumentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchDocumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDocumentFragment extends Fragment {

    FragmentSearchDocumentBinding binding;
    private static final String sortType = "relevance";

    private String keyword = "";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchDocumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDocumentFragment newInstance(String param1, String param2) {
        SearchDocumentFragment fragment = new SearchDocumentFragment();
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
            keyword = getArguments().getString("SEARCH_QUERY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentSearchDocumentBinding.inflate(inflater, container, false);
        binding.viewPager2.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), getLifecycle(), keyword));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Liên quan"); break;
                case 1: tab.setText("Mới nhất"); break;
                case 2: tab.setText("Mua nhiều nhất"); break;
                case 3: tab.setText("Tải nhiều nhất"); break;
                case 4: tab.setText("Xem nhiều nhất"); break;
                case 5: tab.setText("Giá thấp → cao"); break;
                case 6: tab.setText("Giá cao → thấp"); break;
            }
        }).attach();
        handlerTabLayout();

        return binding.getRoot();
    }


    private void handlerTabLayout(){
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
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