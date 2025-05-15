package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.fragment.DescriptionFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.OrderStatusChildFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopOrderChildCancelledFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopOrderChildConfirmedFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopOrderChildDeliveredFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopOrderChildFragment;

public class ShopOrderAdapter extends FragmentStateAdapter {
    private static final OrderStatus[] ORDER_STATUSES = {
            OrderStatus.PENDING,
            OrderStatus.DELIVERED,
            OrderStatus.CONFIRMED,
            OrderStatus.CANCELED
    };

    public ShopOrderAdapter(@NonNull Fragment fragment)
    {
        super(fragment);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ShopOrderChildFragment.newInstance(ORDER_STATUSES[0]);
            case 1:
                return ShopOrderChildDeliveredFragment.newInstance(ORDER_STATUSES[1]);
            case 2:
                return ShopOrderChildConfirmedFragment.newInstance(ORDER_STATUSES[2]);
            case 3:
                return ShopOrderChildCancelledFragment.newInstance(ORDER_STATUSES[3]);
            default:
                return ShopOrderChildFragment.newInstance(ORDER_STATUSES[4]);
        }
    }

    @Override
    public int getItemCount() {
        return ORDER_STATUSES.length;
    }
}
