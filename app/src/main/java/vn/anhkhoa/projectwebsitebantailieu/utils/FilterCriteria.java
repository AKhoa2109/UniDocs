package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.util.List;

public class FilterCriteria {
    public List<Long> categoryId;
    public List<Integer> rating;
    public Double minPrice;
    public Double maxPrice;

    public FilterCriteria(List<Long> categoryId, List<Integer> rating, Double minPrice, Double maxPrice) {
        this.categoryId = categoryId;
        this.rating = rating;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public List<Long> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<Long> categoryId) {
        this.categoryId = categoryId;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public void setRating(List<Integer> rating) {
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
