package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentAccountBinding;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentDiscountBinding;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscountFragment.
     */

    private FragmentDiscountBinding binding;
    private SessionManager session;
    // TODO: Rename and change types and number of parameters
    public static DiscountFragment newInstance(String param1, String param2) {
        DiscountFragment fragment = new DiscountFragment();
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
        binding = FragmentDiscountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        session = SessionManager.getInstance(requireActivity());
        initView();

    }

    private void initView() {
        setupDateTimePicker(
                binding.buttonStartDate,
                dateTime -> binding.buttonStartDate.setText(formatDateTime(dateTime))
        );
        setupDateTimePicker(
                binding.buttonEndDate,
                dateTime -> binding.textViewEndDate.setText(formatDateTime(dateTime))
        );
    }

    private void setupDateTimePicker(MaterialButton button, Consumer<LocalDateTime> onSelected) {
        button.setOnClickListener(v -> {
            // Bước 1: chọn ngày
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Chọn ngày")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                LocalDate date = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                // Bước 2: chọn giờ
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTitleText("Chọn giờ")
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .build();

                timePicker.addOnPositiveButtonClickListener(n -> {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();

                    LocalTime time = LocalTime.of(hour, minute);
                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    onSelected.accept(dateTime);
                });

            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });
    }

    /** Format LocalDateTime thành chuỗi hiển thị */
    private String formatDateTime(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}