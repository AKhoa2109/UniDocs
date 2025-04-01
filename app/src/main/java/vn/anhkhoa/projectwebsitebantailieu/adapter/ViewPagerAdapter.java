package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.Map;

import vn.anhkhoa.projectwebsitebantailieu.fragment.FilterDocumentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private String keyword;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String keyword) {
        super(fragmentManager, lifecycle);
        this.keyword = keyword;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String sortType;
        switch (position) {
            case 0: sortType = "relevance"; break;
            case 1: sortType = "newest"; break;
            case 2: sortType = "most_purchased"; break;
            case 3: sortType = "most_downloaded"; break;
            case 4: sortType = "most_viewed"; break;
            case 5: sortType = "price_asc"; break;
            case 6: sortType = "price_desc"; break;
            default: sortType = "relevance";
        }
        return FilterDocumentFragment.newInstance(sortType, keyword);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

}
