package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.databinding.ItemOrderHistoryBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {
    private List<OrderDtoRequest> orderList = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onDetailClick(OrderDtoRequest order);
        void onBuyAgainClick(OrderDtoRequest order);

        void onCancelClick(OrderDtoRequest order);
    }

    public OrderHistoryAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        OrderDtoRequest order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<OrderDtoRequest> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderHistoryViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderDtoRequest order) {
            binding.tvOrderStatus.setText(setStatus(order.getStatus()));
            binding.tvDocumentName.setText(order.getDocName());
            binding.tvDocumentQuantity.setText("Số lượng: " + order.getQuantity());
            binding.tvDocumentOriginalPrice.setText(
                    CurrentFormatter.format(order.getOriginalPrice())
            );
            binding.tvDocumentSellPrice.setText(
                    CurrentFormatter.format(order.getOriginalPrice())
            );
            
            // Load image using Glide
            if (order.getDocImageUrl() != null && !order.getDocImageUrl().isEmpty()) {
                Glide.with(binding.ivDocument)
                    .load(order.getDocImageUrl())
                    .into(binding.ivDocument);
            }

            binding.btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDetailClick(order);
                }

            });

            binding.btnBuyAgain.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBuyAgainClick(order);
                }
            });

            binding.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick(order);
                }
            });

            if(order.getStatus() == OrderStatus.CANCELED){
                binding.btnDetail.setVisibility(View.VISIBLE);
                binding.btnBuyAgain.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.GONE);
            }
            else if(order.getStatus() == OrderStatus.CONFIRMED){
                binding.btnDetail.setVisibility(View.VISIBLE);
                binding.btnBuyAgain.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.GONE);
            }
            else{
                binding.btnDetail.setVisibility(View.VISIBLE);
                binding.btnBuyAgain.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }

        }

        private String setStatus(OrderStatus status){
            if(status == OrderStatus.CANCELED)
                return "Đã hủy";
            else if(status == OrderStatus.CONFIRMED)
                return "Hoàn thành";
            else if(status == OrderStatus.DELIVERED)
                return "Đang giao";
            else
                return "Chưa xác nhận";
        }
    }
} 