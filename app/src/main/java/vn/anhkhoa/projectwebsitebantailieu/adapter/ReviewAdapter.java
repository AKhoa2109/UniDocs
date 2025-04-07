package vn.anhkhoa.projectwebsitebantailieu.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.anhkhoa.projectwebsitebantailieu.R;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewDto review = reviews.get(position);

        Glide.with(holder.itemView.getContext()).load(review.getAvatar()).into(holder.civReviewerAvatar);
        holder.tvReviewerName.setText(review.getName());
        holder.tvReviewDate.setText(review.getCreatedAt().toLocalDate().toString());
        holder.ratingBar.setRating(review.getRate());
        holder.tvReviewContent.setText(review.getContent());

        // Gắn adapter cho review criterial
        criterials = new ArrayList<>();
        criterials = review.getCriterialDtos();
        holder.rvReviewCriterical.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.VERTICAL,false));
        ReviewCriterialAdapter criterialAdapter = new ReviewCriterialAdapter(criterials);
        holder.rvReviewCriterical.setAdapter(criterialAdapter);
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civReviewerAvatar;
        TextView tvReviewerName, tvReviewDate, tvReviewContent;
        RatingBar ratingBar;
        RecyclerView rvReviewCriterical;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civReviewerAvatar = itemView.findViewById(R.id.ivReviewerAvatar);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rvReviewCriterical = itemView.findViewById(R.id.rvReviewCriterical);
        }
    }
}
