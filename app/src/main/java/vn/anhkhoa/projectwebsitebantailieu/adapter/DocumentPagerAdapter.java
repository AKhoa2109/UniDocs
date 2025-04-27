package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.fragment.ShopDocumentChildFragment;

public class DocumentPagerAdapter extends FragmentStateAdapter {
    public DocumentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String sortType;
        switch (position) {
            case 0: sortType = "newest"; break;
            case 1: sortType = "most_sold"; break;
            case 2: sortType = "price_asc"; break;
            case 3: sortType = "price_desc"; break;
            default: sortType = "newest";
        }
        return ShopDocumentChildFragment.newInstance(sortType);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
