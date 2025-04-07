package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewDto implements Serializable {
    private Long reviewId;
    private Integer rate;
    private LocalDateTime createdAt;
    private String content;
    private Long userId;
    private String name;

    private String avatar;

    private List<ReviewCriterialDto> criterialDtos;

    public ReviewDto(Long reviewId, Integer rate, LocalDateTime createdAt, String content, Long userId, String name, String avatar,List<ReviewCriterialDto> criterialDtos) {
        this.reviewId = reviewId;
        this.rate = rate;
        this.createdAt = createdAt;
        this.content = content;
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
        this.criterialDtos = criterialDtos;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ReviewCriterialDto> getCriterialDtos() {
        return criterialDtos;
    }

    public void setCriterialDtos(List<ReviewCriterialDto> criterialDtos) {
        this.criterialDtos = criterialDtos;
    }
}
