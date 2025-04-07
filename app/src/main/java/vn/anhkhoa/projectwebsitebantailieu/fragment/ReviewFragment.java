package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<ReviewDto> reviews;

    private Map<Integer,Long> rateCount;
    private DocumentDto documentDto;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReviewFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            documentDto = (DocumentDto) getArguments().getSerializable("document");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        reviews = new ArrayList<>();
        getApiReviewById(documentDto.getDoc_id());
        setBindDataView();
        getApiRateCount(documentDto.getDoc_id());
        return binding.getRoot();
    }

    private void getApiReviewById(Long docId) {
        ApiService.apiService.getReviewsByDocumentId(docId).enqueue(new Callback<ResponseData<List<ReviewDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<ReviewDto>>> call, Response<ResponseData<List<ReviewDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<ReviewDto> reviews = response.body().getData();
                    binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                    ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
                    binding.rvReviews.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<ReviewDto>>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getApiRateCount(Long docId){
        ApiService.apiService.getRateReportByDocumentId(docId).enqueue(new Callback<ResponseData<Map<Integer, Long>>>() {
            @Override
            public void onResponse(Call<ResponseData<Map<Integer, Long>>> call, Response<ResponseData<Map<Integer, Long>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    rateCount = response.body().getData();
                    updateProgressBars(rateCount);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Map<Integer, Long>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBindDataView(){
        String numReview = documentDto.getTotalReview()+" đánh giá";
        String avgRate = documentDto.getAvgRate()+"";
        binding.tvTotalReviews.setText(numReview);
        binding.tvAverageRating.setText(avgRate);
        binding.ratingBarAverage.setRating(Float.valueOf(avgRate));
    }

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

    private void setProgressBar(int progressBarId, long count, long total) {
        ProgressBar progressBar = getView().findViewById(progressBarId);
        int percent = (int) ((count * 100.0f) / total);
        progressBar.setProgress(percent);
    }

}