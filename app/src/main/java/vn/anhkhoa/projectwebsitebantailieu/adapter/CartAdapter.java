package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.location.GnssAntennaInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private List<CartDto> carts;
    private Listener listener;
    private boolean isSelectAll = false;
    public interface Listener {
        void onCheckboxClick(int position, boolean isChecked);
        void onQuantityChanged(int position, int quantity);
        void onRemoveItem(int position);
    }

    public CartAdapter(List<CartDto> carts, Listener listener) {
        this.carts = carts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDto item = carts.get(position);

        holder.cbSelect.setChecked(item.isSelected());
        holder.tvProductName.setText(item.getDocName());
        holder.tvProductPrice.setText(String.format("%sđ", item.getSellPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(holder.itemView.getContext()).load(item.getDocImageUrl()).into(holder.ivProduct);

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onCheckboxClick(position, isChecked);
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            if(item.isSelected())
                listener.onQuantityChanged(position, newQuantity);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if(item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                if(item.isSelected())
                    listener.onQuantityChanged(position, newQuantity);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onRemoveItem(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount(){
        if(carts == null)
            return 0;
        return carts.size();
    }

    public void selectAllItems(boolean select) {
        isSelectAll = select;
        for(CartDto item : carts) {
            item.setSelected(select);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox cbSelect;
        private TextView tvProductName, tvProductPrice, tvQuantity;
        private ImageButton btnIncrease, btnDecrease, btnRemove;

        private ImageView ivProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivProduct = itemView.findViewById(R.id.ivProduct);
        }
    }
}
