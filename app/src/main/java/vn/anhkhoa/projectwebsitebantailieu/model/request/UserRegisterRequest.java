package vn.anhkhoa.projectwebsitebantailieu.model.request;

import java.io.Serializable;

import vn.anhkhoa.projectwebsitebantailieu.enums.AccountType;
import vn.anhkhoa.projectwebsitebantailieu.enums.Role;

public class UserRegisterRequest implements Serializable {
    private String name;
    private String email;
    private String password;
    private String avatar;
    private Role role;
    private Boolean isActive;
    private String phoneNumber;
    private AccountType type;
    public UserRegisterRequest() {
    }

    public UserRegisterRequest( String name, String email, String password,String avatar, Role role, Boolean isActive, String phoneNumber, AccountType type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.isActive = isActive;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
