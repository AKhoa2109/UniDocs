package vn.anhkhoa.projectwebsitebantailieu.model.request;

import java.time.LocalDateTime;

import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;

public class OrderDetailDtoRequest {
    private Long orderId;
    private Long docId;
    private String docName;
    private Long userId;
    private String name;
    private String phone;
    private String address;
    private LocalDateTime orderAt;
    private OrderStatus orderStatus;

    public OrderDetailDtoRequest(Long orderId, Long docId, String docName, Long userId, String name, String phone, String address, LocalDateTime orderAt, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.docId = docId;
        this.docName = docName;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.orderAt = orderAt;
        this.orderStatus = orderStatus;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getOrderAt() {
        return orderAt;
    }

    public void setOrderAt(LocalDateTime orderAt) {
        this.orderAt = orderAt;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
