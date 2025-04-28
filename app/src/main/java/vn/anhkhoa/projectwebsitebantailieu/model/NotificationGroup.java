package vn.anhkhoa.projectwebsitebantailieu.model;

import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;

public class NotificationGroup {
    private NotificationType type;
    private List<NotificationDto> items;
    public NotificationGroup(NotificationType type, List<NotificationDto> items) {
        this.type = type;
        this.items = items;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public List<NotificationDto> getItems() {
        return items;
    }

    public void setItems(List<NotificationDto> items) {
        this.items = items;
    }
}
