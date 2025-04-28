package vn.anhkhoa.projectwebsitebantailieu.model;

import java.time.LocalDateTime;

import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;

public class NotificationDto {
    private Long notiId;
    private Long userId;
    private String title;
    private String content;
    private NotificationType type;
    private LocalDateTime createdAt;
    private boolean isRead;

    public NotificationDto(){}

    public NotificationDto(Long notiId,String title, String content, NotificationType type, LocalDateTime createdAt, boolean isRead) {
        this.notiId = notiId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public NotificationDto(Long notiId, Long serverId,String title, String content, NotificationType type, LocalDateTime createdAt, boolean isRead) {
        this.notiId = notiId;
        this.userId = serverId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getNotiId() {
        return notiId;
    }

    public void setNotiId(Long notiId) {
        this.notiId = notiId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setServerId(Long serverId) {
        this.userId = serverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
