package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OrderDetailDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.utils.OrderViewModel;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

public class ShopOrderChildAdapter extends RecyclerView.Adapter<ShopOrderChildAdapter.OrderViewHolder> {
    private List<OrderDtoRequest> orderList;
    private Context context;

    public ShopOrderChildAdapter(Context context, List<OrderDtoRequest> orderList) {
        this.context = context;
        this.orderList = orderList;
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
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
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
            String[] orderStatus = {"PENDING", "DELIVERED", "CONFIRMED", "CANCELED"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    orderStatus
            );
            actvStatus.setAdapter(adapter);
        }

        public void bind(OrderDtoRequest order, int position) {
            tvOrderStatus.setText(order.getStatus().name());
            tvDocumentName.setText(order.getDocName());
            tvDocumentQuantity.setText("Số lượng: " + order.getQuantity());
            tvDocumentOriginalPrice.setText(String.format("%,.0f đồng", order.getOriginalPrice()));
            tvDocumentSellPrice.setText(String.format("Giá: %,.0f đồng", order.getSellPrice()));

            actvStatus.setText(order.getStatus().name(), false);

            btnSave.setOnClickListener(v -> {
                String selected = actvStatus.getText().toString();
                try {
                    OrderStatus newStatus = OrderStatus.valueOf(selected);
                    OrderStatus oldStatus = order.getStatus();
                    if (newStatus == oldStatus) return;

                    // Optimistic update
                    order.setStatus(newStatus);
                    notifyItemChanged(position);

                    ApiService.apiService.updateOrderStatus(order.getOrderId(), newStatus).enqueue(new Callback<ResponseData<Void>>() {
                        @Override
                        public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                            if (response.isSuccessful()) {
                                ToastUtils.show(context, "Cập nhật thành công");
                                OrderViewModel vm = new ViewModelProvider((FragmentActivity) context)
                                        .get(OrderViewModel.class);
                                vm.triggerReload();
                            } else {
                                order.setStatus(oldStatus);
                                notifyItemChanged(position);
                                ToastUtils.show(context, "Cập nhật thất bại");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                            order.setStatus(oldStatus);
                            notifyItemChanged(position);
                            ToastUtils.show(context, "Lỗi kết nối");
                        }
                    });
                } catch (IllegalArgumentException e) {
                    ToastUtils.show(itemView.getContext(), "Trạng thái không hợp lệ");
                }
            });
        }
    }
}

