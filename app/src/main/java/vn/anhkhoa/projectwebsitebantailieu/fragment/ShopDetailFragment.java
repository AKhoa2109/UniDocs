package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.TopDocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopDetailBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopDetailFragment extends Fragment {
    private SessionManager sessionManager;
    private List<DocumentDto> topDocuments, documents;
    FragmentShopDetailBinding binding;
    private TopDocumentAdapter topDocAdapter;
    private DocumentAdapter documentAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopDetailFragment newInstance(String param1, String param2) {
        ShopDetailFragment fragment = new ShopDetailFragment();
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
        binding = FragmentShopDetailBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        bindView();
        getApiTop3Document();
        getApiAllDocumentByUserId();
        return binding.getRoot();
    }

    private void bindView(){
        topDocuments = new ArrayList<>();
        documents = new ArrayList<>();
        documentAdapter = new DocumentAdapter(getContext(), documents);
        topDocAdapter = new TopDocumentAdapter(getContext(), topDocuments);
        LinearLayoutManager linearLayoutCateManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        binding.rvTopDocument.setLayoutManager(linearLayoutCateManager);
        binding.rvAllDocument.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvTopDocument.setAdapter(topDocAdapter);
        binding.rvAllDocument.setAdapter(documentAdapter);
    }

    private void getApiTop3Document(){
        ApiService.apiService.getTopDocument(sessionManager.getUser().getUserId(),3).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful()){
                    topDocuments.clear();
                    topDocuments.addAll(response.body().getData());
                    topDocAdapter.notifyDataSetChanged();
                }
                else{
                    ToastUtils.show(getContext(), "Lỗi lấy dữ liệu","#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi kết nối","#FF0000");
            }
        });
    }

    private void getApiAllDocumentByUserId(){
        ApiService.apiService.getAllDocumentByUserId(sessionManager.getUser().getUserId()).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful()){
                    documents.clear();
                    documents.addAll(response.body().getData());
                    documentAdapter.notifyDataSetChanged();
                }
                else{
                    ToastUtils.show(getContext(), "Lỗi lấy dữ liệu","#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi kết nối","#FF0000");
            }
        });
    }
}