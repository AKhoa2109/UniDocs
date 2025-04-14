package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentRelevanceBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelevanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelevanceFragment extends Fragment {
    private FragmentRelevanceBinding binding;

    private DocumentDto documentDto;
    private List<DocumentDto> documentRelevance;
    private List<DocumentDto> documentShop;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RelevanceFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RelevanceFragment newInstance(DocumentDto documentDto) {
        RelevanceFragment fragment = new RelevanceFragment();
        Bundle args = new Bundle();
        args.putSerializable("document", documentDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentDto = (DocumentDto) getArguments().getSerializable("document");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRelevanceBinding.inflate(inflater, container, false);

        documentRelevance = new ArrayList<>();
        documentShop = new ArrayList<>();

        binding.rvRelevanceDocument.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvShopProducts.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        getApiRelevance("cate",documentDto.getCateId(),documentDto.getDocId());
        getApiShopRelevance("user",documentDto.getUserId(),documentDto.getDocId());
        return binding.getRoot();
    }

    private void getApiRelevance(String type, Long id, Long docId){
        ApiService.apiService.getRelevanceDocument(type, id, docId).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    documentRelevance.clear();
                    documentRelevance = response.body().getData();
                    binding.rvRelevanceDocument.setAdapter(new DocumentAdapter(getContext(),documentRelevance));
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void getApiShopRelevance(String type, Long id, Long docId){
        ApiService.apiService.getRelevanceDocument(type, id, docId).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    documentShop.clear();
                    documentShop = response.body().getData();
                    binding.rvShopProducts.setAdapter(new DocumentAdapter(getContext(),documentShop));
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}