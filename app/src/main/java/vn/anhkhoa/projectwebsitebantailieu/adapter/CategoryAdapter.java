package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<CategoryDto> categories;

    public CategoryAdapter(List<CategoryDto> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryDto categoryDto = categories.get(position);
        if (categoryDto == null)
            return;
//        holder.imgDocument.setImageResource(R.drawable.facebook_icon);
        Glide.with(holder.itemView.getContext())
                .load(categoryDto.getCate_icon())
                .into(holder.ivCategoryIcon);
        holder.tvCategoryName.setText(categoryDto.getCate_name());
    }

    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivCategoryIcon;
        private TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}

