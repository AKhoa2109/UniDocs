package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DiscountAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentVoucherBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherFragment extends Fragment {
    private FragmentVoucherBinding binding;
    private List<DiscountDto> discountDtos;
    private DocumentDto documentDto;
    private List<Long> documentIds;
    private List<Long> userIds;
    private List<Long> categoryIds;
    private List<CartDto> cartDtos;
    private DiscountAdapter discountAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VoucherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoucherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherFragment newInstance(String param1, String param2) {
        VoucherFragment fragment = new VoucherFragment();
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
         binding = FragmentVoucherBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
           cartDtos  = (List<CartDto>) bundle.getSerializable("cart");
        }
        discountDtos = new ArrayList<>();
        handlerGetIdDocumnet(cartDtos);
         return binding.getRoot();
    }

    private DocumentDto getApiDocumentInfo(Long docId){
        ApiService.apiService.getDiscountDocument(docId).enqueue(new Callback<ResponseData<DocumentDto>>() {
            @Override
            public void onResponse(Call<ResponseData<DocumentDto>> call, Response<ResponseData<DocumentDto>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    documentDto = response.body().getData();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<DocumentDto>> call, Throwable t) {

            }
        });
        return documentDto;
    }

    public void getApiDiscountByScope(){
        ApiService.apiService.getDiscountByScope(userIds,categoryIds,documentIds).enqueue(new Callback<ResponseData<List<DiscountDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DiscountDto>>> call, Response<ResponseData<List<DiscountDto>>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    discountDtos = response.body().getData();
                    discountAdapter = new DiscountAdapter(discountDtos);
                    binding.rvDiscount.setAdapter(discountAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DiscountDto>>> call, Throwable t) {

            }
        });
    }

    private void handlerGetIdDocumnet(List<CartDto> cartDto){
        for(CartDto c : cartDto){
            DocumentDto docDto = getApiDocumentInfo(c.getDocId());
            documentIds.add(docDto.getDoc_id());
            categoryIds.add(docDto.getCateId());
            userIds.add(docDto.getUserId());
        }
    }
}