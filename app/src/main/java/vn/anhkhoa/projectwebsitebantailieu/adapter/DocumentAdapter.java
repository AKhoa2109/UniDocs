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
import java.util.List;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).openProductDetailFragment(documentDto);
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgDocument;
        private TextView tvDocName, tvSellPrice, tvTotalSold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDocument = itemView.findViewById(R.id.img_document);
            tvDocName = itemView.findViewById(R.id.tv_doc_name);
            tvSellPrice = itemView.findViewById(R.id.tv_sell_price);
            tvTotalSold = itemView.findViewById(R.id.tvNumSold);
        }
    }
}
