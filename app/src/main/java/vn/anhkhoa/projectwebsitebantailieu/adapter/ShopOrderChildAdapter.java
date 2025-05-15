package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OrderDetailDtoRequest;

public class ShopOrderChildAdapter extends RecyclerView.Adapter<ShopOrderChildAdapter.OrderViewHolder>{
    private List<OrderDtoRequest> orderList;
    private static OnStatusChangeListener statusChangeListener;

    public ShopOrderChildAdapter(List<OrderDtoRequest> orderList) {
        this.orderList = orderList;
    }

    public interface OnStatusChangeListener {
        void onStatusChanged(int position, OrderStatus newStatus);
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_shop, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDtoRequest order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderStatus, tvDocumentName, tvDocumentQuantity, tvDocumentOriginalPrice, tvDocumentSellPrice;
        AutoCompleteTextView actvStatus;
        Button btnSave;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            tvDocumentQuantity = itemView.findViewById(R.id.tvDocumentQuantity);
            tvDocumentOriginalPrice = itemView.findViewById(R.id.tvDocumentOriginalPrice);
            tvDocumentSellPrice = itemView.findViewById(R.id.tvDocumentSellPrice);
            actvStatus = itemView.findViewById(R.id.actvGender);
            btnSave = itemView.findViewById(R.id.btnSubmit);

            setupStatusDropdown();
        }

        private void setupStatusDropdown() {
            String[] orderStatus = {"PENDING", "CONFIRMED", "DELIVERED", "CANCELED"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    orderStatus
            );
            actvStatus.setAdapter(adapter);
        }

        public void bind(OrderDtoRequest order) {
            tvOrderStatus.setText(order.getStatus().name());
            tvDocumentName.setText(order.getDocName());
            tvDocumentQuantity.setText("Số lượng: " + order.getQuantity());
            tvDocumentOriginalPrice.setText(String.format("%,.0f đồng", order.getOriginalPrice()));
            tvDocumentSellPrice.setText(String.format("Giá: %,.0f đồng", order.getSellPrice()));

            // Set current status
            actvStatus.setText(order.getStatus().name(), false);

            btnSave.setOnClickListener(v -> {
                String selected = actvStatus.getText().toString();
                OrderStatus selectedStatus = OrderStatus.valueOf(selected);
                order.setStatus(selectedStatus);

                if (statusChangeListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        statusChangeListener.onStatusChanged(position, selectedStatus);
                    }
                }
            });
        }
    }
}
