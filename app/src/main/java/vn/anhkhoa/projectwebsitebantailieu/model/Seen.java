package vn.anhkhoa.projectwebsitebantailieu.model;

public class Seen {
    private Long seenId;
    private Long userId;
    private Long chatLineId;
    private String seenAt;

    // Constructor rỗng
    public Seen() {}

    // Constructor đầy đủ
    public Seen(Long seenId, Long userId, Long chatLineId, String seenAt) {
        this.seenId = seenId;
        this.userId = userId;
        this.chatLineId = chatLineId;
        this.seenAt = seenAt;
    }

    // Getter và Setter
    public Long getSeenId() {
        return seenId;
    }

    public void setSeenId(Long seenId) {
        this.seenId = seenId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatLineId() {
        return chatLineId;
    }

    public void setChatLineId(Long chatLineId) {
        this.chatLineId = chatLineId;
    }

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }
}
