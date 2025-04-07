package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewCriterialDto;

public class ReviewCriterialAdapter extends RecyclerView.Adapter<ReviewCriterialAdapter.ViewHolder>{
    private List<ReviewCriterialDto> criterials;

    public ReviewCriterialAdapter(List<ReviewCriterialDto> criterials) {
        this.criterials = criterials;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_criterial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewCriterialDto criterial = criterials.get(position);
        String s = criterial.getName()+" : "+criterial.getContent();
        Log.d("TEST",s);
        holder.tvCriterial.setText(s);
    }

    @Override
    public int getItemCount() {
        return criterials != null ? criterials.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCriterial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCriterial = itemView.findViewById(R.id.tvCriterial);
        }
    }
}
