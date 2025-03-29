package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentSearchDocumentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchDocumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDocumentFragment extends Fragment {

    FragmentSearchDocumentBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchDocumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDocumentFragment newInstance(String param1, String param2) {
        SearchDocumentFragment fragment = new SearchDocumentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentSearchDocumentBinding.inflate(inflater, container, false);
        handlerFilter();
        return binding.getRoot();
    }

    private void showFilterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(view);

        ChipGroup cgCategory = view.findViewById(R.id.cgCategory);
        ChipGroup cgRating = view.findViewById(R.id.cgRating);
        // Thêm các nút Apply và Cancel
        builder.setPositiveButton("Áp dụng", (dialog, which) -> {
            List<Integer> selectedCats = cgCategory.getCheckedChipIds();

        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void handlerFilter(){
        binding.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
    }
}