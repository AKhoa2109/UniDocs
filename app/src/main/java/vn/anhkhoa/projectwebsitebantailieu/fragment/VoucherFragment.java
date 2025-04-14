package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.CartAdapter;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DiscountAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentVoucherBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CartViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherFragment extends Fragment implements DiscountAdapter.Listener{
    private FragmentVoucherBinding binding;
    private List<DiscountDto> discountDtos;
    private DocumentDto documentDto;
    private List<Long> documentIds;
    private List<Long> userIds;
    private List<Long> categoryIds;
    private List<CartDto> cartDtos;
    private DiscountAdapter discountAdapter;
    private CartViewModel cartViewModel;
    private DiscountDto selectedDiscount;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VoucherFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VoucherFragment newInstance() {
        VoucherFragment fragment = new VoucherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVoucherBinding.inflate(inflater, container, false);
        initView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            cartDtos  = (List<CartDto>) bundle.getSerializable("cart");
        }
        handlerGetIdDocumnet(cartDtos, documentIds, categoryIds, userIds);
        double totalPrice = calculateTotalPrice(cartDtos);
        discountAdapter.setTotalCartPrice(totalPrice);
        handlerApplyVoucher();
        return binding.getRoot();
    }

    private void initView(){
        discountDtos = new ArrayList<>();
        documentIds = new ArrayList<>();
        userIds = new ArrayList<>();
        categoryIds = new ArrayList<>();
        discountAdapter = new DiscountAdapter(discountDtos, this);
        binding.rcVoucher.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcVoucher.setAdapter(discountAdapter);
    }


    public void getApiDiscountByScope(List<Long> userIds, List<Long> categoryIds, List<Long> documentIds){
        ApiService.apiService.getDiscountByScope(userIds,categoryIds,documentIds).enqueue(new Callback<ResponseData<List<DiscountDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DiscountDto>>> call, Response<ResponseData<List<DiscountDto>>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    discountDtos.clear();
                    discountDtos.addAll(response.body().getData());
                    discountAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DiscountDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlerGetIdDocumnet(List<CartDto> cartDto, List<Long> documentIds, List<Long> categoryIds, List<Long> userIds){
        List<Long> docIds = new ArrayList<>();
        for (CartDto c : cartDto) {
            docIds.add(c.getDocId());
        }

        ApiService.apiService.getDiscountDocument(docIds).enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DocumentDto> documentList = response.body().getData();
                    for (DocumentDto doc : documentList) {
                        documentIds.add(doc.getDocId());
                        categoryIds.add(doc.getCateId());
                        userIds.add(doc.getUserId());
                    }
                    getApiDiscountByScope(userIds, categoryIds, documentIds);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi lấy document", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVoucherSelected(DiscountDto voucher) {
        selectedDiscount = voucher;
    }

    private void handlerApplyVoucher() {
        binding.btnConfirmDiscount.setOnClickListener(v -> {
            if (selectedDiscount == null) {
                Toast.makeText(getContext(), "Vui lòng chọn voucher", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi kết quả về CartFragment
            Bundle result = new Bundle();
            result.putSerializable("selectedDiscount", selectedDiscount);
            result.putSerializable("selectedCartItems", (Serializable) cartDtos);
            getParentFragmentManager().setFragmentResult("voucherResult", result);

            // Quay về CartFragment chỉ với 1 lần pop
            getParentFragmentManager().popBackStack();
        });
    }

    private double calculateTotalPrice(List<CartDto> cartDtos) {
        double total = 0;
        for (CartDto c : cartDtos) {
            total += c.getSellPrice()*c.getQuantity();
        }
        return total;
    }
}