package vn.anhkhoa.projectwebsitebantailieu.model.response;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private long userId;
    private String name;
    private String email;
    private String avatar;

    public UserResponse(long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
