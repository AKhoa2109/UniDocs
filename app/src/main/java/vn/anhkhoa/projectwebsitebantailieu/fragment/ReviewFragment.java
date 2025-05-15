package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.ReviewAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentReviewBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {
    private FragmentReviewBinding binding;

    // Danh sách các đánh giá (review) của tài liệu
    private List<ReviewDto> reviews;

    // Lưu số lượng đánh giá cho từng mức sao (1-5)
    private Map<Integer,Long> rateCount;
    // Thông tin tài liệu đang được xem đánh giá
    private DocumentDto documentDto;
    private ReviewAdapter reviewAdapter;

    // Các hằng số dùng cho Bundle arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReviewFragment() {
        // Required empty public constructor
    }

    // Hàm tạo instance mới của Fragment, truyền vào DocumentDto
    public static ReviewFragment newInstance(DocumentDto documentDto) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("document", documentDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy dữ liệu DocumentDto từ arguments
        if (getArguments() != null) {
            documentDto = (DocumentDto) getArguments().getSerializable("document");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo ViewBinding và inflate layout
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        // Gọi API lấy danh sách review
        getApiReviewById(documentDto.getDocId());
    }

    // Gọi API lấy danh sách review của tài liệu
    private void getApiReviewById(Long docId) {
        ApiService.apiService.getReviewsByDocumentId(docId).enqueue(new Callback<ResponseData<List<ReviewDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<ReviewDto>>> call, Response<ResponseData<List<ReviewDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    // Lấy danh sách review từ response
                    List<ReviewDto> newData = response.body().getData();

                    // Xóa hết data cũ
                    reviews.clear();
                    // Thêm toàn bộ data mới
                    reviews.addAll(newData);
                    reviewAdapter.notifyDataSetChanged();

                    updateReviewSummary(reviews);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<ReviewDto>>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị tổng số đánh giá, điểm trung bình, và RatingBar trung bình lên giao diện
    private void initView(){
        String numReview = documentDto.getTotalReview()+" đánh giá";
        String avgRate = documentDto.getAvgRate()+"";
        binding.tvTotalReviews.setText(numReview);
        binding.tvAverageRating.setText(avgRate);
        binding.ratingBarAverage.setRating(Float.valueOf(avgRate));

        reviews = new ArrayList<>();
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        reviewAdapter = new ReviewAdapter(reviews);
        binding.rvReviews.setAdapter(reviewAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
        );
        binding.rvReviews.addItemDecoration(decoration);
        reviewAdapter.notifyDataSetChanged();


    }

    // Cập nhật các ProgressBar thể hiện tỷ lệ từng mức sao
    private void updateProgressBars(Map<Integer, Long> rateMap) {
        long total = 0;
        for (Long count : rateMap.values()) {
            total += count;
        }

        if (total == 0) return;

        setProgressBar(R.id.pb5Star, rateMap.getOrDefault(5, 0L), total);
        setProgressBar(R.id.pb4Star, rateMap.getOrDefault(4, 0L), total);
        setProgressBar(R.id.pb3Star, rateMap.getOrDefault(3, 0L), total);
        setProgressBar(R.id.pb2Star, rateMap.getOrDefault(2, 0L), total);
        setProgressBar(R.id.pb1Star, rateMap.getOrDefault(1, 0L), total);
    }

    // Thiết lập giá trị phần trăm cho từng ProgressBar mức sao
    private void setProgressBar(int progressBarId, long count, long total) {
        ProgressBar progressBar = getView().findViewById(progressBarId);
        int percent = (int) ((count * 100.0f) / total);
        progressBar.setProgress(percent);
    }


    private void updateReviewSummary(List<ReviewDto> reviews) {
        // 1. Tổng số review
        int totalReviews = reviews.size();

        // 2. Tổng điểm để tính trung bình
        float sumRate = 0f;
        // 3. Đếm số sao mỗi mức
        int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0;

        for (ReviewDto r : reviews) {
            int rate = r.getRate();
            sumRate += rate;
            switch (rate) {
                case 5: count5++; break;
                case 4: count4++; break;
                case 3: count3++; break;
                case 2: count2++; break;
                case 1: count1++; break;
            }
        }

        // 4. Tính điểm trung bình
        float avgRate = totalReviews > 0
                ? sumRate / totalReviews
                : 0f;

        // 5. Cập nhật UI
        // Tổng đánh giá
        binding.tvTotalReviews.setText(totalReviews + " đánh giá");

        // Điểm trung bình (1 chữ số thập phân)
        String avgText = String.format(Locale.getDefault(), "%.1f", avgRate);
        binding.tvAverageRating.setText(avgText);

        // RatingBar trung bình
        binding.ratingBarAverage.setRating(avgRate);

        // 6. Cập nhật ProgressBars — dùng % theo tổng (max mặc định = 100)
        if (totalReviews > 0) {
            binding.pb5Star.setProgress(count5 * 100 / totalReviews);
            binding.pb4Star.setProgress(count4 * 100 / totalReviews);
            binding.pb3Star.setProgress(count3 * 100 / totalReviews);
            binding.pb2Star.setProgress(count2 * 100 / totalReviews);
            binding.pb1Star.setProgress(count1 * 100 / totalReviews);
        } else {
            // nếu không có đánh giá nào
            binding.pb5Star.setProgress(0);
            binding.pb4Star.setProgress(0);
            binding.pb3Star.setProgress(0);
            binding.pb2Star.setProgress(0);
            binding.pb1Star.setProgress(0);
        }
    }

}