package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentDescriptionBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.FileDocument;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserInfoDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionFragment extends Fragment {

    private FragmentDescriptionBinding binding;

    private DocumentDto documentDto;
    private FileDocument fileDocument;
    private UserInfoDto userInfoDto;

    private CategoryDto categoryDto;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public DescriptionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DescriptionFragment newInstance(DocumentDto documentDto) {
        DescriptionFragment fragment = new DescriptionFragment();
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
        binding =  FragmentDescriptionBinding.inflate(inflater, container, false);
        Long userId = documentDto.getUserId();
        getApiLoadShopInfo(userId);
        getApiLoadCategory(documentDto.getCateId());
        getApiFileDocument(documentDto.getDocId());
        handlerShowFile();
        return binding.getRoot();
    }
    
    private void getApiLoadShopInfo(Long userId){
        ApiService.apiService.getShopDetail(userId).enqueue(new Callback<ResponseData<UserInfoDto>>() {
            @Override
            public void onResponse(Call<ResponseData<UserInfoDto>> call, Response<ResponseData<UserInfoDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    userInfoDto = response.body().getData();
                    setBindDataView(userInfoDto);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserInfoDto>> call, Throwable t) {

            }
        });
    }

    boolean check = false;
    private void getApiLoadCategory(Long categoryId){
        ApiService.apiService.getDocumentById(categoryId).enqueue(new Callback<ResponseData<CategoryDto>>() {
            @Override
            public void onResponse(Call<ResponseData<CategoryDto>> call, Response<ResponseData<CategoryDto>> response) {
                if(response.isSuccessful() && response.body() != null){

                    categoryDto = response.body().getData();
                    binding.tvCategory.setText("Loại: "+categoryDto.getCateName());
                }

            }

            @Override
            public void onFailure(Call<ResponseData<CategoryDto>> call, Throwable t) {

            }
        });
    }

    private void setBindDataView(UserInfoDto userInfoDto){
        String page = "Số trang: " + documentDto.getDocPage();
        Long rQuantity = documentDto.getMaxQuantity() - documentDto.getTotalSold();
        String remainingQuantity = "Số lượng còn lại: " + rQuantity;
        String numView = "Số luợt xem: " + documentDto.getView();
        String numDownload = "Số luợt tải: " + documentDto.getDownload();
        String docDesc = documentDto.getDocDesc();
        binding.tvTotalProduct.setText(userInfoDto.getTotalProduct()+"");
        binding.tvTotalProductSale.setText(userInfoDto.getTotalProductSale()+"");
        binding.tvTotalReview.setText(userInfoDto.getTotalReview()+"");
        binding.tvSellerName.setText(userInfoDto.getName());
        binding.tvSellerAddress.setText(userInfoDto.getAddress());
        binding.tvNumPage.setText(page);
        binding.tvRemainingQuantity.setText(remainingQuantity);
        binding.tvNumView.setText(numView);
        binding.tvNumDownload.setText(numDownload);
        binding.tvDocDesc.setText(docDesc);
    }

    private void getApiFileDocument(Long docId){
        ApiService.apiService.getFileDocumentByDocumentId(docId).enqueue(new Callback<ResponseData<FileDocument>>() {
            @Override
            public void onResponse(Call<ResponseData<FileDocument>> call, Response<ResponseData<FileDocument>> response) {
                if(response.isSuccessful() && response.body().getData() != null){
                    FileDocument file = response.body().getData();
                    fileDocument = new FileDocument(file.getFileId(), file.getFileUrl(), file.getFileType(), documentDto.getDocName(), documentDto.getDocPage());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<FileDocument>> call, Throwable t) {
                ToastUtils.show(getContext(), "Lỗi tải file");
            }
        });
    }

    private void handlerShowFile(){
        binding.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getContext() instanceof MainActivity){
                    ((MainActivity) getContext()).openFileDocumentFragment(fileDocument, documentDto);
                }
            }
        });
    }

}