package vn.anhkhoa.projectwebsitebantailieu.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import java.util.List;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.enums.Scope;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.CurrentFormatter;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>{
    private List<DiscountDto> discounts;
    private Listener listener;
    private int selectedPosition = -1;

    public interface Listener {
        void onVoucherSelected(DiscountDto voucher);
    }

    public DiscountAdapter(List<DiscountDto> discounts, Listener listener) {
        this.discounts = discounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        DiscountDto v = discounts.get(position);
        Scope scope = v.getScope();
        switch (scope) {
            case SHOP:
                holder.tvScopeName.setText("SHOP");
                break;
            case DOCUMENT:
                holder.tvScopeName.setText("DOCUMENT");
                break;
            case CATEGORY:
                holder.tvScopeName.setText("CATEGORY");
                break;
        }
        holder.tvName.setText(v.getDiscountName());
        holder.tvConditions.setText(
                v.getMinPrice() != null && v.getMinPrice() > 0
                        ? "Áp dụng cho đơn tối thiểu " + CurrentFormatter.format(v.getMinPrice())
                        : "Không yêu cầu giá tối thiểu"
        );
        holder.tvExpiry.setText("Hạn dùng: " + v.getEndAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Integer remainingQuantity = v.getUsageLimit() - v.getUsedCount();
        holder.tvRemainingQuantity.setText("x" +remainingQuantity);

        holder.rbSelect.setChecked(position == selectedPosition);

        holder.rbSelect.setOnClickListener(view -> {
            int prev = selectedPosition;
            int current = holder.getAdapterPosition();
            if (current == RecyclerView.NO_POSITION) return;
            if (current == prev) return;

            selectedPosition = current;

            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);

            listener.onVoucherSelected(discounts.get(current));
        });

        holder.itemView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    static class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvConditions, tvExpiry, tvRemainingQuantity, tvScopeName;
        RadioButton rbSelect;
        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScopeName = itemView.findViewById(R.id.tvScopeName);
            tvName = itemView.findViewById(R.id.tvVoucherName);
            tvConditions = itemView.findViewById(R.id.tvCondition);
            tvExpiry = itemView.findViewById(R.id.tvExpiryVoucher);
            rbSelect = itemView.findViewById(R.id.cbSelect);
            tvRemainingQuantity = itemView.findViewById(R.id.tvRemainingQuantity);
        }
    }
}

