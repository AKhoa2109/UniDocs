package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.SearchActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.BannerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.CategoryAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentHomeBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private ViewPager2 viewPager;

    WormDotsIndicator dotsIndicator;
    private Handler sliderHandler = new Handler();
    private List<String> bannerImages = new ArrayList<>();
    private Runnable sliderRunnable;

    private EditText edtSearch;
    RecyclerView rcvDocument;
    RecyclerView rcvCate;

    List<DocumentDto> documentDtos = new ArrayList<>();
    List<CategoryDto> categoryDtos = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rcvCate = view.findViewById(R.id.rcvCategory);
        rcvDocument = view.findViewById(R.id.rcvDocument);
        viewPager = view.findViewById(R.id.viewPager);
        dotsIndicator = view.findViewById(R.id.dotsIndicator);
        edtSearch = view.findViewById(R.id.edtSearch);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        initBannerImages();
        setupViewPager();

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvDocument.setLayoutManager(linearLayoutManager);

        LinearLayoutManager  linearLayoutCateManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        rcvCate.setLayoutManager(linearLayoutCateManager);

        documentDtos = new ArrayList<>();
        callApiGetListDocument();
        callApiGetListCategory();

        return view;
    }

    private void callApiGetListDocument(){
        ApiService.apiService.getListDocument().enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                ResponseData<List<DocumentDto>> data = response.body();

                if(data == null)
                {
                    Toast.makeText(getContext(), "Dữ liệu trả về null", Toast.LENGTH_SHORT).show();
                    return;
                }
                documentDtos.clear();
                documentDtos= data.getData();
                DocumentAdapter documentAdapter = new DocumentAdapter(documentDtos);
                rcvDocument.setAdapter(documentAdapter);
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiGetListCategory(){
        ApiService.apiService.getListCategory().enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<CategoryDto>>> call, Response<ResponseData<List<CategoryDto>>> response) {
                if(response.isSuccessful()){
                    ResponseData<List<CategoryDto>> data = response.body();
                    if(data == null)
                    {
                        Toast.makeText(getContext(), "Dữ liệu trả về null", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    categoryDtos.clear();
                    categoryDtos= data.getData();
                    CategoryAdapter categoryAdapter = new CategoryAdapter(categoryDtos);
                    rcvCate.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error" +t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initBannerImages() {
        // Thêm dữ liệu ảnh mẫu (thay thế bằng dữ liệu thực tế)
        bannerImages.add("https://as2.ftcdn.net/jpg/04/32/48/41/1000_F_432484133_WnmdDXCWu7i2tR90K7flMtnJ4zOOfnM2.jpg");
        bannerImages.add("https://static.vecteezy.com/system/resources/previews/023/107/446/non_2x/promo-sale-banner-with-reading-stack-of-books-wooden-letter-tiles-school-books-pile-world-book-day-bookstore-bookshop-library-book-lover-bibliophile-education-a4-for-poster-cover-vector.jpg");
        bannerImages.add("https://www.shutterstock.com/image-vector/promo-sale-banner-library-bookshop-260nw-1790872166.jpg");
    }

    private void setupViewPager() {
        // Khởi tạo Adapter
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        viewPager.setAdapter(bannerAdapter);

        // Kết nối WormDotsIndicator với ViewPager2
        dotsIndicator.attachTo(viewPager);

        // Tự động chuyển slide
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // 3 giây
            }
        });

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition == bannerImages.size() - 1) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(currentPosition + 1);
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

}