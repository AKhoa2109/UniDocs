    package vn.anhkhoa.projectwebsitebantailieu.fragment;
    import android.os.Bundle;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.recyclerview.widget.StaggeredGridLayoutManager;

    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;
    import java.util.ArrayList;
    import java.util.List;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
    import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
    import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
    import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentFilterDocumentBinding;
    import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
    import vn.anhkhoa.projectwebsitebantailieu.utils.LoadingDialog;
    import vn.anhkhoa.projectwebsitebantailieu.utils.ShareViewModel;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link FilterDocumentFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class FilterDocumentFragment extends Fragment {
        private FragmentFilterDocumentBinding binding;
        private DocumentAdapter documentAdapter;
        private List<DocumentDto> documents;
        private String sortType;
        private String keyword;
        private ShareViewModel shareViewModel;

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        public FilterDocumentFragment() {
            // Required empty public constructor
        }

        // TODO: Rename and change types and number of parameters
        public static FilterDocumentFragment newInstance(String sortType, String keyword) {
            FilterDocumentFragment fragment = new FilterDocumentFragment();
            Bundle args = new Bundle();
            args.putString("sortType", sortType);
            args.putString("keyword", keyword);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            shareViewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
                sortType = getArguments().getString("sortType");
                keyword = getArguments().getString("keyword");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentFilterDocumentBinding.inflate(inflater, container, false);
            initViews();
            updateSearchQuery(keyword);
            shareViewModel.getFilterCriteria().observe(getViewLifecycleOwner(), criteria -> {
                Long[] cateIds = criteria != null ? criteria.categoryId : null;
                Double min = criteria != null ? criteria.minPrice : null;
                Double max = criteria != null ? criteria.maxPrice : null;
                Integer[] rates = criteria != null ? criteria.rating : null;
                loadDocuments(keyword,cateIds, min, max, rates);
            });
            return binding.getRoot();
        }


        private void initViews(){
            documents = new ArrayList<>();
            documentAdapter = new DocumentAdapter(getContext(),documents);
            binding.rvFilterDocument.setAdapter(documentAdapter);
            binding.rvFilterDocument.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        public void updateSearchQuery(String query){
            loadDocuments(query,null, null, null, null);
        }


        public void loadDocuments(String keyword,Long[] categoryId, Double minPrice, Double maxPrice, Integer[] rating) {
            Toast.makeText(getContext(), sortType, Toast.LENGTH_SHORT).show();
            ApiService.apiService.filterDocument(keyword,sortType,categoryId,minPrice,maxPrice,rating).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
                @Override
                public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<DocumentDto> data = response.body().getData();

                        // Xử lý cả trường hợp data null hoặc rỗng
                        if (data != null && !data.isEmpty()) {
                            binding.tvNotFound.setVisibility(View.GONE);
                            documents.clear();
                            documents.addAll(data);
                            documentAdapter.notifyDataSetChanged();
                        } else {
                            // Hiển thị thông báo khi không có dữ liệu
                            binding.tvNotFound.setVisibility(View.VISIBLE);
                            documents.clear();
                            documentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Hiển thị thông báo khi response lỗi
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        documents.clear();
                        documentAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }