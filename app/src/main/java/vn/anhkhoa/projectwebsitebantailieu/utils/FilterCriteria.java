package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.util.List;

public class FilterCriteria {
    public Long[] categoryId;
    public Integer[] rating;
    public Double minPrice;
    public Double maxPrice;

    public FilterCriteria(Long[] categoryId, Integer[] rating, Double minPrice, Double maxPrice) {
        this.categoryId = categoryId;
        this.rating = rating;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public Long[] getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long[] categoryId) {
        this.categoryId = categoryId;
    }

    public Integer[] getRating() {
        return rating;
    }

    public void setRating(Integer[] rating) {
        this.rating = rating;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
}
