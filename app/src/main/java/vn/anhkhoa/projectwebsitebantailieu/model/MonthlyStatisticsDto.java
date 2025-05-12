package vn.anhkhoa.projectwebsitebantailieu.model;

import java.util.Map;

public class MonthlyStatisticsDto {
    private String monthYear;
    private Float totalRevenue;
    private Long totalOrders;
    private Long totalProductsSold;
    private Map<String, Float> revenueByCategory;


    public MonthlyStatisticsDto(String monthYear, Float totalRevenue, Long totalOrders, Long totalProductsSold, Map<String, Float> revenueByCategory) {
        this.monthYear = monthYear;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalProductsSold = totalProductsSold;
        this.revenueByCategory = revenueByCategory;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public Float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalProductsSold() {
        return totalProductsSold;
    }

    public void setTotalProductsSold(Long totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }

    public Map<String, Float> getRevenueByCategory() {
        return revenueByCategory;
    }

    public void setRevenueByCategory(Map<String, Float> revenueByCategory) {
        this.revenueByCategory = revenueByCategory;
    }
}
