package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import vn.anhkhoa.projectwebsitebantailieu.enums.DiscountStatus;
import vn.anhkhoa.projectwebsitebantailieu.enums.DiscountType;
import vn.anhkhoa.projectwebsitebantailieu.enums.Scope;

public class DiscountDto implements Serializable {
    private Long discountId;
    private String discountName;
    private DiscountType discountType;
    private DiscountStatus status;
    private Scope scope;
    private Integer usageLimit;
    private Integer usedCount;
    private Double discountValue;
    private LocalDateTime startDate;
    private Double maxPrice;
    private Double minPrice;
    private LocalDateTime endAt;
    private Long scopeId;

    public DiscountDto(){

    }

    public DiscountDto(Long discountId, String discountName, DiscountType discountType, DiscountStatus status, Scope scope, Integer usageLimit, Integer usedCount, Double discountValue, LocalDateTime startDate, Double maxPrice, Double minPrice, LocalDateTime endAt, Long scopeId) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountType = discountType;
        this.status = status;
        this.scope = scope;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.endAt = endAt;
        this.scopeId = scopeId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }


    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public DiscountStatus getStatus() {
        return status;
    }

    public void setStatus(DiscountStatus status) {
        this.status = status;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
