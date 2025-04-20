package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;

public class TopDocumentAdapter extends RecyclerView.Adapter<TopDocumentAdapter.ViewHolder>{
    private List<DocumentDto> items = new ArrayList<>();

    private Context context;

    public TopDocumentAdapter(Context context, List<DocumentDto> items) {
        this.context = context;
        this.items = items;
    }


    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_document, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        DocumentDto doc = items.get(pos);
        h.tvTopBadge.setText("TOP "+(pos+1));
        h.tvDocName.setText(doc.getDocName());
        h.tvSellPrice.setText("Giá: " + CurrentFormatter.format(doc.getSellPrice()));
        h.tvAvgRate.setText(doc.getAvgRate().toString());
        h.tvTotalSold.setText(doc.getTotalSold()+" lượt");
        Glide.with(h.itemView.getContext())
                .load(doc.getDocImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(h.imgDoc);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDoc;
        TextView tvDocName, tvSellPrice, tvAvgRate,tvTotalSold, tvTopBadge;

        ViewHolder(@NonNull View v) {
            super(v);
            tvTopBadge  = v.findViewById(R.id.tvTopBadge);
            imgDoc      = v.findViewById(R.id.imgProduct);
            tvDocName   = v.findViewById(R.id.tvProductName);
            tvSellPrice = v.findViewById(R.id.tvProductPrice);
            tvTotalSold = v.findViewById(R.id.tvNumSold);
            tvAvgRate   = v.findViewById(R.id.tvNumRate);
        }
    }
}
