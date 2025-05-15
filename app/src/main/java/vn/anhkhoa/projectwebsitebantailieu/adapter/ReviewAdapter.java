package vn.anhkhoa.projectwebsitebantailieu.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.stream.Collectors;

import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemReviewBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ImageFullscreenFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.FileMedia;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewCriterialDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewDto;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private List<ReviewDto> reviews;
    List<ReviewCriterialDto> criterials;

    public ReviewAdapter(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewDto review = reviews.get(position);

        //avata
        Glide.with(holder.itemView.getContext()).load(review.getAvatar()).into(holder.binding.imgAvatar);
        holder.binding.tvUserName.setText(review.getName());
        holder.binding.ratingBar.setRating(review.getRate());

        //hinh anh/video
        LinearLayoutManager lm = new LinearLayoutManager(
                holder.itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        holder.binding.rvMedia.setLayoutManager(lm);

        //adapter
        List<FileMedia> list = review.getFileMedias();  // từ data model
        MediaAdapter adapter = new MediaAdapter(
                holder.itemView.getContext(),
                list,
                media -> {
                    if (media.getFileType() == FileType.IMAGE) {
                        // ví dụ: show full-screen image Activity/Fragment
                        openImageViewer(media.getFileUrl(), holder.itemView.getContext());
                    } else {
                        // play video
//                        openVideoPlayer(media.getFileUrl());
                    }
                }
        );
        holder.binding.rvMedia.setAdapter(adapter);

        // Gắn adapter cho review criterial
        review.getFileMedias();
//        review.getCriterialDtos();
        List<ReviewCriterialDto> listCriter = review.getCriterialDtos();
        String formatted = listCriter.stream()
                .map(dto -> dto.getName() + ": " + dto.getContent())
                .collect(Collectors.joining("\n"));
        holder.binding.tvCriterial.setText(formatted);

        if(review.getCreatedAt() != null)
            holder.binding.tvReviewDate.setText(review.getCreatedAt().toLocalDate().toString());

        holder.binding.tvReviewContent.setText(review.getContent());
    }

    private void openImageViewer(String url, Context context){
        Context ctx = context;
        if (ctx instanceof MainActivity) {
            MainActivity main = (MainActivity) ctx;
            ImageFullscreenFragment imageFullscreenFragment = ImageFullscreenFragment.newInstance(url);
            main.showFragment(imageFullscreenFragment, "imageFullscreenFragment");
        }
    }

    @Override
    public int getItemCount() {

        return reviews != null ? reviews.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        CircleImageView civReviewerAvatar;
//        TextView tvReviewerName, tvReviewDate, tvReviewContent;
//        RatingBar ratingBar;
//        RecyclerView rvReviewCriterical;
        ItemReviewBinding binding;

        public ViewHolder(@NonNull ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
//            civReviewerAvatar = itemView.findViewById(R.id.ivReviewerAvatar);
//            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
//            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
//            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
//            ratingBar = itemView.findViewById(R.id.ratingBar);
//            rvReviewCriterical = itemView.findViewById(R.id.rvReviewCriterical);
        }
    }
}
