package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.fragment.OrderStatusChildFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopOrderChildFragment;

public class ShopOrderAdapter extends FragmentStateAdapter {
    private final String[] statusValues;

    public ShopOrderAdapter(
            @NonNull Fragment fragment,
            String[] statusValues
    ) {
        super(fragment);
        this.statusValues = statusValues;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Tạo fragment con với status tương ứng
        return ShopOrderChildFragment.newInstance(OrderStatus.valueOf(statusValues[position]));
    }

    @Override
    public int getItemCount() {
        return statusValues.length;
    }
}
