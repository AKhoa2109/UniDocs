package vn.anhkhoa.projectwebsitebantailieu.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentShopDashboardBinding;
import vn.anhkhoa.projectwebsitebantailieu.model.MonthlyStatisticsDto;
import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopDashboardFragment extends Fragment {
    FragmentShopDashboardBinding binding;
    private List<MonthlyStatisticsDto> monthlyStatisticsDtos;
    private SessionManager sessionManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShopDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopDashboardFragment newInstance(String param1, String param2) {
        ShopDashboardFragment fragment = new ShopDashboardFragment();
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
        binding =  FragmentShopDashboardBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        getApiStatisticData(sessionManager.getUser().getUserId());
        return binding.getRoot();
    }

    private void setupCharts() {
        setupLineChart();
        setupPieChart();
        setupBarCharts();
    }

    private void getApiStatisticData(Long userId){
        ApiService.apiService.getMonthlyStats(userId).enqueue(new Callback<ResponseData<List<MonthlyStatisticsDto>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<MonthlyStatisticsDto>>> call, Response<ResponseData<List<MonthlyStatisticsDto>>> response) {
                if(response.isSuccessful() && response.body() != null){
                    monthlyStatisticsDtos = response.body().getData();
                    setupCharts();
                    updateCharts(monthlyStatisticsDtos);
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<MonthlyStatisticsDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCharts(List<MonthlyStatisticsDto> data) {
        getActivity().runOnUiThread(() -> {
            // Line Chart - Total Revenue
            List<Entry> lineEntries = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                lineEntries.add(new Entry(i, (float) data.get(i).getTotalRevenue()));
            }

            LineDataSet lineDataSet = new LineDataSet(lineEntries, "Doanh thu theo tháng");
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setValueTextColor(Color.BLACK);

            LineData lineData = new LineData(lineDataSet);
            binding.lineChartRevenue.setData(lineData);
            binding.lineChartRevenue.invalidate();

            // Pie Chart - Revenue by Category
            List<PieEntry> pieEntries = new ArrayList<>();
            for (MonthlyStatisticsDto item : data) {
                for (Map.Entry<String, Float> category : item.getRevenueByCategory().entrySet()) {
                    pieEntries.add(new PieEntry(category.getValue(), category.getKey()));
                }
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Doanh thu theo danh mục");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData pieData = new PieData(pieDataSet);
            binding.pieChartRevenueByCategory.setData(pieData);
            binding.pieChartRevenueByCategory.invalidate();

            // Bar Chart - Total Orders
            List<BarEntry> barEntriesOrders = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                barEntriesOrders.add(new BarEntry(i, data.get(i).getTotalOrders()));
            }

            BarDataSet barDataSetOrders = new BarDataSet(barEntriesOrders, "Tổng đơn hàng");
            barDataSetOrders.setColor(Color.RED);
            BarData barDataOrders = new BarData(barDataSetOrders);
            binding.barChartTotalOrders.setData(barDataOrders);
            binding.barChartTotalOrders.invalidate();

            // Bar Chart - Total Products Sold
            List<BarEntry> barEntriesProducts = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                barEntriesProducts.add(new BarEntry(i, data.get(i).getTotalProductsSold()));
            }

            BarDataSet barDataSetProducts = new BarDataSet(barEntriesProducts, "Sản phẩm đã bán");
            barDataSetProducts.setColor(Color.GREEN);
            BarData barDataProducts = new BarData(barDataSetProducts);
            binding.barChartTotalProductsSold.setData(barDataProducts);
            binding.barChartTotalProductsSold.invalidate();
        });
    }
    private void setupLineChart() {
        binding.lineChartRevenue.getDescription().setEnabled(false);
        binding.lineChartRevenue.setTouchEnabled(true);
        binding.lineChartRevenue.setDragEnabled(true);
        binding.lineChartRevenue.setScaleEnabled(true);

        XAxis xAxis = binding.lineChartRevenue.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths(monthlyStatisticsDtos)));
    }

    private void setupPieChart() {
        binding.pieChartRevenueByCategory.getDescription().setEnabled(false);
        binding.pieChartRevenueByCategory.setUsePercentValues(true);
        binding.pieChartRevenueByCategory.setEntryLabelColor(Color.BLACK);
        binding.pieChartRevenueByCategory.setEntryLabelTextSize(12f);
        binding.pieChartRevenueByCategory.setHoleRadius(30f);
    }

    private void setupBarCharts() {
        setupBarChart(binding.barChartTotalOrders);
        setupBarChart(binding.barChartTotalProductsSold);
    }

    private void setupBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths(monthlyStatisticsDtos)));

        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
    }

    private List<String> getMonths(List<MonthlyStatisticsDto> data) {
        List<String> months = new ArrayList<>();
        for (MonthlyStatisticsDto item : data) {
            months.add(item.getMonthYear());
        }
        return months;
    }
}