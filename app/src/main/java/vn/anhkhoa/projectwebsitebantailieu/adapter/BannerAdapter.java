package vn.anhkhoa.projectwebsitebantailieu.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import vn.anhkhoa.projectwebsitebantailieu.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private List<String> images;

    public BannerAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(images.get(position))
                .centerCrop()
                .into(holder.bannerImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView bannerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}
