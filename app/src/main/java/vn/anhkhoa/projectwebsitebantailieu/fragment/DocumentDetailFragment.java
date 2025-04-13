package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.library.foysaltech.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.library.foysaltech.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DetailPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.SliderAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentDocumentDetailBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentImageDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;
import vn.anhkhoa.projectwebsitebantailieu.utils.NumberFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocumentDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocumentDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentDocumentDetailBinding binding;
    private DocumentDto documentDto;

    private SliderAdapter sliderAdapter;
    private List<DocumentImageDto> images;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DocumentDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DocumentDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocumentDetailFragment newInstance(String param1, String param2) {
        DocumentDetailFragment fragment = new DocumentDetailFragment();
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
        binding = FragmentDocumentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = view.findViewById(R.id.fragment_document_detail_layout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            documentDto = (DocumentDto) bundle.getSerializable("document");
        }

        images = new ArrayList<>();

        getApiImageByDocumentId(documentDto.getDocId());
        getApiDocumentDetail(documentDto.getDocId());

    }

    private void getApiDocumentDetail(Long id){
        ApiService.apiService.getDocumentDetail(id).enqueue(new Callback<ResponseData<DocumentDto>>() {
            @Override
            public void onResponse(Call<ResponseData<DocumentDto>> call, Response<ResponseData<DocumentDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.d("API_RESPONSE", new Gson().toJson(response.body()));
                    ResponseData<DocumentDto> data = response.body();
                    documentDto = data.getData();
                    bindDataToView(documentDto);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<DocumentDto>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getApiImageByDocumentId(Long id){
        ApiService.apiService.getAllImageByDocumentId(id).enqueue(new Callback<ResponseData<List<DocumentImageDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentImageDto>>> call, Response<ResponseData<List<DocumentImageDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    ResponseData<List<DocumentImageDto>> data = response.body();
                    images = data.getData();
                    handlerSliderView(images);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentImageDto>>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindDataToView(DocumentDto documentDto){
        String sellPrice = CurrentFormatter.format(documentDto.getSellPrice());
        String originalPrice = CurrentFormatter.format(documentDto.getOriginalPrice()) ;
        String sold = NumberFormatter.formatterNum(documentDto.getTotalSold()) + " lượt mua";
        binding.tvProductName.setText(documentDto.getDocName());
        binding.tvSellPrice.setText(sellPrice);
        binding.tvOriginalPrice.setText(originalPrice);
        binding.tvNummBuy.setText(sold);
        DetailPagerAdapter adapter = new DetailPagerAdapter(requireActivity(),documentDto);
        binding.viewPager2.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Mô tả");
                            break;
                        case 1:
                            tab.setText("Đánh giá");
                            break;
                        case 2:
                            tab.setText("Liên quan");
                            break;
                    }
                }).attach();
    }

    private void handlerSliderView(List<DocumentImageDto> images){
        sliderAdapter = new SliderAdapter(getContext(), images);
        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.colorSecondary));
        binding.imageSlider.setIndicatorUnselectedColor(Color.BLACK);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }
}