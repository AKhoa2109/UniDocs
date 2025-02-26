package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.adapter.DocumentAdapter;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    RecyclerView rcvDocument;

    List<DocumentDto> documentDtos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rcvDocument = view.findViewById(R.id.rcvDocument);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvDocument.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvDocument.addItemDecoration(itemDecoration);

        documentDtos = new ArrayList<>();
        callApiGetListDocument();

        return view;
    }

    private void callApiGetListDocument(){
        ApiService.apiService.getListDocument().enqueue(new Callback<ResponseData<List<DocumentDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<DocumentDto>>> call, Response<ResponseData<List<DocumentDto>>> response) {
                ResponseData<List<DocumentDto>> data = response.body();

                if(data == null)
                    return;
                documentDtos = data.getData();
                DocumentAdapter documentAdapter = new DocumentAdapter(documentDtos);
                rcvDocument.setAdapter(documentAdapter);
            }

            @Override
            public void onFailure(Call<ResponseData<List<DocumentDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "bi loi roi", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}