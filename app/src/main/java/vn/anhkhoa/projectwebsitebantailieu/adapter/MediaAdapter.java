package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;
import vn.anhkhoa.projectwebsitebantailieu.model.FileMedia;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    public interface OnMediaClickListener {
        void onMediaClicked(FileMedia media);
    }

    private final List<FileMedia> medias;
    private final Context context;
    private final OnMediaClickListener listener;

    public MediaAdapter(Context context,
                        List<FileMedia> medias,
                        OnMediaClickListener listener) {
        this.context  = context;
        this.medias   = medias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_media, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        FileMedia media = medias.get(pos);

        // 1. Load ảnh hoặc thumbnail video
        if (media.getFileType() == FileType.IMAGE) {
            h.imgPlayIcon.setVisibility(View.GONE);
            Glide.with(context)
                    .load(media.getFileUrl())
                    .into(h.imgMedia);

        } else if(media.getFileType() == FileType.VIDEO){ // VIDEO
            h.imgPlayIcon.setVisibility(View.VISIBLE);
            // Lấy 1 frame thumbnail của video
            Glide.with(context)
                    .asBitmap()                  // tải dưới dạng bitmap
                    .load(media.getFileUrl())
                    .into(h.imgMedia);
        }

        // 2. Bắt sự kiện click
        h.itemView.setOnClickListener(v -> listener.onMediaClicked(media));
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedia, imgPlayIcon;
        ViewHolder(View itemView) {
            super(itemView);
            imgMedia     = itemView.findViewById(R.id.imgMedia);
            imgPlayIcon  = itemView.findViewById(R.id.imgPlayIcon);
        }
    }
}
