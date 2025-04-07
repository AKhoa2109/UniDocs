package vn.anhkhoa.projectwebsitebantailieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.anhkhoa.projectwebsitebantailieu.fragment.DescriptionFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.RelevanceFragment;
import vn.anhkhoa.projectwebsitebantailieu.fragment.ReviewFragment;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public class DetailPagerAdapter extends FragmentStateAdapter {
    private DocumentDto document;

    public DetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, DocumentDto document) {
        super(fragmentActivity);
        this.document = document;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DescriptionFragment.newInstance(document);
            case 1:
                return ReviewFragment.newInstance(document);
            case 2:
                return RelevanceFragment.newInstance(document);
            default:
                return DescriptionFragment.newInstance(document);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
