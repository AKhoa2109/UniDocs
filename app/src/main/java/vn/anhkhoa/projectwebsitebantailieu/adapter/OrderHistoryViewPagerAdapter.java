package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.fragment.OrderStatusChildFragment;

public class OrderHistoryViewPagerAdapter extends FragmentStateAdapter {
    private final String[] statusValues;

    public OrderHistoryViewPagerAdapter(
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
        return OrderStatusChildFragment.newInstance(OrderStatus.valueOf(statusValues[position]));
    }

    @Override
    public int getItemCount() {
        return statusValues.length;
    }
}
