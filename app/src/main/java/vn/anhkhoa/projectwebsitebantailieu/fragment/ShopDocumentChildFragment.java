package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonConfig;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopDocumentChildBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopDocumentChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopDocumentChildFragment extends Fragment {

    FragmentShopDocumentChildBinding binding;
    private List<DocumentDto> documentDtos;
    private DocumentAdapter documentAdapter;
    private String sortType;
    private SessionManager sessionManager;
    private Skeleton skeleton;

    public ShopDocumentChildFragment() {
        // Required empty public constructor
    }

    public static ShopDocumentChildFragment newInstance(String sortType) {
        ShopDocumentChildFragment fragment = new ShopDocumentChildFragment();
        Bundle args = new Bundle();
        args.putString("sortType", sortType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortType = getArguments().getString("sortType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentShopDocumentChildBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        bindView();
        showSkeleton();
        loadData();
        return binding.getRoot();
    }

    private void bindView(){
        documentDtos = new ArrayList<>();
        binding.rvDocumentFilter.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        documentAdapter = new DocumentAdapter(getContext(),documentDtos);
        binding.rvDocumentFilter.setAdapter(documentAdapter);
    }

    private void showSkeleton() {
        // Apply skeleton using item_document layout as mask, showing 6 placeholder items
        skeleton = SkeletonLayoutUtils.applySkeleton(
                binding.rvDocumentFilter,
                R.layout.item_document,
                6
        );
        skeleton.showSkeleton();
    }

    private void loadData(){
        ApiService.apiService.getAllDocumentByUserIdAndSortType(sortType,sessionManager.getUser().getUserId()).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                skeleton.showOriginal();
                if(response.isSuccessful() && response.body() != null){
                    documentDtos.clear();
                    documentDtos.addAll(response.body().getData());
                    documentAdapter.notifyDataSetChanged();
                }
                else{
                    ToastUtils.show(getContext(), "Lỗi lấy dữ liệu","#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                skeleton.showOriginal();
                ToastUtils.show(getContext(), "Lỗi kết nối","#FF0000");
            }
        });
    }


}