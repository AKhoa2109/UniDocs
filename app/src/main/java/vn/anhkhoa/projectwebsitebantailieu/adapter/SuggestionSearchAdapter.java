package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class SuggestionSearchAdapter extends RecyclerView.Adapter<SuggestionSearchAdapter.ViewHolder> {
    private List<DocumentDto> documents;

    public SuggestionSearchAdapter(List<DocumentDto> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentDto documentDto = documents.get(position);
        if (documentDto == null)
            return;

        holder.txtNameDoc.setText(documentDto.getDoc_name());
    }

    @Override
    public int getItemCount() {
        if (documents != null)
            return documents.size();
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNameDoc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameDoc = itemView.findViewById(R.id.tvNameDoc);
        }
    }
}
