package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;
import java.util.Objects;

public class CartDto implements Serializable {
    private Long cartId;
    private Integer quantity;
    private Long userId;
    private Long docId;
    private String docName;
    private Double sellPrice;
    private String docImageUrl;
    private boolean isSelected;
    private String action;
    private int syncStatus ;

    public CartDto(Long cartId, Integer quantity, Long userId, Long docId, String docName, Double sellPrice, String docImageUrl) {
        this.cartId = cartId;
        this.quantity = quantity;
        this.userId = userId;
        this.docId = docId;
        this.docName = docName;
        this.sellPrice = sellPrice;
        this.docImageUrl = docImageUrl;
    }

    public CartDto(Long cartId, Integer quantity, Long userId, Long docId, String docName, Double sellPrice, String docImageUrl,boolean isSelected,String action,int syncStatus) {
        this.cartId = cartId;
        this.quantity = quantity;
        this.userId = userId;
        this.docId = docId;
        this.docName = docName;
        this.sellPrice = sellPrice;
        this.docImageUrl = docImageUrl;
        this.isSelected = isSelected;
        this.action = action;
        this.syncStatus  = syncStatus;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getDocImageUrl() {
        return docImageUrl;
    }

    public void setDocImageUrl(String docImageUrl) {
        this.docImageUrl = docImageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDto cartDto = (CartDto) o;
        return Objects.equals(cartId, cartDto.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }
}
