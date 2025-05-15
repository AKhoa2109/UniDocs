package vn.anhkhoa.projectwebsitebantailieu.model.request;

import java.util.List;

public class CreateOrderFromCartRequest {
    private List<Long> cartIds;  // Danh sách ID của các item trong giỏ hàng
    private Long discountId;     // ID của discount được áp dụng (có thể null)
    private Long userId;         // ID của user

    public CreateOrderFromCartRequest(List<Long> cartIds, Long discountId, Long userId) {
        this.cartIds = cartIds;
        this.discountId = discountId;
        this.userId = userId;
    }

    public List<Long> getCartIds() {
        return cartIds;
    }

    public void setCartIds(List<Long> cartIds) {
        this.cartIds = cartIds;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}