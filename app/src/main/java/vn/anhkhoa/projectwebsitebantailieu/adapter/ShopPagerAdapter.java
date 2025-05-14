package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopCategoryFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopDashboardFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopDetailFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopDocumentFragment;

public class ShopPagerAdapter extends FragmentStateAdapter {
    public ShopPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ShopDetailFragment();
            case 1:
                return new ShopDocumentFragment();
            case 2:
                return new ShopCategoryFragment();
            case 3:
                return new ShopDashboardFragment();
            default:
                return new ShopDetailFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
