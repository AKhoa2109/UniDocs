package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;

public class OrderDtoRequest implements Serializable {
    private Long orderId;
    private Long docId;
    private String docName;
    private Long originalPrice;
    private Long sellPrice;
    private String docImageUrl;
    private String docDesc;
    private Integer quantity;

    private OrderStatus status;

    public OrderDtoRequest(Long orderId, Long docId, String docName, Long originalPrice, Long sellPrice, String docImageUrl, String docDesc, Integer quantity, OrderStatus status) {
        this.orderId = orderId;
        this.docId = docId;
        this.docName = docName;
        this.originalPrice = originalPrice;
        this.sellPrice = sellPrice;
        this.docImageUrl = docImageUrl;
        this.docDesc = docDesc;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Long sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getDocImageUrl() {
        return docImageUrl;
    }

    public void setDocImageUrl(String docImageUrl) {
        this.docImageUrl = docImageUrl;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}