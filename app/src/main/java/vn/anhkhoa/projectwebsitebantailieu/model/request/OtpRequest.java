package vn.anhkhoa.projectwebsitebantailieu.model.request;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OtpRequest implements Serializable {
    private String email;
    private String otp;
    private LocalDateTime otpExpired;
    private Boolean isActive;

    public OtpRequest(){}
    public OtpRequest(String email, String otp){
        this.email = email;
        this.otp = otp;
    }

    public OtpRequest(String email, String otp, LocalDateTime otpExpired, Boolean isActive) {
        this.email = email;
        this.otp = otp;
        this.otpExpired = otpExpired;
        this.isActive = isActive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpExpired() {
        return otpExpired;
    }

    public void setOtpExpired(LocalDateTime otpExpired) {
        this.otpExpired = otpExpired;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
