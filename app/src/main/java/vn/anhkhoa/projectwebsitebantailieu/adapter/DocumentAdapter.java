package vn.anhkhoa.projectwebsitebantailieu.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.activity.SearchActivity;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<DocumentDto> documents;

    private Context context;

    public DocumentAdapter(Context context, List<DocumentDto> documents) {
        this.context = context;
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
                .load(documentDto.getDocImageUrl())
                        .into(holder.imgDocument);
        holder.tvDocName.setText(documentDto.getDocName());
        holder.tvSellPrice.setText(CurrentFormatter.format(documentDto.getSellPrice()));
        String totalSold = String.valueOf(documentDto.getTotalSold())+" lượt xem";
        holder.tvTotalSold.setText(totalSold);
        // Tính % giảm giá: (Giá gốc - Giá bán) / Giá gốc * 100
        Double originalObj = documentDto.getOriginalPrice();
        Double sellObj     = documentDto.getSellPrice();
        double original    = originalObj != null ? originalObj : 0.0;
        double sell        = sellObj     != null ? sellObj     : 0.0;

        // Tính % giảm giá chỉ khi có giá gốc > 0
        double discountPercent = 0;
        if (original > 0) {
            discountPercent = (original - sell) / original * 100;
        }
        // Hiển thị "-45%" hoặc "-0%" nếu không có dữ liệu
        holder.tvDiscount.setText(String.format(Locale.getDefault(), "-%.0f%%", discountPercent));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).openProductDetailFragment(documentDto);
                }
                else {
                    Context ctx = holder.itemView.getContext();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.putExtra("documentDto", documentDto);
                    // Đảm bảo reuse MainActivity đang tồn tại
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ctx.startActivity(intent);

                    // Nếu context đang là Activity thì finish nó
                    if (ctx instanceof Activity) {
                        ((Activity) ctx).finish();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (documents != null)
            return documents.size();
        return 0;
    }
    public void updateList(List<DocumentDto> newList) {
        documents.clear();
        documents.addAll(newList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgDocument;
        private TextView tvDocName, tvSellPrice, tvTotalSold, tvDiscount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDocument = itemView.findViewById(R.id.img_document);
            tvDocName = itemView.findViewById(R.id.tv_doc_name);
            tvSellPrice = itemView.findViewById(R.id.tv_sell_price);
            tvTotalSold = itemView.findViewById(R.id.tvNumSold);
            tvDiscount = itemView.findViewById(R.id.tv_percent_sale);
        }
    }
}
