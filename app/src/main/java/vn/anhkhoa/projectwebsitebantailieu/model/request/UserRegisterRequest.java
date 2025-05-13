package vn.anhkhoa.projectwebsitebantailieu.model.request;

import java.io.Serializable;
import java.time.LocalDate;

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
    private String address;
    private String gender;
    private LocalDate dob;
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

    public UserRegisterRequest(String name, String email, String password, String avatar, Role role, Boolean isActive, String phoneNumber, String address, String gender, LocalDate dob, AccountType type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.isActive = isActive;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.dob = dob;
        this.type = type;
    }

    public UserRegisterRequest(String name, String email, String avatar, String phoneNumber, String address, String gender, LocalDate dob) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.dob = dob;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
