package vn.anhkhoa.projectwebsitebantailieu.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.adapter.NotificationAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentNotificationBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationGroup;
import vn.anhkhoa.projectwebsitebantailieu.utils.NetworkUtil;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;
import vn.anhkhoa.projectwebsitebantailieu.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private List<NotificationGroup> notificationGroups;
    private NotificationDao notificationDao;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        binding =  FragmentNotificationBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        initView();
        databaseHandler = DatabaseHandler.getInstance(requireContext());
        notificationDao = new NotificationDao(requireContext());
        if(NetworkUtil.isNetworkAvailable(requireContext())){
            getApiNotificationGroup(sessionManager.getUser().getUserId());
        }
        else {
            loadFromLocal();
            ToastUtils.show(getContext(), "Không có kết nối mạng. Đang hiển thị thông báo từ thiết bị.", "#FFA500");
        }
        return  binding.getRoot();
    }

    private void initView(){
        notificationGroups = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(),notificationGroups);
        binding.rvNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvNotification.setAdapter(notificationAdapter);

        binding.btnBack.setOnClickListener(v->{requireActivity().onBackPressed();});
    }

    private void getApiNotificationGroup(Long userId){
        ApiService.apiService.getNotificationsGrouped(userId).enqueue(new Callback<ResponseData<List<NotificationGroup>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<NotificationGroup>>> call, Response<ResponseData<List<NotificationGroup>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<NotificationGroup> groupsFromApi = response.body().getData();
                    notificationGroups.clear();
                    notificationGroups.addAll(groupsFromApi);
                    notificationAdapter.notifyDataSetChanged();

                    saveToLocal(groupsFromApi);
                    syncLocalToServer(); // Gửi dữ liệu local lên server nếu có
                }
                else{
                    ToastUtils.show(getContext(),"Lỗi lấy thông báo","#FF0000");
                    loadFromLocal();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<NotificationGroup>>> call, Throwable t) {
                ToastUtils.show(getContext(),"Lỗi kết nối","#FF0000");
                loadFromLocal();
            }
        });
    }

    private void saveToLocal(List<NotificationGroup> groups) {
        for (NotificationGroup group : groups) {
            for (NotificationDto notification : group.getItems()) {
                if (!notificationDao.existsNotification(notification)) {
                    notificationDao.addNotification(notification);
                }
            }
        }
    }

    private void loadFromLocal() {
        List<NotificationDto> localNotis = notificationDao.getNotifications(sessionManager.getUser().getUserId());
        notificationGroups.clear();
        for (NotificationDto dto : localNotis) {
            boolean found = false;
            for (NotificationGroup group : notificationGroups) {
                if (group.getType() == dto.getType()) {
                    group.getItems().add(dto);
                    found = true;
                    break;
                }
            }
            if (!found) {
                List<NotificationDto> newList = new ArrayList<>();
                newList.add(dto);
                notificationGroups.add(new NotificationGroup(dto.getType(), newList));
            }
        }
        notificationAdapter.notifyDataSetChanged();
    }

    private void syncLocalToServer() {
        List<NotificationDto> localNotis = notificationDao.getNotifications(sessionManager.getUser().getUserId());
        if (localNotis.isEmpty()) return;
        getApiSyncNotification(localNotis);
    }

    private void getApiSyncNotification(List<NotificationDto> notification){
        ApiService.apiService.syncNotification(notification).enqueue(new Callback<ResponseData<List<NotificationDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<NotificationDto>>> call, Response<ResponseData<List<NotificationDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationDto> syncedNotis = response.body().getData();
                    if (!syncedNotis.isEmpty()) {
                        ToastUtils.show(getContext(), "Đã đồng bộ " + syncedNotis.size() + " thông báo", "#00FF00");
                    }
                } else {
                    ToastUtils.show(getContext(), "Lỗi khi đồng bộ thông báo", "#FF0000");
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<NotificationDto>>> call, Throwable t) {
                ToastUtils.show(getContext(), "Không thể kết nối server để đồng bộ", "#FF0000");
            }
        });
    }

}