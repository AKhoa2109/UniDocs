package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.NotificationChildAdapter;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentNotificationChildBinding;
import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationChildFragment extends Fragment {
    private NotificationChildAdapter adapter;
    private List<NotificationDto> notifications;
    private NotificationDao notificationDao;
    private DatabaseHandler databaseHandler;
    private List<NotificationDto> notificationsLocal;
    private NotificationType type;
    FragmentNotificationChildBinding binding;
    private SessionManager sessionManager;
    private  String ARG_TYPE;

    public NotificationChildFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static NotificationChildFragment newInstance() {
        NotificationChildFragment fragment = new NotificationChildFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentNotificationChildBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            notifications  = (List<NotificationDto>) bundle.getSerializable("notifications");
            type = (NotificationType) bundle.getSerializable("type");

        }
        sessionManager = SessionManager.getInstance(requireContext());
        notificationsLocal = new ArrayList<>();
        databaseHandler = DatabaseHandler.getInstance(requireContext());
        notificationDao = new NotificationDao(requireContext());
        adapter = new NotificationChildAdapter(getContext(),notifications);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvNotificationChild.setLayoutManager(lm);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), lm.getOrientation());
        binding.rvNotificationChild.addItemDecoration(divider);
        binding.rvNotificationChild.setAdapter(adapter);
        handlerMarkAll();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v->{requireActivity().onBackPressed();});
    }

    private void handlerMarkAll(){
        binding.markAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationDao dao = new NotificationDao(getContext());
                for (NotificationDto dto : notifications) {
                    dto.setRead(true);
                    dao.updateIsRead(dto.getNotiId(), true);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkIsRead() {
        notificationsLocal.clear();
        notifications.clear();
        // Lấy danh sách mới nhất từ DB
        notificationsLocal = notificationDao.getNotificationsByType(type,sessionManager.getUser().getUserId());

        // Map từ ID -> NotificationDto để dễ so sánh
        Map<Long, NotificationDto> localMap = new HashMap<>();
        for (NotificationDto dto : notificationsLocal) {
            localMap.put(dto.getNotiId(), dto);
        }

        // Cập nhật trạng thái isRead nếu có khác biệt
        for (int i = 0; i < notifications.size(); i++) {
            NotificationDto dto = notifications.get(i);
            NotificationDto localDto = localMap.get(dto.getNotiId());

            if (localDto != null && dto.isRead() != localDto.isRead()) {
                dto.setRead(localDto.isRead());
            }
        }

        // Cập nhật lại danh sách nếu có chênh lệch
        Set<Long> currentIds = notifications.stream().map(NotificationDto::getNotiId).collect(Collectors.toSet());
        Set<Long> localIds = localMap.keySet();

        // Thêm phần tử mới từ DB vào notifications
        for (Long id : localIds) {
            if (!currentIds.contains(id)) {
                notifications.add(localMap.get(id));
            }
        }

        // Xoá phần tử không còn trong DB
        notifications.removeIf(dto -> !localIds.contains(dto.getNotiId()));

        // Cập nhật lại UI
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIsRead();
    }

}