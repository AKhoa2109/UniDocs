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

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.activity.MainActivity;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class SuggestionDocumentAdapter extends RecyclerView.Adapter<SuggestionDocumentAdapter.ViewHolder> {
    private List<DocumentDto> documents;

    public SuggestionDocumentAdapter(List<DocumentDto> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_document, parent, false);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        private TextView tvDocName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDocument = itemView.findViewById(R.id.img_document);
            tvDocName = itemView.findViewById(R.id.tv_doc_name);
        }
    }
}
