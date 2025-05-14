package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchShopBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchShopFragment extends Fragment {
    FragmentSearchShopBinding binding;
    private SessionManager sessionManager;
    private List<DocumentDto> documentDtos,originalList,filteredList;
    private DocumentAdapter documentAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchShopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchShopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchShopFragment newInstance(String param1, String param2) {
        SearchShopFragment fragment = new SearchShopFragment();
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
        binding = FragmentSearchShopBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        initView();
        getApiDocumentByUserId(sessionManager.getUser().getUserId());
        setupSearchView();
        return binding.getRoot();

    }

    private void initView(){
        originalList = new ArrayList<>();
        filteredList = new ArrayList<>();
        documentDtos = new ArrayList<>();
        binding.rvProducts.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        documentAdapter = new DocumentAdapter(getContext(), documentDtos);
        binding.rvProducts.setAdapter(documentAdapter);
    }

    private void getApiDocumentByUserId(Long userId){
        ApiService.apiService.getAllDocumentByUserId(userId).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if(response.isSuccessful() && response.body().getData() != null){
                    originalList.clear();
                    originalList.addAll(response.body().getData());
                    documentDtos.clear();
                    documentDtos.addAll(originalList);
                    documentAdapter.updateList(originalList);
                }
                else{
                    originalList.clear();
                    documentDtos.clear();
                    documentAdapter.updateList(originalList);
                    ToastUtils.show(getContext(), "Lỗi lấy dữ liệu","#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi kết nối","#FF0000");
            }
        });
    }

    private void setupSearchView() {
        binding.searchView.setIconified(false);
        binding.searchView.requestFocus();

        // Hiện bàn phím
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT);

        binding.searchView.setOnCloseListener(() -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Ẩn bàn phím khi submit
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);

                filterProducts(query != null ? query.trim() : "");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText != null ? newText.trim() : "");
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        filteredList.clear();


        if (query == null || query.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (DocumentDto document : originalList) {
                if (document.getDocName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(document);
                }
            }
        }

        updateUI(filteredList);
    }

    private void updateUI(List<DocumentDto> list) {
        if (list.isEmpty()) {
            binding.rvProducts.setVisibility(View.GONE);
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText("Không có tài liệu nào phù hợp");
        } else {
            binding.rvProducts.setVisibility(View.VISIBLE);
            binding.tvEmpty.setVisibility(View.GONE);
            documentAdapter.updateList(list);
        }
    }

}