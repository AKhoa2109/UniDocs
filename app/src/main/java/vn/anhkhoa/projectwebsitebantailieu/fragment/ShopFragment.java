package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ShopPagerAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserInfoDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {

    FragmentShopBinding binding;
    private ShopPagerAdapter shopPagerAdapter;
    private SessionManager sessionManager;
    private UserInfoDto userInfoDto;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentShopBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        shopPagerAdapter = new ShopPagerAdapter(requireActivity());
        binding.viewPager2.setAdapter(shopPagerAdapter);
        getApiLoadShopInfo(sessionManager.getUser().getUserId());
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Shop");
                            break;
                        case 1:
                            tab.setText("Sản phẩm");
                            break;
                        case 2:
                            tab.setText("Danh mục");
                            break;
                    }
                }).attach();
        binding.searchView.setFocusable(false);
        binding.searchView.setOnClickListener(v -> openSearchShopFragment());
        return binding.getRoot();

    }

    private void getApiLoadShopInfo(Long userId){
        ApiService.apiService.getShopDetail(userId).enqueue(new Callback<ResponseData<UserInfoDto>>() {
            @Override
            public void onResponse(Call<ResponseData<UserInfoDto>> call, Response<ResponseData<UserInfoDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    userInfoDto = response.body().getData();
                    bindView(userInfoDto);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserInfoDto>> call, Throwable t) {

            }
        });
    }

    private void bindView(UserInfoDto userInfoDto){
        Glide.with(getContext()).load(userInfoDto.getAvatar()).into(binding.shopAvatar);
        binding.shopName.setText(userInfoDto.getName());
        binding.tvTotalProduct.setText(userInfoDto.getTotalProduct()+"");
        binding.tvTotalSellProduct.setText(userInfoDto.getTotalProductSale()+"");
        binding.tvReview.setText(userInfoDto.getTotalReview()+"");
    }

    private void openSearchShopFragment(){
        if(getContext() instanceof MainActivity){
            ((MainActivity) getContext()).openSearchShopFragment();
        }
    }
}