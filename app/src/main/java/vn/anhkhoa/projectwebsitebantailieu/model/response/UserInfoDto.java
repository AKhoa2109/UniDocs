package vn.anhkhoa.projectwebsitebantailieu.model.response;

public class UserInfoDto {
    private Long userId;
    private String name;
    private String address;
    private String avatar;
    private Long totalProduct;
    private Long totalProductSale;
    private Long totalReview;

    public UserInfoDto(Long userId, String name, String address, String avatar, Long totalProduct, Long totalProductSale, Long totalReview) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.avatar = avatar;
        this.totalProduct = totalProduct;
        this.totalProductSale = totalProductSale;
        this.totalReview = totalReview;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(Long totalProduct) {
        this.totalProduct = totalProduct;
    }

    public Long getTotalProductSale() {
        return totalProductSale;
    }

    public void setTotalProductSale(Long totalProductSale) {
        this.totalProductSale = totalProductSale;
    }

    public Long getTotalReview() {
        return totalReview;
    }

    public void setTotalReview(Long totalReview) {
        this.totalReview = totalReview;
    }
}
