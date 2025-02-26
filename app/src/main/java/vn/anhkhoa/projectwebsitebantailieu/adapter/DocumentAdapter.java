package vn.anhkhoa.projectwebsitebantailieu.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

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
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<DocumentDto> documents;

    public DocumentAdapter(List<DocumentDto> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentDto documentDto = documents.get(position);
        if (documentDto == null)
            return;
//        holder.imgDocument.setImageResource(R.drawable.facebook_icon);
        Glide.with(holder.itemView.getContext())
                .load(documentDto.getDoc_image_url())
                        .into(holder.imgDocument);
        holder.tvDocName.setText(documentDto.getDoc_name());
        holder.tvSellPrice.setText(String.valueOf(documentDto.getSell_price()));
    }

    @Override
    public int getItemCount() {
        if (documents != null)
            return documents.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgDocument;
        private TextView tvDocName, tvSellPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDocument = itemView.findViewById(R.id.img_document);
            tvDocName = itemView.findViewById(R.id.tv_doc_name);
            tvSellPrice = itemView.findViewById(R.id.tv_sell_price);
        }
    }
}
