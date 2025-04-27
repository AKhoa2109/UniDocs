package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class CategoryShopAdapter extends RecyclerView.Adapter<CategoryShopAdapter.ViewHolder> {

    private final List<CategoryDto> categories;

    public CategoryShopAdapter(List<CategoryDto> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CategoryDto category = categories.get(position);
        holder.bind(category,holder);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvName, tvCount;
        private final ImageButton btnToggle;
        private final RecyclerView rvProducts;
        private boolean isExpanded = false;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvCount = itemView.findViewById(R.id.tvProductCount);
            btnToggle = itemView.findViewById(R.id.ibDropdown);
            rvProducts = itemView.findViewById(R.id.rvProducts);
        }

        public void bind(CategoryDto category,ViewHolder holder) {
            tvName.setText(category.getCateName());
            tvCount.setText("(" + category.getDocs().size() + ")");

            Glide.with(ivIcon.getContext())
                    .load(category.getCateIcon())
                    .into(ivIcon);

            rvProducts.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            btnToggle.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                btnToggle.animate()
                        .rotation(isExpanded ? 180 : 0)
                        .setDuration(300)
                        .start();
                if (isExpanded) {
                    if (rvProducts.getAdapter() == null) {
                        rvProducts.setLayoutManager(new LinearLayoutManager(
                                itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                        rvProducts.setAdapter(new DocumentShopAdapter(category.getDocs(),holder.itemView.getContext()));
                    }
                    expandView(rvProducts);
                } else {
                    collapseView(rvProducts);
                }
            });
        }

        private void expandView(View view) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.setTranslationY(-20);
            view.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        }

        private void collapseView(View view) {
            view.animate()
                    .translationY(-20)
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> view.setVisibility(View.GONE))
                    .start();
        }
    }
}
