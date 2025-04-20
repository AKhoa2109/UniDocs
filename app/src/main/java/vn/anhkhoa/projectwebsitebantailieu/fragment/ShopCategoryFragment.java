package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.CategoryShopAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopCategoryBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopCategoryFragment extends Fragment {
    FragmentShopCategoryBinding binding;
    private SessionManager sessionManager;
    private List<CategoryDto> categoryDtos;
    private CategoryShopAdapter categoryShopAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopCategoryFragment newInstance(String param1, String param2) {
        ShopCategoryFragment fragment = new ShopCategoryFragment();
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
        binding =  FragmentShopCategoryBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        initView();
        getApiCategoryByUserId(sessionManager.getUser().getUserId());
        return binding.getRoot();
    }

    private void initView(){
        categoryDtos = new ArrayList<>();
        binding.rvAllCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        categoryShopAdapter = new CategoryShopAdapter(categoryDtos);
        binding.rvAllCategory.setAdapter(categoryShopAdapter);
    }

    private void getApiCategoryByUserId(Long userId){
        ApiService.apiService.getCategoryByUserId(userId).enqueue(new Callback<ResponseData<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<CategoryDto>>> call, Response<ResponseData<List<CategoryDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    categoryDtos.clear();
                    categoryDtos.addAll(response.body().getData());
                    categoryShopAdapter.notifyDataSetChanged();
                }
                else{
                    ToastUtils.show(getContext(), "Lỗi lấy dữ liệu","#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<CategoryDto>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi kết nối","#FF0000");
            }
        });
    }
}