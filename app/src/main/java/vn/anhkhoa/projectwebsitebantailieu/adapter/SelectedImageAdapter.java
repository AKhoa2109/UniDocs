package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder> {
    private List<File> images;
    private OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onRemove(File file);
    }

    public SelectedImageAdapter(List<File> images, OnImageRemoveListener removeListener) {
        this.images = images;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        File file = images.get(position);
        Glide.with(holder.itemView.getContext()).load(file).into(holder.img);
        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) removeListener.onRemove(file);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageButton btnRemove;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSelected);
            btnRemove = itemView.findViewById(R.id.btnRemoveImage);
        }
    }
}